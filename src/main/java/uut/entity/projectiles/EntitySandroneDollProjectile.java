package uut.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.particle.ModParticles;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EntitySandroneDollProjectile extends EntityThrowable {

    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntitySandroneDollProjectile.class, DataSerializers.VARINT);

    public EntityLivingBase target;
    public float speedMultiplier = 0.5f;


    private double wanderDirX = 0;
    private double wanderDirY = 0;
    private double wanderDirZ = 0;

    private float damage = 2.5F;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public EntitySandroneDollProjectile(World worldIn) {
        super(worldIn);
        this.setSize(0.2F, 0.2F);
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
    }

    public EntitySandroneDollProjectile(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.setSize(0.35F, 0.35F);
    }

    public EntitySandroneDollProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setSize(0.35F, 0.35F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.updateSmoothRotation();

        if (this.world.isRemote) {
            double centerX = this.posX;
            double centerY = this.posY + (this.height / 2.0F);
            double centerZ = this.posZ;

            for (int i = 0; i < 2; i++) {
                double offsetX = (this.rand.nextDouble() - 0.5D) * this.width;
                double offsetY = (this.rand.nextDouble() - 0.5D) * this.height;
                double offsetZ = (this.rand.nextDouble() - 0.5D) * this.width;

                ModParticles.spawnSandroneDollMissile(
                        this.world,
                        centerX + offsetX,
                        centerY + offsetY,
                        centerZ + offsetZ,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                );
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
                double dY = (this.target.getEntityBoundingBox().minY + (double)(this.target.height / 2.0F)) - (this.posY + (double)(this.height / 2.0F));
                double dZ = this.target.posZ - this.posZ;

                double dist = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);

                if (dist > 1.0E-4D) {
                    dX /= dist;
                    dY /= dist;
                    dZ /= dist;

                    double turnRate = 0.15D;
                    this.motionX += (dX * this.speedMultiplier - this.motionX) * turnRate;
                    this.motionY += (dY * this.speedMultiplier - this.motionY) * turnRate;
                    this.motionZ += (dZ * this.speedMultiplier - this.motionZ) * turnRate;
                }
            } else {
                updateWanderMovement();
            }
        }
    }

    private void updateWanderMovement() {
        if (wanderDirX == 0 && wanderDirY == 0 && wanderDirZ == 0) {
            this.wanderDirX = this.motionX;
            this.wanderDirY = this.motionY;
            this.wanderDirZ = this.motionZ;

            if (Math.abs(wanderDirX) < 0.01 && Math.abs(wanderDirY) < 0.01 && Math.abs(wanderDirZ) < 0.01) {
                this.wanderDirX = (this.rand.nextDouble() - 0.5D);
                this.wanderDirY = (this.rand.nextDouble() - 0.5D) * 0.2D;
                this.wanderDirZ = (this.rand.nextDouble() - 0.5D);
            }
        }

        this.wanderDirX += (this.rand.nextDouble() - 0.5D) * 0.1D;
        this.wanderDirY += (this.rand.nextDouble() - 0.5D) * 0.05D;
        this.wanderDirZ += (this.rand.nextDouble() - 0.5D) * 0.1D;

        double dist = MathHelper.sqrt(wanderDirX * wanderDirX + wanderDirY * wanderDirY + wanderDirZ * wanderDirZ);
        if (dist > 1.0E-4D) {
            double dirX = wanderDirX / dist;
            double dirY = wanderDirY / dist;
            double dirZ = wanderDirZ / dist;

            double turnRate = 0.08D;
            this.motionX += (dirX * this.speedMultiplier - this.motionX) * turnRate;
            this.motionY += (dirY * this.speedMultiplier - this.motionY) * turnRate;
            this.motionZ += (dirZ * this.speedMultiplier - this.motionZ) * turnRate;
        }
    }

    private void updateSmoothRotation() {
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        double horizDist = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (horizDist < 1.0E-4D && Math.abs(this.motionY) < 1.0E-4D) {
            return;
        }

        float targetYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
        float targetPitch = (float) (MathHelper.atan2(this.motionY, horizDist) * (180D / Math.PI));

        this.rotationYaw = clampAngle(this.rotationYaw, targetYaw, 30.0F);
        this.rotationPitch = clampAngle(this.rotationPitch, targetPitch, 30.0F);
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
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.HOSTILE, 0.8F, 2.5F);

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
                            UUID ownerTarget = targetPet.getOwnerId();
                            UUID ownerThrower = ((EntityTameable) thrower).getOwnerId();

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

                targetEnt.hurtResistantTime = 0;

                targetEnt.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), this.damage);
            }
        }

        this.world.setEntityState(this, (byte)3);
        this.setDead();
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null) {
                if (result.entityHit == this.getThrower() || result.entityHit instanceof EntitySandroneDollProjectile) {
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