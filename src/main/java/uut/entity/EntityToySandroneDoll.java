package uut.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.projectiles.EntitySandroneDollProjectile;
import uut.item.ModItems;
import uut.util.ModSounds;

import static org.jline.utils.Log.error;

public class EntityToySandroneDoll extends BaseDefensiveMob {
    private int healTimer;
    private int attackTimer;
    private int fireballCooldown = 0;

    private SilentFakePlayer cachedFakeEater;

    private static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(EntityToySandroneDoll.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityToySandroneDoll.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToySandroneDoll.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> EATING = EntityDataManager.createKey(EntityToySandroneDoll.class, DataSerializers.BOOLEAN);

    public EntityToySandroneDoll(final World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 1.45f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FOLLOWING, true);
        this.dataManager.register(SITTING, false);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.BLACK.getDyeDamage());
        this.dataManager.register(EATING, false);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(3, new EntityAISit(this) {
            @Override
            public boolean shouldExecute() {
                return isSitting();
            }
        });

        this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.2D, false));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.15D, 10.0F, 2.0F) {
            @Override
            public boolean shouldExecute() {
                return isFollowing() && super.shouldExecute() && EntityToySandroneDoll.this.getAttackTarget() == null;
            }
        });

        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.8D) {
            @Override
            public boolean shouldExecute() {
                return !isFollowing() && super.shouldExecute();
            }
        });

        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, entity -> !isSitting() && entity instanceof IMob));
    }

    public boolean isEating() {
        return this.dataManager.get(EATING);
    }

    public void setEating(boolean eating) {
        this.dataManager.set(EATING, eating);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.attackTimer > 0) --this.attackTimer;
        if (this.fireballCooldown > 0) --this.fireballCooldown;

        if (!this.world.isRemote) {
            EntityLivingBase target = this.getAttackTarget();

            if (!this.isSitting() && target != null && target.isEntityAlive() && this.fireballCooldown <= 0) {
                this.playSound(ModSounds.sandrone_doll_shoot, 1.0F, 1.0F);

                float baseDamage = 2.5F;
                float calculatedDamage = baseDamage + (this.getAttackBoostCount() * 0.5F);

                int missileCount = 4;
                for (int i = 0; i < missileCount; i++) {
                    EntitySandroneDollProjectile missile = new EntitySandroneDollProjectile(this.world, this);

                    missile.setDamage(calculatedDamage);

                    double spawnX = this.posX + (this.rand.nextDouble() - 0.5D) * 0.6D;
                    double spawnY = this.posY + (double)(this.height * 0.75F);
                    double spawnZ = this.posZ + (this.rand.nextDouble() - 0.5D) * 0.6D;

                    missile.setPosition(spawnX, spawnY, spawnZ);
                    missile.target = target;

                    missile.motionX = (this.rand.nextDouble() - 0.5D) * 0.5D;
                    missile.motionY = 0.3D + this.rand.nextDouble() * 0.2D;
                    missile.motionZ = (this.rand.nextDouble() - 0.5D) * 0.5D;

                    this.world.spawnEntity(missile);
                }

                this.fireballCooldown = 300;
            }

            ItemStack offhandItem = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            boolean hasFood = !offhandItem.isEmpty() && offhandItem.getItem() instanceof ItemFood;

            boolean shouldStartEating = this.getHealth() <= (this.getMaxHealth() * 0.5F);
            boolean shouldContinueEating = this.isEating() && (this.getHealth() < this.getMaxHealth());

            if (this.isEntityAlive() && (shouldStartEating || shouldContinueEating) && hasFood) {
                if (!this.isEating()) {
                    this.setEating(true);
                    this.setActiveHand(EnumHand.OFF_HAND);
                }

                this.healTimer++;

                spawnHitboxParticles(offhandItem);


                if (this.healTimer >= 32) {
                    ItemFood food = (ItemFood) offhandItem.getItem();

                    float healAmount = (float) food.getHealAmount(offhandItem);
                    this.heal(healAmount);

                    ItemStack foodCopy = offhandItem.copy();

                    ItemStack containerStack = simulatePlayerEatingEffects(food, foodCopy);

                    if (containerStack.isEmpty() && food.hasContainerItem(foodCopy)) {
                        containerStack = food.getContainerItem(foodCopy);
                    }

                    this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5F, this.rand.nextFloat() * 0.1F + 0.9F);

                    offhandItem.shrink(1);

                    if (!containerStack.isEmpty()) {
                        this.entityDropItem(containerStack.copy(), 0.0F);
                    }

                    if (offhandItem.isEmpty()) {
                        this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    ItemStack updatedOffhand = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
                    if (this.getHealth() >= this.getMaxHealth() || updatedOffhand.isEmpty() || !(updatedOffhand.getItem() instanceof ItemFood)) {
                        this.setEating(false);
                        this.resetActiveHand();
                        this.healTimer = 0;
                    } else {
                        this.resetActiveHand();
                        this.setActiveHand(EnumHand.OFF_HAND);
                        this.healTimer = 0;
                    }
                }
            } else {
                if (this.isEating() || this.healTimer > 0) {
                    this.healTimer = 0;
                    this.setEating(false);
                    this.resetActiveHand();
                }
            }

            if (this.isFollowing() && this.getAttackTarget() == null) {
                EntityLivingBase owner = this.getOwner();
                if (owner != null && this.getDistanceSq(owner) > 144.0D) {
                    this.teleportToOwner(owner);
                }
            }
        }
    }

    @Override
    public void onItemUseFinish() {
        this.resetActiveHand();
    }

    private void spawnHitboxParticles(ItemStack foodStack) {
        if (this.world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) this.world;
            int itemId = Item.getIdFromItem(foodStack.getItem());
            int meta = foodStack.getMetadata();

            for (int i = 0; i < 4; ++i) {
                double px = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
                double py = this.posY + this.rand.nextDouble() * (double)this.height;
                double pz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;

                double speedX = (this.rand.nextDouble() - 0.5D) * 0.1D;
                double speedY = 0.05D + this.rand.nextDouble() * 0.1D;
                double speedZ = (this.rand.nextDouble() - 0.5D) * 0.1D;

                worldServer.spawnParticle(EnumParticleTypes.ITEM_CRACK, px, py, pz, 1, speedX, speedY, speedZ, 0.05D, itemId, meta);

                if (this.rand.nextBoolean()) {
                    worldServer.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, px, py, pz, 1, speedX, speedY, speedZ, 0.02D);
                }
            }
        }
    }

    private static class SilentFakePlayer extends FakePlayer {
        SilentFakePlayer(WorldServer world, GameProfile profile) {
            super(world, profile);
        }

        @Override
        protected void onNewPotionEffect(PotionEffect id) {}

        @Override
        protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {}

        @Override
        protected void onFinishedPotionEffect(PotionEffect effect) {}
    }

    private ItemStack simulatePlayerEatingEffects(ItemFood food, ItemStack realStack) {
        if (!(this.world instanceof WorldServer)) {
            return ItemStack.EMPTY;
        }
        WorldServer worldserver = (WorldServer) this.world;
        ItemStack containerResult = ItemStack.EMPTY;

        try {
            if (this.cachedFakeEater == null || this.cachedFakeEater.world != worldserver) {
                GameProfile profile = new GameProfile(this.getUniqueID(), "[SandroneEater]");
                this.cachedFakeEater = new SilentFakePlayer(worldserver, profile);
            }
            SilentFakePlayer fakePlayer = this.cachedFakeEater;

            fakePlayer.setPositionAndRotation(this.posX, this.posY, this.posZ, 0F, 0F);
            fakePlayer.setHealth(fakePlayer.getMaxHealth());
            fakePlayer.clearActivePotions();

            ItemStack singleItem = realStack.copy();
            singleItem.setCount(1);
            fakePlayer.setHeldItem(EnumHand.MAIN_HAND, singleItem);

            NBTTagCompound foodNbt = new NBTTagCompound();
            foodNbt.setInteger("foodLevel", 0);
            foodNbt.setFloat("foodSaturationLevel", 0F);
            foodNbt.setFloat("foodExhaustionLevel", 0F);
            foodNbt.setInteger("foodTickTimer", 0);
            fakePlayer.getFoodStats().readNBT(foodNbt);

            try {
                containerResult = food.onItemUseFinish(singleItem, this.world, fakePlayer);
            } catch (Throwable modException) {
                error("Error simulating food effects for "
                        + food.getRegistryName() + " on EntityToySandroneDoll", modException);
            }

            for (PotionEffect effect : fakePlayer.getActivePotionEffects()) {
                this.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(),
                        effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
            }

            fakePlayer.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            fakePlayer.clearActivePotions();

        } catch (Throwable t) {
            error("Unexpected failure simulating food on EntityToySandroneDoll", t);
        }

        return containerResult;
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.cachedFakeEater = null;
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
                this.isSafeToStandAt(pos) && this.isSafeToStandAt(pos.up());
    }

    private boolean isSafeToStandAt(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        return !state.getMaterial().isSolid() && !state.getMaterial().isLiquid();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isOwner(player) && itemstack.getItem() == ModItems.toy_command_staff) {
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

        if (hand == EnumHand.MAIN_HAND && this.isOwner(player)) {
            if (itemstack.getItem() instanceof ItemFood || itemstack.isEmpty()) {
                ItemStack currentEntityItem = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
                boolean willDoSomething = !currentEntityItem.isEmpty()
                        || (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemFood);

                if (willDoSomething) {
                    if (!this.world.isRemote) {
                        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemFood) {
                            ItemStack toEquip = player.capabilities.isCreativeMode ? itemstack.copy() : itemstack.splitStack(itemstack.getCount());
                            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, toEquip);
                        } else {
                            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                        }

                        if (!currentEntityItem.isEmpty()) {
                            player.setHeldItem(hand, currentEntityItem.copy());
                        } else if (!player.capabilities.isCreativeMode) {
                            player.setHeldItem(hand, ItemStack.EMPTY);
                        }

                        this.healTimer = 0;
                        this.setEating(false);
                        this.resetActiveHand();

                        this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    }
                    return true;
                }

            } else if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemDye) {
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

    public boolean isFollowing() { return this.dataManager.get(FOLLOWING); }
    public void setFollowing(boolean follow) { this.dataManager.set(FOLLOWING, follow); }

    public boolean isSitting() { return this.dataManager.get(SITTING); }
    public void setSitting(boolean sitting) { this.dataManager.set(SITTING, sitting); }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (net.minecraft.entity.monster.EntityShulker.class.isAssignableFrom(cls)) {
            return true;
        }
        return !EntityGolem.class.isAssignableFrom(cls) && cls != BaseDefensiveMob.class && super.canAttackClass(cls);
    }

    public EnumDyeColor getCollarColor() { return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR)); }
    public void setCollarColor(EnumDyeColor color) { this.dataManager.set(COLLAR_COLOR, color.getDyeDamage()); }

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
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.5D);
    }

    @Override
    public boolean attackEntityAsMob(final Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte)4);
        float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

        if (flag) {
            entityIn.motionY += 0.4D;
            this.applyEnchantments(this, entityIn);
            this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.4F);
        }
        return flag;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) { this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 0.25F, 1.6F); }
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_IRONGOLEM_DEATH; }
    @Override
    protected float getSoundPitch() { return 1.6F; }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() { return this.attackTimer; }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) this.attackTimer = 10;
        else super.handleStatusUpdate(id);
    }
}
