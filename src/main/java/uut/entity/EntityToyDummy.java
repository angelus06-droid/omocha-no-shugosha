package uut.entity;

import net.minecraft.world.*;
import net.minecraft.entity.ai.*;
import net.minecraft.util.datafix.*;
import net.minecraft.entity.*;
import javax.annotation.*;
import com.google.common.base.*;
import net.minecraft.entity.monster.*;
import java.util.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityToyDummy extends BaseDefensiveMob {
    private int healTimer;
    public EntityToyDummy(World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 1.4f);
    }

    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    public static void registerFixesToyDummy(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityToyDummy.class);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote && this.rand.nextInt(10) == 0) {
            AxisAlignedBB areaBusqueda = this.getEntityBoundingBox().grow(20.0, 10.0, 20.0);

            List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, areaBusqueda, EntitySelectors.IS_ALIVE);

            for (int j = 0; j < 5 && !list.isEmpty(); ++j) {
                EntityLivingBase entitylivingbase = list.get(this.rand.nextInt(list.size()));

                if (entitylivingbase != this && entitylivingbase instanceof EntityMob) {
                    ((EntityMob)entitylivingbase).setAttackTarget(this);
                }
            }
        }
        if (!this.world.isRemote) {
            if (this.isEntityAlive() && this.getHealth() < this.getMaxHealth()) {
                this.healTimer++;

                if (this.healTimer >= 80) {
                    this.heal(1.0F);
                    this.healTimer = 0;
                }
            } else {
                this.healTimer = 0;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles(EnumParticleTypes particleType) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(particleType,
                    this.posX + (this.rand.nextFloat() * this.width * 2.0F) - this.width,
                    this.posY + 1.0D + (this.rand.nextFloat() * this.height),
                    this.posZ + (this.rand.nextFloat() * this.width * 2.0F) - this.width,
                    d0, d1, d2);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_CLOTH_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_CLOTH_BREAK;
    }
}