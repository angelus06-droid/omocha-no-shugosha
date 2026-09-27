package uut.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import uut.item.ItemToyAttackBooster;
import uut.item.ItemToyDefenseBooster;
import uut.item.ItemToyHeartBooster;
import uut.item.ItemToySpeedBooster;

public class BaseDefensiveMob extends EntityTameable implements IToyMob {

    private int heartBoostCount = 0;
    private int defenseBoostCount = 0;
    private int attackBoostCount = 0;
    private int speedBoostCount = 0;

    public BaseDefensiveMob(World worldIn) {
        super(worldIn);
        this.experienceValue = 5;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null) {
            this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        }
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!stack.isEmpty()) {

            if (stack.getItem() instanceof ItemToyHeartBooster) {
                if (this.heartBoostCount < 20) {
                    if (!this.world.isRemote) {
                        this.heartBoostCount++;
                        double currentMaxHealth = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(currentMaxHealth + 1.0D);
                        this.heal(2.0F);

                        if (!player.capabilities.isCreativeMode) {
                            stack.shrink(1);
                        }
                        this.world.setEntityState(this, (byte) 100);
                    }
                    return true;
                }
            }

            else if (stack.getItem() instanceof ItemToyDefenseBooster) {
                if (this.defenseBoostCount < 10) {
                    if (!this.world.isRemote) {
                        this.defenseBoostCount++;
                        double currentArmor = this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue();
                        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(currentArmor + 1.0D);

                        if (!player.capabilities.isCreativeMode) {
                            stack.shrink(1);
                        }
                        this.world.setEntityState(this, (byte) 101);
                    }
                    return true;
                }
            }

            else if (stack.getItem() instanceof ItemToyAttackBooster) {
                if (this.attackBoostCount < 5) {
                    if (!this.world.isRemote) {
                        this.attackBoostCount++;

                        IAttributeInstance attackAttr = this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                        if (attackAttr != null) {
                            attackAttr.setBaseValue(attackAttr.getBaseValue() + 1.0D);
                        }

                        if (!player.capabilities.isCreativeMode) {
                            stack.shrink(1);
                        }
                        this.world.setEntityState(this, (byte) 102);
                    }
                    return true;
                }
            }

            else if (stack.getItem() instanceof ItemToySpeedBooster) {
                if (this.speedBoostCount < 5) {
                    if (!this.world.isRemote) {
                        this.speedBoostCount++;

                        IAttributeInstance speedAttr = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                        if (speedAttr != null) {
                            speedAttr.setBaseValue(speedAttr.getBaseValue() + 0.01D);
                        }

                        if (!player.capabilities.isCreativeMode) {
                            stack.shrink(1);
                        }
                        this.world.setEntityState(this, (byte) 103);
                    }
                    return true;
                }
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 100) {
            spawnBoostParticles(EnumParticleTypes.HEART);
        } else if (id == 101) {
            spawnBoostParticles(EnumParticleTypes.CRIT_MAGIC);
        } else if (id == 102) {
            spawnBoostParticles(EnumParticleTypes.VILLAGER_ANGRY);
        } else if (id == 103) {
            spawnBoostParticles(EnumParticleTypes.SPELL_INSTANT);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public int getHeartBoostCount() {
        return this.heartBoostCount;
    }

    public int getDefenseBoostCount() {
        return this.defenseBoostCount;
    }

    public int getAttackBoostCount() {
        return this.attackBoostCount;
    }

    public float getRangedAttackBonus() {
        return this.attackBoostCount * 1.0F;
    }

    public int getSpeedBoostCount() {
        return this.speedBoostCount;
    }

    private void spawnBoostParticles(EnumParticleTypes particle) {
        for (int i = 0; i < 5; ++i) {
            this.world.spawnParticle(particle,
                    this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
                    this.posY + 0.5D + (this.rand.nextDouble() * this.height),
                    this.posZ + (this.rand.nextDouble() - 0.5D) * this.width, 0, 0, 0);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("HeartBoostCount", this.heartBoostCount);
        compound.setInteger("DefenseBoostCount", this.defenseBoostCount);
        compound.setInteger("AttackBoostCount", this.attackBoostCount);
        compound.setInteger("SpeedBoostCount", this.speedBoostCount);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("HeartBoostCount")) {
            this.heartBoostCount = compound.getInteger("HeartBoostCount");
        }
        if (compound.hasKey("DefenseBoostCount")) {
            this.defenseBoostCount = compound.getInteger("DefenseBoostCount");
        }
        if (compound.hasKey("AttackBoostCount")) {
            this.attackBoostCount = compound.getInteger("AttackBoostCount");
        }
        if (compound.hasKey("SpeedBoostCount")) {
            this.speedBoostCount = compound.getInteger("SpeedBoostCount");
        }
    }

    @Override
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        if (entitylivingbaseIn instanceof IToyMob) {
            return;
        }
        super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity attacker = source.getTrueSource();
        if (attacker instanceof IToyMob) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }
}