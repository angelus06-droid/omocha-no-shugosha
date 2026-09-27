package uut.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import uut.entity.EntityToySpaceship;

public class EntityAISpaceshipFollowOwner extends EntityAIBase {
    private final EntityToySpaceship spaceship;
    private EntityLivingBase theOwner;
    private final World theWorld;
    private final double followSpeed;
    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;
    private final float maxDist;
    private final float minDist;
    private float oldWaterCost;

    public EntityAISpaceshipFollowOwner(EntityToySpaceship spaceshipIn, double followSpeedIn, float minDistIn, float maxDistIn) {
        this.spaceship = spaceshipIn;
        this.theWorld = spaceshipIn.getEntityWorld();
        this.followSpeed = followSpeedIn;
        this.petPathfinder = spaceshipIn.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(spaceshipIn.getNavigator() instanceof PathNavigateFlying)) {
            throw new IllegalArgumentException("Requires PathNavigateFlying");
        }
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase owner = this.spaceship.getOwner();

        if (owner == null) return false;
        if (owner instanceof EntityPlayer && ((EntityPlayer) owner).isSpectator()) return false;

        if (this.spaceship.getAttackTarget() != null) return false;

        if (this.spaceship.getDistanceSq(owner) < (double) (this.minDist * this.minDist)) {
            return false;
        }

        this.theOwner = owner;
        return true;
    }

    public boolean continueExecuting() {
        if (this.spaceship.getAttackTarget() != null) return false;

        return !this.petPathfinder.noPath() &&
                this.spaceship.getDistanceSq(this.theOwner) > (double) (this.maxDist * this.maxDist);
    }

    @Override
    public void updateTask() {
        this.spaceship.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.spaceship.getVerticalFaceSpeed());

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;

            if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed)) {
                if (!this.spaceship.getLeashed() && this.spaceship.getDistanceSq(this.theOwner) >= 144.0D) {
                    this.tryTeleport();
                }
            }
        }
    }

    private void tryTeleport() {
        int i = MathHelper.floor(this.theOwner.posX) - 2;
        int j = MathHelper.floor(this.theOwner.posZ) - 2;
        int k = MathHelper.floor(this.theOwner.getEntityBoundingBox().minY);

        for (int l = 0; l <= 4; ++l) {
            for (int i2 = 0; i2 <= 4; ++i2) {
                if ((l < 1 || i2 < 1 || l > 3 || i2 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i2)) {
                    this.spaceship.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k + 2.0D, (double) ((float) (j + i2) + 0.5F), this.spaceship.rotationYaw, this.spaceship.rotationPitch);
                    this.petPathfinder.clearPath();
                    return;
                }
            }
        }
    }

    protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
        BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
        IBlockState iblockstate = this.theWorld.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape((IBlockAccess) this.theWorld, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID &&
                iblockstate.canEntitySpawn(this.spaceship) &&
                this.theWorld.isAirBlock(blockpos.up()) &&
                this.theWorld.isAirBlock(blockpos.up(2));
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.spaceship.getPathPriority(PathNodeType.WATER);
        this.spaceship.setPathPriority(PathNodeType.WATER, 0.0f);
    }

    @Override
    public void resetTask() {
        this.theOwner = null;
        this.petPathfinder.clearPath();
        this.spaceship.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }
}