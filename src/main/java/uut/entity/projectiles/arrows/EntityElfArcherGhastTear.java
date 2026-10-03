package uut.entity.projectiles.arrows;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityElfArcherGhastTear extends EntityTippedArrow {

    private double extraDamageMultiplier = 1.0D;

    private final List<Entity> piercedEntities = new ArrayList<>();

    private int maxPierceCount = 5;

    public EntityElfArcherGhastTear(World worldIn) {
        super(worldIn);
        this.initArrowProperties();
    }

    public EntityElfArcherGhastTear(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.initArrowProperties();
    }

    public EntityElfArcherGhastTear(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        this.initArrowProperties();
    }

    private void initArrowProperties() {
        // Remove this.setFire(100)
        this.setDamage(1.0D + this.extraDamageMultiplier);
        this.pickupStatus = EntityTippedArrow.PickupStatus.DISALLOWED;
    }

    public void setExtraDamageMultiplier(double extraDamage) {
        this.extraDamageMultiplier = extraDamage;
        this.setDamage(1.0D + extraDamage);
    }

    public void setMaxPierceCount(int pierceCount) {
        this.maxPierceCount = pierceCount;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote && !this.inGround) {
            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX, this.posY, this.posZ, 1.0D, 1.0D, 1.0D);
            this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        if (this.isInWater() || this.isWet() || this.world.getBlockState(this.getPosition()).getMaterial() == Material.WATER) {
            if (!this.world.isRemote) {
                this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.setDead();
            } else {
                for (int i = 0; i < 8; ++i) {
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D), this.posY + (this.rand.nextDouble() - 0.5D), this.posZ + (this.rand.nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        Entity target = raytraceResultIn.entityHit;

        if (target != null) {
            if (this.piercedEntities.contains(target)) {
                return;
            }

            this.piercedEntities.add(target);

            float velocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            int finalDamage = MathHelper.ceil((double) velocity * this.getDamage());

            if (this.getIsCritical()) {
                finalDamage += this.rand.nextInt(finalDamage / 2 + 2);
            }

            DamageSource damagesource = (this.shootingEntity == null)
                    ? DamageSource.causeArrowDamage(this, this)
                    : DamageSource.causeArrowDamage(this, this.shootingEntity);

            if (target.attackEntityFrom(damagesource, (float) finalDamage)) {
                if (target instanceof EntityLivingBase) {
                    EntityLivingBase livingTarget = (EntityLivingBase) target;
                    this.arrowHit(livingTarget);
                }
            }

            if (!this.world.isRemote && this.rand.nextBoolean()) {
                this.createCustomExplosion();
            }

            if (this.piercedEntities.size() < this.maxPierceCount) {
                return;
            }
        } else {
            if (!this.world.isRemote && this.rand.nextBoolean()) {
                this.createCustomExplosion();
            }
        }

        super.onHit(raytraceResultIn);
    }

    private void createCustomExplosion() {
        float radius = 2.5F;
        float explosionDamage = 6.0F;

        this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 1.0F, 1.2F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);

        if (this.world instanceof net.minecraft.world.WorldServer) {
            ((net.minecraft.world.WorldServer) this.world).spawnParticle(
                    EnumParticleTypes.EXPLOSION_LARGE,
                    this.posX, this.posY, this.posZ,
                    1, 0.0D, 0.0D, 0.0D, 0.0D
            );
        }

        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - radius, this.posY - radius, this.posZ - radius,
                this.posX + radius, this.posY + radius, this.posZ + radius
        );

        List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        DamageSource explosionSource = DamageSource.causeExplosionDamage((EntityLivingBase) null);

        for (EntityLivingBase entity : targets) {
            if (this.isFriendlyOrShooter(entity)) {
                continue;
            }

            double distance = entity.getDistance(this.posX, this.posY, this.posZ);
            if (distance <= radius) {
                float damageFactor = (float) (1.0D - (distance / radius));
                entity.attackEntityFrom(explosionSource, explosionDamage * damageFactor);
            }
        }
    }

    private boolean isFriendlyOrShooter(Entity target) {
        if (this.shootingEntity == null) {
            return false;
        }

        if (target.isEntityEqual(this.shootingEntity)) {
            return true;
        }

        if (this.shootingEntity.isOnSameTeam(target)) {
            return true;
        }

        if (this.shootingEntity instanceof IEntityOwnable) {
            IEntityOwnable ownable = (IEntityOwnable) this.shootingEntity;
            if (ownable.getOwner() != null && ownable.getOwner().equals(target)) {
                return true;
            }
        }

        if (this.shootingEntity instanceof EntityTameable) {
            EntityTameable tameable = (EntityTameable) this.shootingEntity;
            if (tameable.isTamed() && tameable.getOwner() != null) {
                if (tameable.getOwner().equals(target)) {
                    return true;
                }
                if (target instanceof EntityTameable) {
                    EntityTameable targetTameable = (EntityTameable) target;
                    if (targetTameable.isTamed() && tameable.getOwner().equals(targetTameable.getOwner())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }
}
