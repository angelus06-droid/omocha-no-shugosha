package uut.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityTechnoDemonRocket extends EntityThrowable {

    public EntityTechnoDemonRocket(World worldIn) {
        super(worldIn);
    }

    public EntityTechnoDemonRocket(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityTechnoDemonRocket(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.01F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote) {
            for (int i = 0; i < 1; ++i) {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                        this.posX - this.motionX * 0.5D,
                        this.posY - this.motionY * 0.5D,
                        this.posZ - this.motionZ * 0.5D,
                        -this.motionX * 0.2, 0.1D, -this.motionZ * 0.2);
            }
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

            float radio = 2.0F;
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

                    target.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 6.0F);

                    double d0 = target.posX - this.posX;
                    double d1 = target.posZ - this.posZ;

                    target.addVelocity(d0 * 0.18, 0.10, d1 * 0.18);
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
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 20; ++i) {
                double speedX = this.rand.nextGaussian() * 0.15D;
                double speedY = this.rand.nextGaussian() * 0.15D;
                double speedZ = this.rand.nextGaussian() * 0.15D;

                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, speedX, speedY, speedZ);
            }
        }
    }
}