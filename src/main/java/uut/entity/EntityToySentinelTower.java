package uut.entity;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.datafix.*;
import javax.annotation.*;
import net.minecraft.util.*;
import uut.entity.projectiles.*;
import net.minecraft.entity.*;
import net.minecraft.potion.*;
import net.minecraft.init.*;
import uut.util.ModSounds;

public class EntityToySentinelTower extends BaseTurretMob implements IRangedAttackMob {
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToySentinelTower.class, DataSerializers.VARINT);
    private int attackTimer;
    private int healTimer;

    public EntityToySentinelTower(final World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 1.05f);
        this.isImmuneToFire = true;
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.YELLOW.getDyeDamage());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 50, 39.0f));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, target -> target instanceof IMob));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
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
    public void handleStatusUpdate(final byte id) {
        if (id == 4) {
            this.attackTimer = 10;
        }
        else {
            super.handleStatusUpdate(id);
        }
    }

    public static void registerFixesIronGolem(final DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityToySentinelTower.class);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackTimer > 0) {
            --this.attackTimer;
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
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (this.isOwner(player)) {
            if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemDye) {
                EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(itemstack.getMetadata());

                if (enumdyecolor != this.getCollarColor()) {
                    this.setCollarColor(enumdyecolor);

                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.25F);
                    return true;
                }
            }
        }
        return super.processInteract(player, hand);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_STONE_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_STONE_BREAK;
    }

    @Override
    public void attackEntityWithRangedAttack(final EntityLivingBase target, final float distanceFactor) {
        if (target != null && target.isEntityAlive()) {
            final EntitySentinelBullet missile = new EntitySentinelBullet(this.world, this);

            float calculatedDamage = 6.0F + this.getRangedAttackBonus();
            missile.setCustomDamage(calculatedDamage);

            double d0 = target.posX - this.posX;
            double d1 = target.getEntityBoundingBox().minY - missile.posY;
            double d2 = target.posZ - this.posZ;
            double d3 = (double)Math.sqrt(d0 * d0 + d2 * d2);

            missile.shoot(d0, d1 + d3 * 0.1D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
            missile.target = target;

            this.playSound(ModSounds.sentinel_tower_shoot, 1.0f, 1.0f);

            this.world.setEntityState(this, (byte)4);

            missile.setLocationAndAngles(this.posX, this.posY + this.getEyeHeight(), this.posZ, this.rotationYaw, 0.0f);
            this.world.spawnEntity(missile);
        } else {
            this.setAttackTarget(null);
        }
    }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override
    public boolean isPotionApplicable(final PotionEffect potioneffectIn) {
        return potioneffectIn.getPotion() != MobEffects.POISON && super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
    }
}