package uut.entity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import uut.entity.ai.EntityAIGnomeFarm;
import uut.item.ModItems;

public class EntityToyGardenGnome extends BaseDefensiveMob {

    private int healTimer;
    private int attackTimer;
    private BlockPos boundChestPos = null;

    public final InventoryBasic gnomeInventory = new InventoryBasic("GnomeInventory", false, 9);

    private static final DataParameter<Integer> GNOME_TYPE = EntityDataManager.createKey(EntityToyGardenGnome.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(EntityToyGardenGnome.class, DataSerializers.VARINT);

    public EntityToyGardenGnome(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.1F);
        this.setCanPickUpLoot(true);
        this.dataManager.register(COLLAR_COLOR, EnumDyeColor.BLUE.getDyeDamage());
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(GNOME_TYPE, 1);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIGnomeFarm(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote && this.boundChestPos != null) {
            if (this.world.isBlockLoaded(this.boundChestPos) && this.world.getTileEntity(this.boundChestPos) == null) {
                this.onBoundChestBroken();
            }
        }

        if (!this.world.isRemote && this.isEntityAlive() && this.isTamed()) {
            ItemStack held = this.getHeldItemMainhand();
            if ((held.getItem() instanceof ItemShears || isBoneMeal(held)) && !this.isInventoryFull()) {
                java.util.List<EntityItem> items = this.world.getEntitiesWithinAABB(
                        EntityItem.class,
                        this.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D)
                );

                for (EntityItem itemEntity : items) {
                    if (!itemEntity.isDead && crops(itemEntity.getItem())) {
                        ItemStack resto = ItemHandlerHelper.insertItemStacked(new InvWrapper(this.gnomeInventory), itemEntity.getItem(), false);
                        if (resto.isEmpty()) {
                            itemEntity.setDead();
                        } else {
                            itemEntity.setItem(resto);
                        }
                    }
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

    private void spawnAssignmentParticles(BlockPos chestPos) {
        if (this.world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) this.world;

            worldServer.spawnParticle(
                    EnumParticleTypes.VILLAGER_HAPPY,
                    this.posX, this.posY + 1.0D, this.posZ,
                    8, 0.2D, 0.2D, 0.2D, 0.02D
            );

            worldServer.spawnParticle(
                    EnumParticleTypes.CLOUD,
                    chestPos.getX() + 0.5D, chestPos.getY() + 1.1D, chestPos.getZ() + 0.5D,
                    8, 0.2D, 0.2D, 0.2D, 0.02D
            );
        }
    }

    private void onBoundChestBroken() {
        if (this.boundChestPos != null) {
            if (this.world instanceof WorldServer) {
                WorldServer worldServer = (WorldServer) this.world;

                worldServer.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        this.boundChestPos.getX() + 0.5D, this.boundChestPos.getY() + 0.5D, this.boundChestPos.getZ() + 0.5D,
                        10, 0.2D, 0.2D, 0.2D, 0.02D
                );

                worldServer.spawnParticle(
                        EnumParticleTypes.WATER_DROP,
                        this.posX, this.posY + 1.0D, this.posZ,
                        6, 0.2D, 0.2D, 0.2D, 0.02D
                );
            }
            this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F);
            this.boundChestPos = null;
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isOwner(player)) {
            if (!itemstack.isEmpty() && itemstack.getItem() == ModItems.toy_command_staff && player.isSneaking()) {
                if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("ChestX")) {
                    int x = itemstack.getTagCompound().getInteger("ChestX");
                    int y = itemstack.getTagCompound().getInteger("ChestY");
                    int z = itemstack.getTagCompound().getInteger("ChestZ");
                    BlockPos targetPos = new BlockPos(x, y, z);

                    if (this.getDistanceSqToCenter(targetPos) <= 256.0D) {
                        this.setBoundChestPos(targetPos);
                        if (!this.world.isRemote) {
                            player.sendStatusMessage(new TextComponentTranslation("message.gnome.chest_assigned", x, y, z), true);
                            this.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.2F);

                            this.spawnAssignmentParticles(targetPos);
                        }
                    } else {
                        if (!this.world.isRemote) {
                            player.sendStatusMessage(new TextComponentTranslation("message.farming.chest_too_far"), true);
                        }
                    }
                    return true;
                }
            }

