package uut.entity.projectiles;

import net.minecraft.block.Block; // IMPORTANTE
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks; // IMPORTANTE
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessShooter;

public class EntityShooterBullet extends EntityThrowable {
    private float damage;

    public EntityShooterBullet(final World world) {
        super(world);
        this.damage = 4.0f;
        this.setSize(0.3f, 0.3f);
    }

    public EntityShooterBullet(final World worldIn, final EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.damage = 4.0f;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.015f;
    }

    public void onUpdate() {
        super.onUpdate();
        final float horizontalDistance = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232);
        this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)horizontalDistance) * 57.29577951308232);

        if (this.world.isRemote) {
            int dirtId = Block.getStateId(Blocks.DIRT.getDefaultState());

            for (int i = 0; i < 3; ++i) {
                final double px = this.posX + (this.rand.nextDouble() - 0.5) * 0.2;
                final double py = this.posY + (this.rand.nextDouble() - 0.5) * 0.2;
                final double pz = this.posZ + (this.rand.nextDouble() - 0.5) * 0.2;

                this.world.spawnParticle(EnumParticleTypes.FALLING_DUST, px, py, pz, 0.0, 0.0, 0.0, new int[]{dirtId});
            }
        }
        if (this.ticksExisted > 200) {
            this.setDead();
        }
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            this.rotationPitch = 90.0f;
            this.prevRotationPitch = 90.0f;
        }
    }

    protected void onImpact(final RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            final IBlockState state = this.world.getBlockState(result.getBlockPos());
            if (state.getCollisionBoundingBox((IBlockAccess)this.world, result.getBlockPos()) == null) {
                return;
            }
        }
        if (result.entityHit != null) {
            final Entity target = result.entityHit;
            final EntityLivingBase shooter = this.getThrower();
            if (shooter != null) {
                if (target == shooter) {
                    return;
                }
                if (shooter instanceof EntityToyPrincessShooter) {
                    final EntityToyPrincessShooter toy = (EntityToyPrincessShooter)shooter;
                    final EntityLivingBase owner = toy.getOwner();
                    if (owner != null && target == owner) {
                        this.setDead();
                        return;
                    }
                    if (target instanceof IEntityOwnable) {
                        final IEntityOwnable ownableTarget = (IEntityOwnable)target;
                        if (owner != null && owner.getUniqueID().equals(ownableTarget.getOwnerId())) {
                            this.setDead();
                            return;
                        }
                    }
                }
            }

            if (target instanceof EntityLivingBase) {
                ((EntityLivingBase) target).hurtResistantTime = 0;
            }

            target.attackEntityFrom(DamageSource.causeThrownDamage((Entity)this, (Entity)shooter), this.damage);
        }
        if (!this.world.isRemote) {
            this.setDead();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte id) {
        if (id == 3) {
            int dirtId = Block.getStateId(Blocks.DIRT.getDefaultState());
            for (int i = 0; i < 15; ++i) {
                double motionX = (this.rand.nextDouble() - 0.5) * 0.2;
                double motionY = this.rand.nextDouble() * 0.2;
                double motionZ = (this.rand.nextDouble() - 0.5) * 0.2;

                this.world.spawnParticle(EnumParticleTypes.FALLING_DUST, this.posX, this.posY, this.posZ, motionX, motionY, motionZ, new int[]{dirtId});
            }
        }
    }

    public void setDamage(final double damageIn) {
        this.damage = (float)damageIn;
    }

    public static void registerFixesToyBullet(final DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ToyBullet");
    }
}