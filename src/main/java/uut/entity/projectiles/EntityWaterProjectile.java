package uut.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityWaterProjectile extends EntityThrowable {

    private static final float WATER_DAMAGE = 2.5F;

    public EntityWaterProjectile(World worldIn) {
        super(worldIn);
    }

    public EntityWaterProjectile(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.015F;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {

            boolean shouldDie = true;

            if (result.entityHit != null) {
                if (result.entityHit != this.getThrower()) {

                    float damage = WATER_DAMAGE;

                    if (result.entityHit instanceof EntityEnderman || result.entityHit.isImmuneToFire()) {
                        damage *= 2.0F;
                    }

                    DamageSource source = DamageSource.causeThrownDamage(this, this.getThrower());
                    result.entityHit.attackEntityFrom(source, damage);

                    result.entityHit.extinguish();
                }
            } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState state = this.world.getBlockState(result.getBlockPos());
                AxisAlignedBB collisionBox = state.getCollisionBoundingBox(this.world, result.getBlockPos());

                if (collisionBox == null) {
                    shouldDie = false;
                }
            }

            if (shouldDie) {
                this.extinguishNearbyFire();
                this.setDead();
            }
        }

        if (this.world.isRemote) {
            for (int i = 0; i < 8; ++i) {
                this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH,
                        this.posX, this.posY, this.posZ,
                        (this.rand.nextDouble() - 0.5D) * 0.3D,
                        this.rand.nextDouble() * 0.2D,
                        (this.rand.nextDouble() - 0.5D) * 0.3D);
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.WATER_DROP,
                    this.posX, this.posY, this.posZ,
                    0, 0, 0);
        } else if (this.isInWater() && !this.isDead) {
            this.extinguishNearbyFire();
            this.setDead();
        }
    }

    private void extinguishNearbyFire() {
        BlockPos center = new BlockPos(this);
        BlockPos min = center.add(-1, -1, -1);
        BlockPos max = center.add(1, 1, 1);

        for (BlockPos pos : BlockPos.getAllInBoxMutable(min, max)) {
            if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
                this.world.setBlockToAir(pos);
                this.world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                        SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}