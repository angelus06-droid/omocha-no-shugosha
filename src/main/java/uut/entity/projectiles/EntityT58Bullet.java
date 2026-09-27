package uut.entity.projectiles;

import net.minecraft.entity.projectile.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import uut.entity.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.block.state.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.datafix.*;

public class EntityT58Bullet extends EntityThrowable {
    private float damage;

    public EntityT58Bullet(final World world) {
        super(world);
        this.damage = 2.5f;
        this.setSize(0.0625f, 0.0625f);
    }

    public EntityT58Bullet(final World worldIn, final EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.damage = 2.5f;
    }

    protected float getGravityVelocity() {
        return 0.0f;
    }

    public void onUpdate() {
        super.onUpdate();
        final float horizontalDistance = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232);
        this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)horizontalDistance) * 57.29577951308232);
        if (this.world.isRemote) {
            final float red = 0.42745098f;
            final float green = 0.81960785f;
            final float blue = 1.0f;
            for (int i = 0; i < 5; ++i) {
                final double px = this.posX + (this.rand.nextDouble() - 0.5) * 0.2;
                final double py = this.posY + (this.rand.nextDouble() - 0.5) * 0.2;
                final double pz = this.posZ + (this.rand.nextDouble() - 0.5) * 0.2;
                this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, (double)red, (double)green, (double)blue, new int[0]);
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

            if (target instanceof EntityLivingBase) {
                ((EntityLivingBase) target).hurtResistantTime = 0;
            }

            if (shooter != null) {
                if (target == shooter) {
                    return;
                }
                if (shooter instanceof EntityToyT58) {
                    final EntityToyT58 toy = (EntityToyT58)shooter;
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
            target.attackEntityFrom(DamageSource.causeThrownDamage((Entity)this, (Entity)shooter), this.damage);
        }
        if (!this.world.isRemote) {
            this.setDead();
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(final byte id) {
        if (id == 3) {
            for (int i = 0; i < 10; ++i) {
                this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0, 0.6, 1.0, new int[0]);
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
