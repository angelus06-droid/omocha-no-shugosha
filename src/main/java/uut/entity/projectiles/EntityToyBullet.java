package uut.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.projectile.*;
import net.minecraft.world.*;
import net.minecraft.util.datafix.*;
import net.minecraft.init.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

public class EntityToyBullet extends EntityThrowable {

    private float damage = 5.5f;

    public EntityToyBullet(World worldIn) {
        super(worldIn);
    }

    public EntityToyBullet(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 4.0F, 0.5F);
    }

    public EntityToyBullet(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public void setDamage(double damageIn) {
        this.damage = (float)damageIn;
    }
    @Override
    protected float getGravityVelocity() {
        return 0.04f;
    }
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.isInWater() && !this.onGround) {
            this.motionX *= 1.008D;
            this.motionY *= 1.008D;
            this.motionZ *= 1.008D;
        }
    }

    public static void registerFixesToyBullet(DataFixer fixer) {
        EntityThrowable.registerFixesThrowable(fixer, "ToyBullet");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 12; ++i) {
                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                        this.posX, this.posY, this.posZ,
                        (this.rand.nextDouble() - 0.5D) * 0.2D,
                        (this.rand.nextDouble() - 0.5D) * 0.2D,
                        (this.rand.nextDouble() - 0.5D) * 0.2D);
            }
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {

            if (result.entityHit != null) {
                Entity entity = result.entityHit;
                EntityLivingBase thrower = this.getThrower();

                if (thrower != null && (entity == thrower || entity.isRidingSameEntity(thrower))) {
                    this.processImpactEffects();
                    return;
                }

                if (entity instanceof EntityTameable) {
                    EntityTameable tameable = (EntityTameable) entity;
                    if (tameable.isTamed() && thrower != null) {
                        if (tameable.isOwner(thrower)) {
                            this.processImpactEffects();
                            return;
                        }
                        if (thrower instanceof EntityTameable && ((EntityTameable) thrower).isTamed()) {
                            if (tameable.getOwnerId() != null && tameable.getOwnerId().equals(((EntityTameable) thrower).getOwnerId())) {
                                this.processImpactEffects();
                                return;
                            }
                        }
                    }
                }

                if (thrower != null && entity.getClass() == thrower.getClass()) {
                    if (!(thrower instanceof EntityPlayer)) {
                        this.processImpactEffects();
                        return;
                    }
                }

                if (entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).hurtResistantTime = 0;
                }

                entity.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), this.damage);
                this.processImpactEffects();
                return;
            }

            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = result.getBlockPos();
                if (this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos) == Block.NULL_AABB) {
                    return;
                }
            }

            this.processImpactEffects();
        }
    }

    private void processImpactEffects() {
        this.world.playSound(null, this.posX, this.posY, this.posZ,
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.NEUTRAL, 0.5f, 1.5f + this.rand.nextFloat() * 0.3f);

        this.world.setEntityState(this, (byte)3);
        this.setDead();
    }
}