package uut.entity;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityToyArrowTower extends BaseTurretMob implements IRangedAttackMob {
    private static final DataParameter<ItemStack> EQUIPPED_ARROW = EntityDataManager.createKey(EntityToyArrowTower.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> AMMO_COUNT = EntityDataManager.createKey(EntityToyArrowTower.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_INFINITE = EntityDataManager.createKey(EntityToyArrowTower.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyArrowTower.class, DataSerializers.VARINT);

    private int healTimer;

    public EntityToyArrowTower(World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 0.95f);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(EQUIPPED_ARROW, ItemStack.EMPTY);
        this.dataManager.register(AMMO_COUNT, 0);
        this.dataManager.register(IS_INFINITE, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.25D, 20, 30.0f));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, true, false, target -> target instanceof IMob));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.isPotionActive(MobEffects.POISON)) {
            this.removePotionEffect(MobEffects.POISON);
        }
        if (!this.world.isRemote) {
            EntityLivingBase target = this.getAttackTarget();
            if (target != null) {
                double distSq = this.getDistanceSq(target);
                if (distSq > 16.0D * 16.0D && !this.getEntitySenses().canSee(target)) {
                    this.getNavigator().clearPath();
                    this.rotationYaw = this.prevRotationYaw;
                    this.renderYawOffset = this.rotationYaw;
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
        if (this.world.isRemote && this.isInfinite() && this.ticksExisted % 5 == 0) {
            for (int i = 0; i < 2; ++i) {
                this.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.1D, 0.0D);
            }
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (this.isOwner(player)) {
            if (!heldItem.isEmpty() && heldItem.getItem().getRegistryName() != null) {
                if (heldItem.getItem().getRegistryName().getPath().equals("toy_capsule")) {
                    return false;
                }
            }

            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye) {
                EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(heldItem.getMetadata());
                if (enumdyecolor != this.getCollarColor()) {
                    this.setCollarColor(enumdyecolor);
                    if (!player.capabilities.isCreativeMode) {
                        heldItem.shrink(1);
                    }
                    this.playSound(SoundEvents.BLOCK_CLOTH_BREAK, 1.0F, 1.25F);
                    return true;
                }
            }

            if (heldItem.getItem() == Items.ENCHANTED_BOOK) {
                if (!this.world.isRemote) {
                    if (!this.isInfinite()) {
                        this.setInfinite(true);
                        this.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
                        if (!player.capabilities.isCreativeMode) heldItem.shrink(1);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.toy_tower.infinite_on"), true);
                    } else {
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.toy_tower.infinite_have"), true);
                    }
                }
                return true;
            }

            if (isArrow(heldItem)) {
                if (!this.world.isRemote) {
                    ItemStack currentType = this.getEquippedArrow();

                    if (!currentType.isEmpty() && (!ItemStack.areItemsEqual(currentType, heldItem) || !ItemStack.areItemStackTagsEqual(currentType, heldItem))) {
                        this.dropStoredAmmo(player, hand);
                    }

                    int canAccept = 64 - this.getAmmo();
                    int toAdd = Math.min(canAccept, heldItem.getCount());

                    if (toAdd > 0) {
                        ItemStack stackToSave = heldItem.copy();
                        stackToSave.setCount(1);
                        this.setEquippedArrow(stackToSave);
                        this.addAmmo(toAdd);

                        this.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, 1.0F, 1.0F + ((float)this.getAmmo() / 64.0F));

                        if (!player.capabilities.isCreativeMode) {
                            heldItem.shrink(toAdd);
                        }

                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.toy_tower.status", this.getAmmo(), 64, stackToSave.getDisplayName()), true);
                    }
                }
                return true;
            }

            if (heldItem.isEmpty()) {
                if (!this.world.isRemote) {
                    if (this.getAmmo() > 0) {
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.toy_tower.status", this.getAmmo(), 64, this.getEquippedArrow().getDisplayName()), true);
                    } else {
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.toy_tower.empty"), true);
                    }
                }
                return true;
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (target == null || !target.isEntityAlive()) {
            this.setAttackTarget(null);
            return;
        }

        if (this.getAmmo() <= 0 && !this.isInfinite()) {
            this.playSound(SoundEvents.BLOCK_DISPENSER_FAIL, 1.0F, 1.2F);
            return;
        }

        ItemStack arrowStack = this.getEquippedArrow();

        if (arrowStack.isEmpty() && this.isInfinite()) {
            arrowStack = new ItemStack(Items.ARROW);
        } else if (arrowStack.isEmpty()) {
            return;
        }

        ItemArrow itemarrow = (ItemArrow)(arrowStack.getItem() instanceof ItemArrow ? arrowStack.getItem() : Items.ARROW);

        String registryName = arrowStack.getItem().getRegistryName() != null ? arrowStack.getItem().getRegistryName().toString() : "";
        boolean isTriple = registryName.contains("triple_arrow");
        int shots = isTriple ? 3 : 1;

        double spawnX = this.posX;
        double spawnY = this.posY + this.getEyeHeight() - 0.1D;
        double spawnZ = this.posZ;

        double d0 = target.posX - spawnX;
        double d1 = (target.getEntityBoundingBox().minY + (double)(target.height / 2.0F)) - spawnY;
        double d2 = target.posZ - spawnZ;
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);

        double gravityCompensation = d3 * 0.115D;

        float yawToTarget = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float pitchToTarget = (float)(-(MathHelper.atan2(d1 + gravityCompensation, d3) * (180D / Math.PI)));

        float velocity = 2.5F;
        float inaccuracy = 0.5F;

        double attackBoostBonus = this.getRangedAttackBonus();

        for (int i = 0; i < shots; i++) {
            EntityArrow entityarrow = itemarrow.createArrow(this.world, arrowStack, this);

            entityarrow.setPosition(spawnX, spawnY, spawnZ);

            float yawOffset = 0.0F;
            if (isTriple) {
                if (i == 1) yawOffset = -8.0F;
                if (i == 2) yawOffset = 8.0F;
            }

            entityarrow.shoot(this, pitchToTarget, yawToTarget + yawOffset, 0.0F, velocity, inaccuracy);
            entityarrow.setIsCritical(true);

            if (registryName.contains("diamond")) entityarrow.setDamage(entityarrow.getDamage() + 2.0D);
            if (registryName.contains("reinforced")) entityarrow.setDamage(entityarrow.getDamage() + 3.0D);

            if (attackBoostBonus > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + attackBoostBonus);
            }

            if (this.world.isRemote && this.isInfinite()) {
                for (int p = 0; p < 3; ++p) {
                    this.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entityarrow.posX + (this.rand.nextDouble() - 0.5D) * 0.5D, entityarrow.posY, entityarrow.posZ + (this.rand.nextDouble() - 0.5D) * 0.5D, 0.0D, 0.0D, 0.0D);
                }
            }

            if (!this.world.isRemote) {
                this.world.spawnEntity(entityarrow);
            }
        }

        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));

        if (!this.world.isRemote && !this.isInfinite()) {
            this.consumeAmmo(1);
        }
    }

    private void dropStoredAmmo(EntityPlayer player, EnumHand hand) {
        if (this.getAmmo() > 0 && !this.getEquippedArrow().isEmpty()) {
            ItemStack toReturn = this.getEquippedArrow().copy();
            toReturn.setCount(this.getAmmo());

            if (!player.capabilities.isCreativeMode) {
                ItemStack heldItem = player.getHeldItem(hand);

                if (heldItem.isEmpty()) {
                    player.setHeldItem(hand, toReturn);
                } else {
                    if (!player.inventory.addItemStackToInventory(toReturn)) {
                        this.entityDropItem(toReturn, 0.5f);
                    }
                }
            }

            this.setAmmo(0);
            this.setEquippedArrow(ItemStack.EMPTY);
        }
    }

    private boolean isArrow(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ItemArrow;
    }

    public void setAmmo(int amount) {
        this.dataManager.set(AMMO_COUNT, amount);
    }
    public void addAmmo(int amount) {
        this.setAmmo(Math.min(64, this.getAmmo() + amount));
    }
    public void consumeAmmo(int amount) {
        this.setAmmo(Math.max(0, this.getAmmo() - amount));
        if (this.getAmmo() <= 0) this.setEquippedArrow(ItemStack.EMPTY);
    }
    public int getAmmo() { return this.dataManager.get(AMMO_COUNT); }

    public void setEquippedArrow(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        this.dataManager.set(EQUIPPED_ARROW, copy);
    }
    public ItemStack getEquippedArrow() { return this.dataManager.get(EQUIPPED_ARROW); }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    public void setInfinite(boolean value) {
        this.dataManager.set(IS_INFINITE, value);
    }
    public boolean isInfinite() {
        return this.dataManager.get(IS_INFINITE);
    }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("IsInfinite", this.isInfinite());
        if (!this.getEquippedArrow().isEmpty()) {
            compound.setTag("EquippedArrow", this.getEquippedArrow().writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("AmmoCount", this.getAmmo());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setInfinite(compound.getBoolean("IsInfinite"));
        if (compound.hasKey("EquippedArrow", 10)) {
            this.setEquippedArrow(new ItemStack(compound.getCompoundTag("EquippedArrow")));
        }
        this.setAmmo(compound.getInteger("AmmoCount"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!this.world.isRemote && this.getAmmo() > 0 && !this.getEquippedArrow().isEmpty()) {
            ItemStack toDrop = this.getEquippedArrow().copy();
            toDrop.setCount(this.getAmmo());
            this.entityDropItem(toDrop, 0.5f);

            this.setAmmo(0);
            this.setEquippedArrow(ItemStack.EMPTY);
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {}

    public boolean isPotionApplicable(PotionEffect pot) {
        if (pot.getPotion() == MobEffects.POISON) {
            return false;
        }
        return super.isPotionApplicable(pot);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.MAGIC && this.isPotionActive(MobEffects.POISON)) {
            return false;
        }
        if ("poison".equals(source.damageType)) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }
}