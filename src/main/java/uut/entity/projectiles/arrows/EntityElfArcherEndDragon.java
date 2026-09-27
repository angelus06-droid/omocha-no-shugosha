package uut.entity.projectiles.arrows;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityArrow;
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

import java.util.ArrayList;
import java.util.List;

public class EntityElfArcherEndDragon extends EntityArrow {

    private double extraDamageMultiplier = 1.0D;
    private final List<Entity> piercedEntities = new ArrayList<>();
    private int maxPierceCount = 5;
    private boolean spawnedCloud = false;

    public EntityElfArcherEndDragon(World worldIn) {
        super(worldIn);
        this.initArrowProperties();
    }

    public EntityElfArcherEndDragon(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.initArrowProperties();
    }

    public EntityElfArcherEndDragon(World worldIn, EntityLivingBase shooter) {
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

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote && !this.inGround) {
            this.world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, this.posX, this.posY, this.posZ,
                    (this.rand.nextDouble() - 0.5D) * 0.1D, -0.05D, (this.rand.nextDouble() - 0.5D) * 0.1D);
            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX, this.posY, this.posZ, 0.5D, 0.0D, 0.5D);
        }

        if (this.inGround && !this.spawnedCloud) {
            if (!this.world.isRemote) {
                this.spawnDragonBreathCloud();
            }
            this.spawnedCloud = true;
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

    private void spawnDragonBreathCloud() {
        EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
        cloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
        cloud.setRadius(2.5F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(200);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());

        cloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 0));

        if (this.shootingEntity instanceof EntityLivingBase) {
            cloud.setOwner((EntityLivingBase) this.shootingEntity);
        }

        this.world.spawnEntity(cloud);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        Entity target = raytraceResultIn.entityHit;

        if (target != null) {
            if (this.piercedEntities.contains(target)) {
                return;
            }

            if (this.shouldAvoidDamage(target)) {
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

            // Generar la nube de humo si la flecha alcanza el límite de penetración (pierce)
            if (this.piercedEntities.size() >= this.maxPierceCount) {
                if (!this.world.isRemote && !this.spawnedCloud) {
                    this.spawnDragonBreathCloud();
                    this.spawnedCloud = true;
                }
                super.onHit(raytraceResultIn);
                return;
            }
        } else {
            // Si impactó contra un bloque sólido
            if (!this.world.isRemote && !this.spawnedCloud) {
                this.spawnDragonBreathCloud();
                this.spawnedCloud = true;
            }
            super.onHit(raytraceResultIn);
        }
    }

    private boolean shouldAvoidDamage(Entity target) {
        if (target.getClass().getSimpleName().equals("EntityToyElfArcher")) {
            return true;
        }

        if (this.shootingEntity != null) {
            if (target == this.shootingEntity) {
                return true;
            }

            if (this.shootingEntity instanceof EntityTameable) {
                EntityTameable shooterTamed = (EntityTameable) this.shootingEntity;

                if (shooterTamed.isTamed()) {
                    EntityLivingBase owner = shooterTamed.getOwner();

                    if (target == owner) {
                        return true;
                    }

                    if (target instanceof EntityTameable) {
                        EntityTameable targetTamed = (EntityTameable) target;
                        if (targetTamed.isTamed() && targetTamed.getOwner() == owner) {
                            return true;
                        }
                    }

                    return target.isOnSameTeam(shooterTamed) || (owner != null && target.isOnSameTeam(owner));
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