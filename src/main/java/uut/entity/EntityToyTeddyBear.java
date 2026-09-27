package uut.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.*;
import net.minecraft.inventory.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import javax.annotation.*;
import net.minecraft.entity.monster.*;
import net.minecraft.network.datasync.*;
import net.minecraft.util.text.TextComponentTranslation;
import uut.entity.ai.EntityAITeddyBearRangedBow;
import uut.item.ModItems;

public class EntityToyTeddyBear extends BaseDefensiveMob implements IRangedAttackMob {

    private int attackTimer;
    public final EntityAITeddyBearRangedBow aiArrowAttack;
    public final EntityAIAttackMelee aiAttackOnCollide;

    private static final DataParameter<Integer> BEAR_TYPE = EntityDataManager.createKey(EntityToyTeddyBear.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FIREARMOR = EntityDataManager.createKey(EntityToyTeddyBear.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyTeddyBear.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyTeddyBear.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyTeddyBear.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntityToyTeddyBear.class, DataSerializers.BOOLEAN);

    private int healTimer;
    private int shieldActiveTicks;
    private int shieldCooldownTimer;
    private static final int SHIELD_COOLDOWN = 8;

    public EntityToyTeddyBear(World worldIn) {
        super(worldIn);
        this.aiArrowAttack = new EntityAITeddyBearRangedBow(this, 1.0D, 35, 15.0F);
        this.aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, false);
        this.setSize(0.6F, 0.9F);
        this.setCombatTask();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(BEAR_TYPE, 1);
        this.dataManager.register(FIREARMOR, false);
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
        this.dataManager.register(SWINGING_ARMS, false);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.15D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(2, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {if (entity == null || isSitting() || !entity.isEntityAlive()) return false;return entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker;}
        ));
    }

    private void teleportToOwner(EntityLivingBase owner) {
        for (int i = 0; i < 10; ++i) {
            int offsetX = this.rand.nextInt(7) - 3;
            int offsetZ = this.rand.nextInt(7) - 3;

            BlockPos targetPos = new BlockPos(owner.posX + offsetX, owner.getEntityBoundingBox().minY, owner.posZ + offsetZ);

            if (this.isValidTeleportPos(targetPos)) {
                this.setLocationAndAngles((double)targetPos.getX() + 0.5D, (double)targetPos.getY(), (double)targetPos.getZ() + 0.5D, this.rotationYaw, this.rotationPitch);
                this.getNavigator().clearPath();
                return;
            }
        }
    }

    private boolean isValidTeleportPos(BlockPos pos) {
        return this.world.getBlockState(pos.down()).isSideSolid(this.world, pos.down(), EnumFacing.UP) &&
                this.isSafeToStandAt(pos) &&
                this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid() && !state.getMaterial().isLiquid();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isOwner(player)) {
            if (itemstack.getItem() == ModItems.toy_command_staff) {
                if (!this.world.isRemote) {
                    if (isFollowing()) {
                        setFollowing(false);
                        setSitting(true);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.sit"), true);
                    } else if (isSitting()) {
                        setFollowing(false);
                        setSitting(false);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.free"), true);
                    } else {
                        setFollowing(true);
                        setSitting(false);
                        player.sendStatusMessage(new TextComponentTranslation("uut.msg.mode.follow"), true);
                    }
                }

                if (this.world.isRemote) {
                    if (this.isSitting()) {
                        this.spawnColoredParticle(EnumParticleTypes.SPELL_MOB, 0.0F, 0.0F, 1.0F);
                    } else if (this.isFollowing()) {
                        this.spawnColoredParticle(EnumParticleTypes.SPELL_MOB, 1.0F, 0.0F, 0.0F);
                    } else {
                        this.spawnColoredParticle(EnumParticleTypes.SPELL_MOB, 0.0F, 1.0F, 0.0F);
                    }
                }

                this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                return true;
            }

            boolean isBlacklisted = itemstack.getItem() == ModItems.gunner_gun || itemstack.getItem() == ModItems.watergun;

            boolean isWeapon = (itemstack.getItem() instanceof ItemSword || itemstack.getItem() instanceof ItemBow || itemstack.getItem() instanceof ItemTool) && !isBlacklisted;
            boolean isShield = itemstack.getItem() instanceof ItemShield && !isBlacklisted;
            boolean isHandEmpty = itemstack.isEmpty();

            if (hand == EnumHand.MAIN_HAND && (isWeapon || isShield || isHandEmpty)) {
                EntityEquipmentSlot slot = isShield ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
                ItemStack bearOldItem = this.getItemStackFromSlot(slot);

                if (isHandEmpty && bearOldItem.isEmpty()) {
                    return super.processInteract(player, hand);
                }

                if (!this.world.isRemote) {
                    ItemStack playerItemCopy = itemstack.copy();
                    if (!playerItemCopy.isEmpty()) {
                        playerItemCopy.setCount(1);
                    }
                    this.setItemStackToSlot(slot, playerItemCopy);

                    if (player.capabilities.isCreativeMode) {
                        if (!bearOldItem.isEmpty()) {
                            player.setHeldItem(hand, bearOldItem);
                        }
                    } else {
                        player.setHeldItem(hand, bearOldItem);
                    }

                    if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                    }

                    if (slot == EntityEquipmentSlot.MAINHAND) {
                        this.setCombatTask();
                    }
                }

                if (!itemstack.isEmpty() || !bearOldItem.isEmpty()) {
                    this.playSound(isShield ? SoundEvents.ENTITY_ITEM_BREAK : SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);
                }
                return true;
            }


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

            if (!itemstack.isEmpty() && itemstack.getItem() == Items.BLAZE_POWDER) {
                if (!this.getArmored()) {
                    if (!this.world.isRemote) {
                        this.setArmored(true);
                        if (!player.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }
                        this.world.setEntityState(this, (byte) 45);
                    }
                    this.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.25F);
                    return true;
                }
            }
        }
        return super.processInteract(player, hand);
    }

    private void spawnColoredParticle(EnumParticleTypes type, float red, float green, float blue) {
        for (int i = 0; i < 10; ++i) {
            double px = this.posX + (this.rand.nextFloat() - 0.5D) * this.width;
            double py = this.posY + this.rand.nextFloat() * this.height;
            double pz = this.posZ + (this.rand.nextFloat() - 0.5D) * this.width;

            this.world.spawnParticle(type, px, py, pz, (double)red, (double)green, (double)blue);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        ItemStack stack = this.getHeldItemMainhand();
        int knockingModifier = 0;

        if (!stack.isEmpty() && target instanceof EntityLivingBase) {
            damage += EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase)target).getCreatureAttribute());
            knockingModifier += EnchantmentHelper.getKnockbackModifier(this);
        }

        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);

        DamageSource source = DamageSource.causeMobDamage(this);


        boolean flag = target.attackEntityFrom(source, damage);

        if (flag) {
            if (knockingModifier > 0) {
                ((EntityLivingBase)target).knockBack(this, (float)knockingModifier * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int fireAspectLevel = EnchantmentHelper.getFireAspectModifier(this);
            if (fireAspectLevel > 0) {
                target.setFire(fireAspectLevel * 4);
            }

            if (!stack.isEmpty()) {
                if (target instanceof EntityLivingBase) {
                    stack.getItem().hitEntity(stack, (EntityLivingBase) target, this);
                }

                int sweepingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack);
                if (sweepingLevel > 0 || (stack.getItem().getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack).containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName()) && this.rand.nextFloat() < 0.3F)) {
                    this.performSweepingAttack(target, damage, sweepingLevel);
                }

                stack.damageItem(1, this);
            }

            this.applyEnchantments(this, target);
        }

        this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_PLAYER_ATTACK_WEAK : SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
        return flag;
    }

    private void performSweepingAttack(Entity mainTarget, float damage, int sweepingLevel) {
        float sweepModifier = sweepingLevel > 0 ? (float)sweepingLevel / ((float)sweepingLevel + 1.0F) : 0.5F;
        float sweepDamage = 1.0F + damage * sweepModifier;

        List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, mainTarget.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D));

        for (EntityLivingBase entity : targets) {
            if (entity != this && entity != mainTarget && !this.isOnSameTeam(entity)) {
                if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null && ((IEntityOwnable) entity).getOwnerId().equals(this.getOwnerId())) continue;
                if (this.getDistanceSq(entity) >= 9.0D) continue;
                entity.knockBack(this, 0.4F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), sweepDamage);
            }
        }
        this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
        if (this.world instanceof WorldServer) {
            ((WorldServer) this.world).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, this.posX, this.posY + this.height * 0.5D, this.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        ItemStack shield = this.getHeldItemOffhand();

        if (!this.isHandActive() && this.shieldCooldownTimer <= 0
                && !shield.isEmpty() && shield.getItem() instanceof ItemShield
                && !source.isUnblockable() && this.rand.nextFloat() < 0.6F) {
            this.setActiveHand(EnumHand.OFF_HAND);
            this.shieldActiveTicks = 20;
        }

        boolean isBlockingNow = this.isHandActive()
                && this.getActiveHand() == EnumHand.OFF_HAND
                && !shield.isEmpty()
                && shield.getItem() instanceof ItemShield;

        if (isBlockingNow && !source.isUnblockable()) {
            shield.damageItem((int) amount, this);
            this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
            this.resetActiveHand();
            this.shieldActiveTicks = 0;
            this.shieldCooldownTimer = SHIELD_COOLDOWN;
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    private void updateShieldBlocking() {
        ItemStack offhand = this.getHeldItemOffhand();
        boolean hasShield = !offhand.isEmpty() && offhand.getItem() instanceof ItemShield;

        if (!hasShield) {
            if (this.isHandActive() && this.getActiveHand() == EnumHand.OFF_HAND) {
                this.resetActiveHand();
            }
            return;
        }

        if (this.isHandActive() && this.getActiveHand() == EnumHand.OFF_HAND) {
            if (--this.shieldActiveTicks <= 0) {
                this.resetActiveHand();
                this.shieldCooldownTimer = SHIELD_COOLDOWN;
            }
            return;
        }

        if (this.shieldCooldownTimer > 0) {
            --this.shieldCooldownTimer;
            return;
        }

        EntityLivingBase target = this.getAttackTarget();
        if (target != null && this.getDistanceSq(target) <= 25.0D && this.rand.nextInt(6) == 0) {
            this.setActiveHand(EnumHand.OFF_HAND);
            this.shieldActiveTicks = 30 + this.rand.nextInt(20);
        }
    }

    public void setCombatTask() {
        if (!this.world.isRemote) {
            this.tasks.removeTask(this.aiAttackOnCollide);
            this.tasks.removeTask(this.aiArrowAttack);

            ItemStack stack = this.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBow) {
                this.tasks.addTask(3, this.aiArrowAttack);
            } else {
                this.tasks.addTask(3, this.aiAttackOnCollide);
            }
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        ItemStack bowStack = this.getHeldItemMainhand();
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        EntityArrow arrow = ((ItemArrow) Items.ARROW).createArrow(this.world, arrowStack, this);

        arrow.setEnchantmentEffectsFromEntity(this, distanceFactor);

        if (!bowStack.isEmpty()) {
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
            if (punchLevel > 0) {
                arrow.setKnockbackStrength(punchLevel);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack) > 0) {
                arrow.setFire(100);
            }
        }

        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - arrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);

        arrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));

        double calculatedDamageMultiplier = 2.5D + (this.getAttackBoostCount() * 1.0D);

        arrow.setDamage(calculatedDamageMultiplier);

        if (!bowStack.isEmpty()) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
            if (powerLevel > 0) {
                double bonusPowerDamage = (double)powerLevel * 0.5D + 0.5D;

                arrow.setDamage(arrow.getDamage() + bonusPowerDamage);
            }
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(arrow);
        this.stopActiveHand();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.attackTimer > 0) --this.attackTimer;
        if (!this.world.isRemote) {
            if (this.isImmuneToFire != this.getArmored()) {
                this.isImmuneToFire = this.getArmored();
            }
        }
        if (!this.world.isRemote) {
            this.updateShieldBlocking();
        }

        if (!this.world.isRemote && this.isFollowing() && this.getAttackTarget() == null) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null) {
                double distSq = this.getDistanceSq(owner);
                if (distSq > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }
        }

        if (!this.world.isRemote) {
            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND }) {
                ItemStack stack = this.getItemStackFromSlot(slot);
                if (!stack.isEmpty() && (stack.getItem() == ModItems.gunner_gun || stack.getItem() == ModItems.watergun)) {
                    this.entityDropItem(stack.copy(), 0.5F);
                    this.setItemStackToSlot(slot, ItemStack.EMPTY);
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
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 6;
        } else if (id == 45) {
            for (int i = 0; i < 15; ++i) {
                this.world.spawnParticle(EnumParticleTypes.FLAME,
                        this.posX + (this.rand.nextFloat() - 0.5D) * this.width,
                        this.posY + this.rand.nextFloat() * this.height,
                        this.posZ + (this.rand.nextFloat() - 0.5D) * this.width, 0, 0, 0);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (!this.world.isRemote) {
            ItemStack mainHand = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            if (!mainHand.isEmpty()) {
                this.entityDropItem(mainHand, 0.5F);
            }

            ItemStack offHand = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            if (!offHand.isEmpty()) {
                this.entityDropItem(offHand, 0.5F);
            }
        }
    }

    public boolean isFollowing() {
        return this.dataManager.get(FOLLOWING);
    }
    public void setFollowing(boolean follow) {
        this.dataManager.set(FOLLOWING, follow);
    }
    public int getBearType() {
        return this.dataManager.get(BEAR_TYPE);
    }
    public void setBearType(int type) {
        this.dataManager.set(BEAR_TYPE, type);
    }
    public boolean getArmored() {
        return this.dataManager.get(FIREARMOR);
    }
    public void setArmored(boolean armored) {
        this.dataManager.set(FIREARMOR, armored);
    }

    public EnumDyeColor getCollarColor() {
        return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR));
    }

    public void setCollarColor(EnumDyeColor color) {
        this.dataManager.set(COLLAR_COLOR, color.getDyeDamage());
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        super.setItemStackToSlot(slotIn, stack);

        if (!this.world.isRemote && slotIn == EntityEquipmentSlot.MAINHAND) {
            this.setCombatTask();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("BearType", this.getBearType());
        compound.setBoolean("firearmor", this.getArmored());
        compound.setBoolean("following", this.isFollowing());
        compound.setBoolean("sitting", this.isSitting());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setBearType(compound.getInteger("BearType"));
        this.setArmored(compound.getBoolean("firearmor"));
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getInteger("CollarColor")));
        }

        this.setCombatTask();
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 0.25F, 5.0F);
    }

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_CLOTH_HIT;
    }

    @Nullable @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_CLOTH_BREAK;
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    }

    public boolean isAiming() {
        return this.dataManager.get(SWINGING_ARMS);
    }
}