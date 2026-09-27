package uut.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.projectile.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityTameable;
import java.util.List;

public class EntityTankMissle extends EntityThrowable {

    public EntityTankMissle(World worldIn) {
        super(worldIn);
    }

    public EntityTankMissle(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityTankMissle(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote) {
            for (int i = 0; i < 2; ++i) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                        this.posX - this.motionX * 0.5D,
                        this.posY - this.motionY * 0.5D,
                        this.posZ - this.motionZ * 0.5D,
                        -this.motionX * 0.2, 0.1D, -this.motionZ * 0.2);
            }
            this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            if (this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos) == Block.NULL_AABB) {
                return;
            }
        }

        if (!this.world.isRemote) {
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 2.0F, 1.5F);

            float radio = 3.0F;
            List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this,
                    new AxisAlignedBB(this.posX - radio, this.posY - radio, this.posZ - radio,
                            this.posX + radio, this.posY + radio, this.posZ + radio));

            Entity thrower = this.getThrower();

            for (Entity entity : entities) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase target = (EntityLivingBase) entity;

                    if (target == thrower) continue;

                    if (target instanceof EntityTameable) {
                        EntityTameable targetPet = (EntityTameable) target;
                        if (targetPet.isTamed()) {
                            if (thrower instanceof EntityLivingBase && targetPet.isOwner((EntityLivingBase) thrower)) continue;
                            if (thrower instanceof EntityTameable && ((EntityTameable) thrower).isTamed()) {
                                java.util.UUID ownerTarget = targetPet.getOwnerId();
                                java.util.UUID ownerThrower = ((EntityTameable) thrower).getOwnerId();
                                if (ownerTarget != null && ownerTarget.equals(ownerThrower)) continue;
                            }
                        }
                    }
                    if (thrower instanceof EntityTameable && ((EntityTameable) thrower).isTamed()) {
                        if (target == ((EntityTameable) thrower).getOwner()) continue;
                    }

                    target.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 12.0F);

                    double d0 = target.posX - this.posX;
                    double d1 = target.posZ - this.posZ;
                    target.addVelocity(d0 * 0.25, 0.2, d1 * 0.25);
                }
            }

            this.world.setEntityState(this, (byte)3);
            this.setDead();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 20; ++i) {
                double speedX = this.rand.nextGaussian() * 0.15D;
                double speedY = this.rand.nextGaussian() * 0.15D;
                double speedZ = this.rand.nextGaussian() * 0.15D;

                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, speedX, speedY, speedZ);
            }
        }
    }
}