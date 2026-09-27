package uut.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGunnerGunBullet extends EntityThrowable {

    private float damage = 5.5F;

    public EntityGunnerGunBullet(World worldIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
    }

    public EntityGunnerGunBullet(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        this.setSize(0.25F, 0.25F);
        this.shoot(throwerIn, throwerIn.rotationPitch, throwerIn.rotationYaw, 0.0F, 3.5F, 0.2F);
    }

    public EntityGunnerGunBullet(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setSize(0.25F, 0.25F);
    }

    public void setDamage(double damageIn) {
        this.damage = (float) damageIn;
    }

    public float getDamage() {
        return this.damage;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.005F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.world.isRemote && !this.inGround) {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
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
                        (this.rand.nextDouble() - 0.5D) * 0.3D,
                        (this.rand.nextDouble() - 0.5D) * 0.3D,
                        (this.rand.nextDouble() - 0.5D) * 0.3D);
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
                    return;
                }

                if (entity instanceof EntityLivingBase) {
                    ((EntityLivingBase) entity).hurtResistantTime = 0;
                }

                DamageSource source = thrower != null ? DamageSource.causeThrownDamage(this, thrower) : DamageSource.GENERIC;
                entity.attackEntityFrom(source, this.damage);

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
                SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.NEUTRAL, 0.5F, 1.5F + this.rand.nextFloat() * 0.3F);

        this.world.setEntityState(this, (byte) 3);
        this.setDead();
    }
}