package uut.entity.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.projectile.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.monster.*;
import net.minecraft.network.datasync.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.SoundEvents;

public class EntitySentinelBullet extends EntityThrowable {

    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntitySentinelBullet.class, DataSerializers.VARINT);

    public EntityLivingBase target;
    public float speedMultiplier = 0.5f;
    private double perfectMotionX;
    private double perfectMotionY;
    private double perfectMotionZ;

    private float customDamage = 6.0F;

    public EntitySentinelBullet(World worldIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
    }

    public EntitySentinelBullet(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.setSize(0.25F, 0.25F);
    }

    public EntitySentinelBullet(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setSize(0.25F, 0.25F);
    }

    public void setCustomDamage(float damage) {
        this.customDamage = damage;
    }

    public float getCustomDamage() {
        return this.customDamage;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.15F, this.posZ, 0.0D, 0.0D, 0.0D);

            if (this.ticksExisted % 2 == 0) {
                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY + 0.15F, this.posZ, -this.motionX * 0.2D, -this.motionY * 0.2D, -this.motionZ * 0.2D);
            }
        } else {
            if (this.ticksExisted > 200) {
                this.executeCustomExplosion();
                return;
            }

            if (this.target == null || this.target.isDead || !this.target.isEntityAlive()) {
                this.findNewTarget();
            }

            if (this.target != null) {
                double dX = this.target.posX - this.posX;
                double dY = (this.target.getEntityBoundingBox().minY + (double)(this.target.height / 2.0F)) - this.posY;
                double dZ = this.target.posZ - this.posZ;

                this.calculatePerfectHeading(dX, dY, dZ, this.speedMultiplier, 1.0f);

                double accel = 0.08;
                this.motionX = interpolateMotion(this.motionX, this.perfectMotionX, accel);
                this.motionY = interpolateMotion(this.motionY, this.perfectMotionY, accel);
                this.motionZ = interpolateMotion(this.motionZ, this.perfectMotionZ, accel);
            } else {
                this.motionX += (this.rand.nextDouble() - 0.5D) * 0.05D;
                this.motionY += (this.rand.nextDouble() - 0.5D) * 0.05D;
                this.motionZ += (this.rand.nextDouble() - 0.5D) * 0.05D;
            }

            this.updateSmoothRotation();
        }
    }

    private void updateSmoothRotation() {
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        double horizDist = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (horizDist < 1.0E-4 && Math.abs(this.motionY) < 1.0E-4) {
            return;
        }

        float targetYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
        float targetPitch = (float) (MathHelper.atan2(this.motionY, horizDist) * (180D / Math.PI));

        float maxTurnPerTick = 20.0F;

        this.rotationYaw = clampAngle(this.rotationYaw, targetYaw, maxTurnPerTick);
        this.rotationPitch = clampAngle(this.rotationPitch, targetPitch, maxTurnPerTick);
    }

    private float clampAngle(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        delta = MathHelper.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    private void findNewTarget() {
        AxisAlignedBB area = this.getEntityBoundingBox().grow(20.0, 10.0, 20.0);
        List<EntityLivingBase> candidates = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);

        EntityLivingBase closest = null;
        double minDistance = Double.MAX_VALUE;

        for (EntityLivingBase entity : candidates) {
            if (entity != this.getThrower() && !Objects.equals(entity, this) && entity.isEntityAlive()) {
                if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null) continue;

                if (entity instanceof IMob) {
                    double distSq = this.getDistanceSq(entity);
                    if (distSq < minDistance) {
                        minDistance = distSq;
                        closest = entity;
                    }
                }
            }
        }
        this.target = closest;
    }

    private void executeCustomExplosion() {
        if (this.world.isRemote) return;

        this.world.playSound(null, this.posX, this.posY, this.posZ,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 1.0F, 2.5F);

        float radio = 2.0F;
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this,
                new AxisAlignedBB(this.posX - radio, this.posY - radio, this.posZ - radio,
                        this.posX + radio, this.posY + radio, this.posZ + radio));

        Entity thrower = this.getThrower();

        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase targetEnt = (EntityLivingBase) entity;

                if (targetEnt == thrower) continue;

                if (targetEnt instanceof EntityTameable) {
                    EntityTameable targetPet = (EntityTameable) targetEnt;

                    if (targetPet.isTamed()) {
                        if (thrower instanceof EntityLivingBase && targetPet.isOwner((EntityLivingBase) thrower)) {
                            continue;
                        }

                        if (thrower instanceof EntityTameable && ((EntityTameable) thrower).isTamed()) {
                            java.util.UUID ownerTarget = targetPet.getOwnerId();
                            java.util.UUID ownerThrower = ((EntityTameable) thrower).getOwnerId();

                            if (ownerTarget != null && ownerTarget.equals(ownerThrower)) {
                                continue;
                            }
                        }
                    }
                }

                if (thrower instanceof EntityTameable && ((EntityTameable) thrower).isTamed()) {
                    if (targetEnt == ((EntityTameable) thrower).getOwner()) {
                        continue;
                    }
                }

                targetEnt.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), this.customDamage);

                double d0 = targetEnt.posX - this.posX;
                double d1 = targetEnt.posZ - this.posZ;
                targetEnt.addVelocity(d0 * 0.25, 0.2, d1 * 0.25);
            }
        }

        this.world.setEntityState(this, (byte)3);
        this.setDead();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null) {
                if (result.entityHit == this.getThrower() || result.entityHit instanceof EntitySentinelBullet) {
                    return;
                }
            }

            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = result.getBlockPos();
                IBlockState state = this.world.getBlockState(pos);
                if (state.getCollisionBoundingBox(this.world, pos) == null ||
                        state.getMaterial().isReplaceable()) {
                    return;
                }
            }
            this.executeCustomExplosion();
        }
    }

    private double interpolateMotion(double current, double target, double step) {
        if (current < target) return Math.min(current + step, target);
        else if (current > target) return Math.max(current - step, target);
        return target;
    }

    public void calculatePerfectHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x /= f; y /= f; z /= f;
        x += this.rand.nextGaussian() * 0.0075 * inaccuracy;
        y += this.rand.nextGaussian() * 0.0075 * inaccuracy;
        z += this.rand.nextGaussian() * 0.0075 * inaccuracy;

        this.perfectMotionX = x * velocity;
        this.perfectMotionY = y * velocity;
        this.perfectMotionZ = z * velocity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int i = 0; i < 20; ++i) {
                double speedX = (this.rand.nextDouble() - 0.5D) * 0.5D;
                double speedY = (this.rand.nextDouble() - 0.5D) * 0.5D;
                double speedZ = (this.rand.nextDouble() - 0.5D) * 0.5D;

                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, speedX, speedY, speedZ);
            }
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }
}