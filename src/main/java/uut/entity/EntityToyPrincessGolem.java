package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityToyPrincessGolem extends BaseSummonableMob {

    private int attackTimer;
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyPrincessGolem.class, DataSerializers.BOOLEAN);

    private int healTimer;
    private int lifeTimer;
    private int maxLifeTicks;

    public EntityToyPrincessGolem(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 0.9f);
        this.maxLifeTicks = 600 + this.rand.nextInt(401);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SITTING, false);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.1D, false) {
            @Override
            public boolean shouldExecute() {
                return !isSitting() && super.shouldExecute();
            }
        });

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {
            if (entity == null || isSitting() || !entity.isEntityAlive()) return false;
            return entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker;
        }));
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        return super.processInteract(player, hand);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            if (this.isEntityAlive() && this.getHealth() <= 4.0F) {
                this.world.setEntityState(this, (byte) 84);
                this.setDead();
                return;
            }

            this.lifeTimer++;
            if (this.lifeTimer >= this.maxLifeTicks) {
                this.world.setEntityState(this, (byte) 84);
                this.setDead();
                return;
            }
        }

        if (this.attackTimer > 0) --this.attackTimer;

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

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) {
        this.dataManager.set(SITTING, sitting);
        if (sitting) {
            this.setAttackTarget(null);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("sitting", this.isSitting());
        compound.setInteger("LifeTimer", this.lifeTimer);
        compound.setInteger("MaxLifeTicks", this.maxLifeTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("LifeTimer")) {
            this.lifeTimer = compound.getInteger("LifeTimer");
        }
        if (compound.hasKey("MaxLifeTicks")) {
            this.maxLifeTicks = compound.getInteger("MaxLifeTicks");
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }

    @Override
    public boolean attackEntityAsMob(final Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte)4);

        entityIn.hurtResistantTime = 0;

        float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if (flag) {
            this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.4F);
            if (entityIn instanceof EntityLivingBase) {
                double deltaX = this.posX - entityIn.posX;
                double deltaZ = this.posZ - entityIn.posZ;

                while (deltaX * deltaX + deltaZ * deltaZ < 1.0E-4D) {
                    deltaX = (Math.random() - Math.random()) * 0.01D;
                    deltaZ = (Math.random() - Math.random()) * 0.01D;
                }

                ((EntityLivingBase)entityIn).knockBack(this, 0.1F, deltaX, deltaZ);

                this.applyEnchantments(this, entityIn);
            }
        }
        return flag;
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
        } else if (id == 84) {
            for (int i = 0; i < 20; ++i) {
                double px = this.posX + (this.rand.nextFloat() - 0.5D) * this.width;
                double py = this.posY + this.rand.nextFloat() * this.height;
                double pz = this.posZ + (this.rand.nextFloat() - 0.5D) * this.width;
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public CombatTracker getCombatTracker() {
        return new CombatTracker(this) {
            @Override
            public ITextComponent getDeathMessage() {
                return null;
            }
        };
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() { return this.attackTimer; }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.ENTITY_IRONGOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRONGOLEM_DEATH;
    }

    @Override
    protected float getSoundPitch() {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.6F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 0.1F, 1.5F);
    }
}