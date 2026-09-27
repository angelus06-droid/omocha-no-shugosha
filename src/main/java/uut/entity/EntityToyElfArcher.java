package uut.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.ai.EntityAIElfArcherRangeAttack;
import uut.entity.projectiles.arrows.EntityElfArcherBlazeRod;
import uut.entity.projectiles.arrows.EntityElfArcherEndDragon;
import uut.entity.projectiles.arrows.EntityElfArcherGhastTear;
import uut.entity.projectiles.arrows.EntityElfArcherWitherRose;
import uut.item.ModItems;

public class EntityToyElfArcher extends BaseDefensiveMob implements IRangedAttackMob {

    public final EntityAIElfArcherRangeAttack aiArrowAttack;

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyElfArcher.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyElfArcher.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyElfArcher.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntityToyElfArcher.class, DataSerializers.BOOLEAN);

    private int healTimer;

    public EntityToyElfArcher(World worldIn) {
        super(worldIn);
        this.aiArrowAttack = new EntityAIElfArcherRangeAttack(this, 1.0D, 25, 15.0F);
        this.setSize(0.6F, 0.9F);
        this.setCombatTask();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.ORANGE.getDyeDamage());
        this.dataManager.register(SWINGING_ARMS, false);
    }

   /**
   * Checks if she has a bow equipped in the main hand. 
   */
    public boolean hasBowEquipped() {
        ItemStack mainHand = this.getHeldItemMainhand();
        return !mainHand.isEmpty() && mainHand.getItem() instanceof ItemBow;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.05D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && super.shouldExecute() && EntityToyElfArcher.this.getAttackTarget() == null;
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

        // She only reacts if she has a bow equipped.
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this) {
            @Override
            public boolean shouldExecute() {
                return hasBowEquipped() && super.shouldExecute();
            }
        });
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this) {
            @Override
            public boolean shouldExecute() {
                return hasBowEquipped() && super.shouldExecute();
            }
        });
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false) {
            @Override
            public boolean shouldExecute() {
                return hasBowEquipped() && super.shouldExecute();
            }
        });
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> {
            if (entity == null || isSitting() || !isEntityAlive() || !hasBowEquipped()) return false;
            return entity instanceof IMob || entity instanceof EntityShulker;
        }));
    }

    public void setCombatTask() {
        if (!this.world.isRemote) {
            this.tasks.removeTask(this.aiArrowAttack);

            if (this.hasBowEquipped()) {
                this.tasks.addTask(3, this.aiArrowAttack);
            } else {
                // If the bow is lost, the target is forgotten and any movement is interrupted.
                this.setAttackTarget(null);
                this.getNavigator().clearPath();
            }
        }
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
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (hand != EnumHand.MAIN_HAND) {
            return super.processInteract(player, hand);
        }

        ItemStack heldItem = player.getHeldItem(hand);

        if (this.isOwner(player)) {

            if (heldItem.getItem() == ModItems.toy_command_staff) {
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
                        this.spawnAnemoEffect(0.1F, 0.8F, 0.5F);
                    } else if (this.isFollowing()) {
                        this.spawnAnemoEffect(0.2F, 1.0F, 0.8F);
                    } else {
                        this.spawnAnemoEffect(0.5F, 0.9F, 0.6F);
                    }
                }

                this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.2F);
                return true;
            }

            boolean isAccessory = heldItem.getItem() == ModItems.blaze_rod_earring ||
                    heldItem.getItem() == ModItems.wither_rose_brooch ||
                    heldItem.getItem() == ModItems.ghast_tear_pendant ||
                    heldItem.getItem() == ModItems.dragon_hairpin;

            if (isAccessory || (player.isSneaking() && heldItem.isEmpty())) {
                EntityEquipmentSlot slot = EntityEquipmentSlot.OFFHAND;
                ItemStack currentAccessory = this.getItemStackFromSlot(slot);

                if (heldItem.isEmpty() && currentAccessory.isEmpty()) {
                    return super.processInteract(player, hand);
                }

                if (!this.world.isRemote) {
                    ItemStack newAccessory = heldItem.copy();
                    if (!newAccessory.isEmpty()) {
                        newAccessory.setCount(1);
                    }

                    this.setItemStackToSlot(slot, newAccessory);

                    if (player.capabilities.isCreativeMode) {
                        if (!currentAccessory.isEmpty()) {
                            player.setHeldItem(hand, currentAccessory);
                        }
                    } else {
                        player.setHeldItem(hand, currentAccessory);
                    }

                    if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                    }
                }

                if (!heldItem.isEmpty() || !currentAccessory.isEmpty()) {
                    this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);
                }
                return true;
            }

            boolean isBlacklisted = heldItem.getItem() == ModItems.gunner_gun || heldItem.getItem() == ModItems.watergun;
            boolean isWeapon = (heldItem.getItem() instanceof ItemBow || heldItem.getItem() instanceof ItemTool) && !isBlacklisted;
            boolean isHandEmpty = heldItem.isEmpty();

            if (isWeapon || isHandEmpty) {
                EntityEquipmentSlot slot = EntityEquipmentSlot.MAINHAND;
                ItemStack currentWeapon = this.getItemStackFromSlot(slot);

                if (isHandEmpty && currentWeapon.isEmpty()) {
                    return super.processInteract(player, hand);
                }

                if (!this.world.isRemote) {
                    ItemStack newWeapon = heldItem.copy();
                    if (!newWeapon.isEmpty()) {
                        newWeapon.setCount(1);
                    }
                    this.setItemStackToSlot(slot, newWeapon);

                    if (player.capabilities.isCreativeMode) {
                        if (!currentWeapon.isEmpty()) {
                            player.setHeldItem(hand, currentWeapon);
                        }
                    } else {
                        player.setHeldItem(hand, currentWeapon);
                    }

                    if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                    }

                    this.setCombatTask();
                }

                if (!heldItem.isEmpty() || !currentWeapon.isEmpty()) {
                    this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);
                }
                return true;
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
        }

        return super.processInteract(player, hand);
    }

    private void spawnAnemoEffect(float red, float green, float blue) {
        for (int i = 0; i < 12; ++i) {
            double px = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
            double py = this.posY + this.rand.nextDouble() * (double)this.height + 0.2D;
            double pz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;

            this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, px, py, pz, (double)red, (double)green, (double)blue);
        }

        for (int j = 0; j < 6; ++j) {
            double px = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 1.2D;
            double py = this.posY + (this.rand.nextDouble() * (double)this.height);
            double pz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 1.2D;

            double motionX = (this.rand.nextDouble() - 0.5D) * 0.1D;
            double motionY = 0.05D + (this.rand.nextDouble() * 0.05D);
            double motionZ = (this.rand.nextDouble() - 0.5D) * 0.1D;

            this.world.spawnParticle(EnumParticleTypes.CLOUD, px, py, pz, motionX, motionY, motionZ);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return false;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        ItemStack bowStack = this.getHeldItemMainhand();
        ItemStack offhandStack = this.getHeldItemOffhand();

        boolean hasBlazeEarring  = !offhandStack.isEmpty() && offhandStack.getItem() == ModItems.blaze_rod_earring;
        boolean hasWitherBrooch  = !offhandStack.isEmpty() && offhandStack.getItem() == ModItems.wither_rose_brooch;
        boolean hasGhastPendant  = !offhandStack.isEmpty() && offhandStack.getItem() == ModItems.ghast_tear_pendant;
        boolean hasDragonHairpin = !offhandStack.isEmpty() && offhandStack.getItem() == ModItems.dragon_hairpin;

        boolean useBlaze  = hasBlazeEarring  && (this.rand.nextFloat() < 0.5F);
        boolean useWither = hasWitherBrooch  && (this.rand.nextFloat() < 0.5F);
        boolean useGhast  = hasGhastPendant  && (this.rand.nextFloat() < 0.5F);
        boolean useDragon = hasDragonHairpin && (this.rand.nextFloat() < 0.5F);

        Entity projectileEntity;

        if (useBlaze) {
            EntityElfArcherBlazeRod blazeArrow = new EntityElfArcherBlazeRod(this.world, this);
            blazeArrow.setExtraDamageMultiplier(2.0D + (this.getAttackBoostCount() * 1.0D));
            projectileEntity = blazeArrow;
            this.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.2F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        }
        else if (useWither) {
            EntityElfArcherWitherRose witherArrow = new EntityElfArcherWitherRose(this.world, this);
            witherArrow.setExtraDamageMultiplier(2.0D + (this.getAttackBoostCount() * 1.0D));
            projectileEntity = witherArrow;
            this.playSound(SoundEvents.ENTITY_WITHER_SHOOT, 0.8F, 1.2F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        }
        else if (useGhast) {
            EntityElfArcherGhastTear ghastArrow = new EntityElfArcherGhastTear(this.world, this);
            ghastArrow.setExtraDamageMultiplier(2.0D + (this.getAttackBoostCount() * 1.0D));
            projectileEntity = ghastArrow;
            this.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 1.0F, 0.8F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        }
        else if (useDragon) {
            projectileEntity = new EntityElfArcherEndDragon(this.world, this);
            this.playSound(SoundEvents.ENTITY_ENDERDRAGON_SHOOT, 1.0F, 0.6F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        }
        else {
            EntityTippedArrow arrow = new EntityTippedArrow(this.world, this);
            arrow.setEnchantmentEffectsFromEntity(this, distanceFactor);

            if (!bowStack.isEmpty() && bowStack.getItem() instanceof ItemBow) {
                int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
                if (punchLevel > 0) {
                    arrow.setKnockbackStrength(punchLevel);
                }

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack) > 0) {
                    arrow.setFire(100);
                }
            }

            double calculatedDamageMultiplier = 2.0D + (this.getAttackBoostCount() * 1.0D);
            arrow.setDamage(calculatedDamageMultiplier);

            if (!bowStack.isEmpty() && bowStack.getItem() instanceof ItemBow) {
                int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
                if (powerLevel > 0) {
                    double bonusPowerDamage = (double) powerLevel * 0.5D + 0.5D;
                    arrow.setDamage(arrow.getDamage() + bonusPowerDamage);
                }
            }

            projectileEntity = arrow;
        }

        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - projectileEntity.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        if (projectileEntity instanceof EntityThrowable) {
            ((EntityThrowable) projectileEntity).shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        } else if (projectileEntity instanceof EntityArrow) {
            ((EntityArrow) projectileEntity).shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.2F / (this.getRNG().nextFloat() * 0.4F + 0.8F));

        this.world.spawnEntity(projectileEntity);
        this.stopActiveHand();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.isFollowing() && this.getAttackTarget() == null) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                this.teleportToOwner(owner);
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
        if (id == 45) {
            for (int i = 0; i < 15; ++i) {
                this.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
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
            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND}) {
                ItemStack stack = this.getItemStackFromSlot(slot);
                if (!stack.isEmpty()) this.entityDropItem(stack, 0.5F);
            }
        }
    }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

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
        compound.setBoolean("following", this.isFollowing());
        compound.setBoolean("sitting", this.isSitting());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setFollowing(compound.getBoolean("following"));
        this.setSitting(compound.getBoolean("sitting"));
        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getInteger("CollarColor")));
        }

        this.setCombatTask();
    }

    public boolean isHighVariant() {
        if (!this.hasCustomName()) return false;
        String name = net.minecraft.util.StringUtils.stripControlCodes(this.getCustomNameTag()).trim();
        return "high".equalsIgnoreCase(name) ||
                "high elf".equalsIgnoreCase(name) ||
                "high-elf".equalsIgnoreCase(name);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) { this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.25f, 1.8f); }
    @Override
    protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.BLOCK_STONE_HIT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_STONE_BREAK; }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    }

    public boolean isAiming() {
        return this.dataManager.get(SWINGING_ARMS);
    }
}
