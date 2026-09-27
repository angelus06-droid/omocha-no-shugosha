package uut.entity.projectiles.arrows;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import uut.entity.EntityToyElfArcher;

import java.util.ArrayList;
import java.util.List;

public class EntityElfArcherWitherRose extends EntityTippedArrow {

    private double extraDamageMultiplier = 1.0D;
    private final List<Entity> piercedEntities = new ArrayList<>();
    private int maxPierceCount = 5;

    public EntityElfArcherWitherRose(World worldIn) {
        super(worldIn);
        this.initArrowProperties();
    }

    public EntityElfArcherWitherRose(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.initArrowProperties();
    }

    public EntityElfArcherWitherRose(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        this.initArrowProperties();
    }

    private void initArrowProperties() {
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

    private boolean shouldIgnoreTarget(Entity target) {
        if (target == null) return true;

        if (this.shootingEntity != null && target == this.shootingEntity) {
            return true;
        }

        if (target instanceof EntityToyElfArcher && this.shootingEntity instanceof EntityToyElfArcher) {
            if (target == this.shootingEntity) return true;
        }

        if (this.shootingEntity instanceof EntityTameable) {
            EntityTameable shooterTameable = (EntityTameable) this.shootingEntity;

            if (shooterTameable.isTamed()) {
                EntityLivingBase owner = shooterTameable.getOwner();

                if (owner != null && target == owner) {
                    return true;
                }

                if (target instanceof EntityTameable) {
                    EntityTameable targetTameable = (EntityTameable) target;
                    if (targetTameable.isTamed() && owner != null && owner.equals(targetTameable.getOwner())) {
                        return true;
                    }
                }
            }
        }

        if (this.shootingEntity != null && this.shootingEntity.isOnSameTeam(target)) {
            return true;
        }

        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote && !this.inGround) {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            this.world.spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
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
            if (shouldIgnoreTarget(target) || this.piercedEntities.contains(target)) {
                return;
            }

            this.piercedEntities.add(target);

            if (target instanceof EntityLivingBase) {
                EntityLivingBase livingTarget = (EntityLivingBase) target;
                livingTarget.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 1));
            }

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

            if (this.piercedEntities.size() < this.maxPierceCount) {
                return;
            }
        }

        super.onHit(raytraceResultIn);
    }

    @Override
    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }
}