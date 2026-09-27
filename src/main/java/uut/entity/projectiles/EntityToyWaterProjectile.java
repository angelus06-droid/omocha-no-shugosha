package uut.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
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
import uut.entity.BaseDefensiveMob;

public class EntityToyWaterProjectile extends EntityThrowable {

    private float customDamage = 2.5F;

    public EntityToyWaterProjectile(World worldIn) {
        super(worldIn);
    }

    public EntityToyWaterProjectile(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public void setDamage(double damage) {
        this.customDamage = (float) damage;
    }

    public float getDamage() {
        return this.customDamage;
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
                EntityLivingBase thrower = this.getThrower();

                if (result.entityHit != thrower && !isFriendly(result.entityHit, thrower)) {

                    float damage = this.customDamage;

                    if (result.entityHit instanceof EntityEnderman || result.entityHit.isImmuneToFire()) {
                        damage *= 2.0F;
                    }

                    DamageSource source = DamageSource.causeThrownDamage(this, thrower);
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
    }

    private boolean isFriendly(net.minecraft.entity.Entity target, EntityLivingBase shooter) {
        if (shooter == null || target == null) return false;

        if (shooter instanceof BaseDefensiveMob) {
            BaseDefensiveMob defensiveMob = (BaseDefensiveMob) shooter;
            EntityLivingBase owner = defensiveMob.getOwner();

            if (owner != null) {
                if (target == owner) return true;

                if (target instanceof BaseDefensiveMob) {
                    BaseDefensiveMob otherMob = (BaseDefensiveMob) target;
                    if (otherMob.getOwner() == owner) return true;
                }

                if (target instanceof EntityTameable) {
                    EntityTameable tameable = (EntityTameable) target;
                    if (tameable.getOwner() == owner) return true;
                }
            }
        }

        if (shooter instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) shooter;

            if (target instanceof BaseDefensiveMob) {
                BaseDefensiveMob targetMob = (BaseDefensiveMob) target;
                if (targetMob.getOwner() == player) return true;
            }

            if (target instanceof EntityTameable) {
                EntityTameable tameable = (EntityTameable) target;
                if (tameable.getOwner() == player) return true;
            }
        }

        return false;
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