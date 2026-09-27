package uut.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
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
import net.minecraft.world.WorldServer;
import uut.entity.projectiles.EntityToyBullet;
import uut.entity.projectiles.EntityToyWaterProjectile;
import uut.item.ItemGunnerGun;
import uut.item.ItemWaterGun;
import uut.item.ModItems;
import uut.entity.ai.EntityAIGunnerRangeAttack;
import uut.util.ModSounds;

public class EntityToyGunner extends BaseDefensiveMob implements IRangedAttackMob {

    private int healTimer;

    private int waterBurstCount = 0;
    private int waterBurstDelay = 0;
    private EntityLivingBase waterTarget = null;

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToyGunner.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToyGunner.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyGunner.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntityToyGunner.class, DataSerializers.BOOLEAN);

    public EntityToyGunner(World worldIn) {
        super(worldIn);
        this.setSize(0.4f, 1.4f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.RED.getDyeDamage());
        this.dataManager.register(SWINGING_ARMS, false);
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

        this.tasks.addTask(3, new EntityAIGunnerRangeAttack(this, 1.1D, 60, 16.0F));

        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.1D, 10.0F, 2.0F) {
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

        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(6, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false,
                entity -> entity != null && !isSitting() && entity.isEntityAlive() && (entity instanceof IMob || entity instanceof net.minecraft.entity.monster.EntityShulker)));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
    }

    public boolean isRangedWeapon(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() == ModItems.gunner_gun || stack.getItem() == ModItems.watergun || stack.getItem() instanceof ItemBow;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        if (slotIn == EntityEquipmentSlot.MAINHAND) {
            if (stack.isEmpty() || isRangedWeapon(stack)) {
                super.setItemStackToSlot(slotIn, stack);
            }
            return;
        }
        super.setItemStackToSlot(slotIn, stack);
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
                    this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
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
                return true;
            }

            boolean isWeapon = isRangedWeapon(itemstack);
            boolean isHandEmpty = itemstack.isEmpty();

            if (hand == EnumHand.MAIN_HAND && (isWeapon || (isHandEmpty && player.isSneaking()))) {
                if (!this.world.isRemote) {
                    ItemStack gunnerOldItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

                    if (isHandEmpty && gunnerOldItem.isEmpty()) {
                        return super.processInteract(player, hand);
                    }

                    ItemStack playerItemCopy = itemstack.copy();
                    if (!playerItemCopy.isEmpty()) {
                        playerItemCopy.setCount(1);
                    }

                    this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, playerItemCopy);

                    if (player.capabilities.isCreativeMode) {
                        if (!gunnerOldItem.isEmpty()) {
                            player.setHeldItem(hand, gunnerOldItem);
                        }
                    } else {
                        player.setHeldItem(hand, gunnerOldItem);
                    }

                    if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                    }
                }

                this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);
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
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {
            ItemStack heldItem = this.getHeldItemMainhand();

            if (!heldItem.isEmpty()) {
                if (heldItem.getItem() instanceof ItemGunnerGun) {
                    ((ItemGunnerGun) heldItem.getItem()).onUpdate(heldItem, this.world, this, EntityEquipmentSlot.MAINHAND.getIndex(), true);
                } else if (heldItem.getItem() instanceof ItemWaterGun) {
                    ((ItemWaterGun) heldItem.getItem()).onUpdate(heldItem, this.world, this, EntityEquipmentSlot.MAINHAND.getIndex(), true);
                }
            }

            if (this.waterBurstCount > 0 && this.waterTarget != null) {
                if (this.waterBurstDelay > 0) {
                    this.waterBurstDelay--;
                } else {
                    if (this.waterTarget.isEntityAlive()) {
                        if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.watergun) {
                            if (heldItem.getItemDamage() >= heldItem.getMaxDamage()) {
                                if (heldItem.getItem() instanceof ItemWaterGun) {
                                    ((ItemWaterGun) heldItem.getItem()).reloadMobFree(heldItem, this.world, this);
                                }
                                this.waterBurstCount = 0;
                                this.waterTarget = null;
                            } else {
                                this.fireSingleWaterDrop(heldItem, this.waterTarget);
                                this.waterBurstCount--;
                                this.waterBurstDelay = 2;
                            }
                        } else {
                            this.waterBurstCount = 0;
                            this.waterTarget = null;
                        }
                    } else {
                        this.waterBurstCount = 0;
                        this.waterTarget = null;
                    }
                }
            }

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

        if (!this.world.isRemote && this.isFollowing() && this.getAttackTarget() == null) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null) {
                double distSq = this.getDistanceSq(owner);
                if (distSq > 144.0D) {
                    this.teleportToOwner(owner);
                }
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
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (target == null || !target.isEntityAlive() || this.world.isRemote) return;

        ItemStack heldItem = this.getHeldItemMainhand();

        if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.gunner_gun) {
            if (heldItem.getItem() instanceof ItemGunnerGun) {
                ItemGunnerGun gun = (ItemGunnerGun) heldItem.getItem();
                NBTTagCompound nbt = heldItem.getTagCompound();

                if (nbt == null) {
                    nbt = new NBTTagCompound();
                    heldItem.setTagCompound(nbt);
                    nbt.setInteger("Ammo", 8);
                }

                if (nbt.getInteger("ReloadTimer") > 0) {
                    return;
                }

                int currentAmmo = nbt.getInteger("Ammo");
                if (currentAmmo <= 0) {
                    gun.reloadMobFree(heldItem, this.world, this);
                    return;
                }

                currentAmmo--;
                nbt.setInteger("Ammo", currentAmmo);
                heldItem.setItemDamage(8 - currentAmmo);
            }

            EntityToyBullet bullet = new EntityToyBullet(this.world, this);

            double bulletDamage = 4.0D + (this.getAttackBoostCount() * 0.5D);
            bullet.setDamage(bulletDamage);

            double spawnX = this.posX - (double)(MathHelper.sin(this.rotationYawHead * 0.017453292F) * 0.4F);
            double spawnY = this.posY + (double)this.getEyeHeight() - 0.2D;
            double spawnZ = this.posZ + (double)(MathHelper.cos(this.rotationYawHead * 0.017453292F) * 0.4F);

            bullet.setLocationAndAngles(spawnX, spawnY, spawnZ, this.rotationYawHead, this.rotationPitch);

            double targetCenterY = target.posY + (double)(target.height * 0.5F);
            double d0 = target.posX - spawnX;
            double d1 = targetCenterY - spawnY;
            double d2 = target.posZ - spawnZ;

            bullet.shoot(d0, d1, d2, 3.2F, 1.0F);

            float pitchsound = 1.0F + world.rand.nextFloat() * 0.4F;
            this.playSound(ModSounds.gunner_gun_shoot, 0.8F, pitchsound);

            this.world.spawnEntity(bullet);

            if (this.world instanceof WorldServer) {
                WorldServer worldServer = (WorldServer) this.world;
                worldServer.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, spawnX, spawnY, spawnZ, 3, 0.05D, 0.05D, 0.05D, 0.02D);
                worldServer.spawnParticle(EnumParticleTypes.FLAME, spawnX, spawnY, spawnZ, 2, 0.02D, 0.02D, 0.02D, 0.05D);
            }
        }
        else if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.watergun) {
            NBTTagCompound nbt = heldItem.getTagCompound();
            if (nbt != null && nbt.getInteger("ReloadProgress") > 0) {
                return;
            }

            if (heldItem.getItemDamage() >= heldItem.getMaxDamage()) {
                if (heldItem.getItem() instanceof ItemWaterGun) {
                    ((ItemWaterGun) heldItem.getItem()).reloadMobFree(heldItem, this.world, this);
                }
                return;
            }

            if (this.waterBurstCount <= 0) {
                this.waterBurstCount = 4 + this.rand.nextInt(5);
                this.waterTarget = target;
                this.waterBurstDelay = 0;
            }

            this.stopActiveHand();
        }
        else if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemBow) {
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

            double calculatedDamageMultiplier = 2.0D + (this.getAttackBoostCount() * 1.0D);

            arrow.setDamage(calculatedDamageMultiplier);

            if (!bowStack.isEmpty()) {
                int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
                if (powerLevel > 0) {
                    double bonusPowerDamage = (double)powerLevel * 0.5D + 0.5D;

                    arrow.setDamage(arrow.getDamage() + bonusPowerDamage);
                }
            }

            this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.4F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.world.spawnEntity(arrow);
            this.stopActiveHand();
        }
    }

    private void fireSingleWaterDrop(ItemStack stack, EntityLivingBase target) {
        EntityToyWaterProjectile waterProjectile = new EntityToyWaterProjectile(this.world, this);

        double waterDamage = 2.0D + (this.getAttackBoostCount() * 0.5D);
        waterProjectile.setDamage(waterDamage);

        double spawnX = this.posX - (double)(MathHelper.sin(this.rotationYawHead * 0.017453292F) * 0.4F);
        double spawnY = this.posY + (double)this.getEyeHeight() - 0.2D;
        double spawnZ = this.posZ + (double)(MathHelper.cos(this.rotationYawHead * 0.017453292F) * 0.4F);

        waterProjectile.setLocationAndAngles(spawnX, spawnY, spawnZ, this.rotationYawHead, this.rotationPitch);

        double targetCenterY = target.posY + (double)(target.height * 0.5F);
        double d0 = target.posX - spawnX;
        double d1 = targetCenterY - spawnY;
        double d2 = target.posZ - spawnZ;

        waterProjectile.shoot(d0, d1, d2, 2.5F, 3.0F);

        stack.setItemDamage(Math.min(stack.getMaxDamage(), stack.getItemDamage() + 1));

        float pitchsound = 1.0F + world.rand.nextFloat() * 0.4F;
        this.playSound(ModSounds.watergun_shoot, 0.9F, pitchsound);

        this.world.spawnEntity(waterProjectile);

        if (this.world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) this.world;
            worldServer.spawnParticle(EnumParticleTypes.WATER_SPLASH, spawnX, spawnY, spawnZ, 5, 0.05D, 0.05D, 0.05D, 0.05D);
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
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    }

    public boolean isAiming() {
        return this.dataManager.get(SWINGING_ARMS);
    }

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
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
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public float getEyeHeight() {
        return 1.2F;
    }
}