            if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemDye && player.isSneaking()) {
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

            boolean isTool = itemstack.getItem() instanceof ItemShears || isBoneMeal(itemstack);
            boolean isHandEmpty = itemstack.isEmpty();

            if (hand == EnumHand.MAIN_HAND && (isTool || isHandEmpty)) {
                if (!this.world.isRemote) {
                    ItemStack farmerOldItem = this.getHeldItemMainhand();

                    if (isHandEmpty && farmerOldItem.isEmpty()) {
                        return super.processInteract(player, hand);
                    }

                    ItemStack playerItemCopy = itemstack.copy();
                    this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, playerItemCopy);

                    if (player.capabilities.isCreativeMode) {
                        if (!farmerOldItem.isEmpty()) {
                            player.setHeldItem(hand, farmerOldItem);
                        }
                    } else {
                        player.setHeldItem(hand, farmerOldItem);
                    }

                    if (player instanceof EntityPlayerMP) {
                        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                    }
                }

                if (!itemstack.isEmpty() || !this.getHeldItemMainhand().isEmpty()) {
                    this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
                }
                return true;
            }
        }
        return super.processInteract(player, hand);
    }

    @Nullable
    public BlockPos getBoundChestPos() {
        return boundChestPos;
    }

    public void setBoundChestPos(@Nullable BlockPos pos) {
        this.boundChestPos = pos;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        return false;
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        return false;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        if (!this.world.isRemote) {
            for (int i = 0; i < this.gnomeInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.gnomeInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }

            ItemStack heldItem = this.getHeldItemMainhand();
            if (!heldItem.isEmpty()) {
                this.entityDropItem(heldItem, 0.0F);
            }
        }
    }

    public boolean isBoneMeal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Items.DYE && stack.getMetadata() == EnumDyeColor.WHITE.getDyeDamage();
    }

    public boolean isInventoryFull() {
        for (int i = 0; i < gnomeInventory.getSizeInventory(); i++) {
            if (gnomeInventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    public boolean hasItemsInInventory() {
        for (int i = 0; i < gnomeInventory.getSizeInventory(); i++) {
            if (!gnomeInventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    private boolean crops(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (isBoneMeal(stack)) return false;

        if (stack.getItem() instanceof ItemSeeds ||
                stack.getItem() instanceof ItemFood ||
                stack.getItem() instanceof ItemSeedFood) {
            return true;
        }

        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if (name.startsWith("seed") || name.startsWith("crop") || name.startsWith("listAllseed")) {
                return true;
            }
        }
        return false;
    }

    public EnumDyeColor getCollarColor() {
        return EnumDyeColor.byDyeDamage(this.dataManager.get(COLLAR_COLOR));
    }

    public void setCollarColor(EnumDyeColor color) {
        this.dataManager.set(COLLAR_COLOR, color.getDyeDamage());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());

        if (this.boundChestPos != null) {
            compound.setInteger("ChestX", this.boundChestPos.getX());
            compound.setInteger("ChestY", this.boundChestPos.getY());
            compound.setInteger("ChestZ", this.boundChestPos.getZ());
        }

        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.gnomeInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.gnomeInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        compound.setTag("GnomeInventoryItems", nbttaglist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("CollarColor", 99)) {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }

        if (compound.hasKey("ChestX") && compound.hasKey("ChestY") && compound.hasKey("ChestZ")) {
            this.boundChestPos = new BlockPos(
                    compound.getInteger("ChestX"),
                    compound.getInteger("ChestY"),
                    compound.getInteger("ChestZ")
            );
        }

        if (compound.hasKey("GnomeInventoryItems", 9)) {
            NBTTagList nbttaglist = compound.getTagList("GnomeInventoryItems", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;

                if (j >= 0 && j < this.gnomeInventory.getSizeInventory()) {
                    this.gnomeInventory.setInventorySlotContents(j, new ItemStack(nbttagcompound));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) this.attackTimer = 10;
        else super.handleStatusUpdate(id);
    }

    @SideOnly(Side.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 0.25F, 5.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_STONE_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_STONE_BREAK;
    }
}