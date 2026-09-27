package uut.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import uut.entity.projectiles.EntityWaterProjectile;
import uut.tabs.CreativeTabsOmochaToys;
import uut.util.ModSounds;

import java.util.List;

public class ItemWaterGun extends ItemBow {

    public static final String REG_NAME = "water_gun";

    private static final int MAX_AMMO = 60;
    private static final int RELOAD_TIME = 40;
    private static final int FIRE_COOLDOWN = 2;

    public ItemWaterGun(final String unlocalizedName) {
        super();
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxDamage(MAX_AMMO);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (stack.getItemDamage() >= MAX_AMMO && findWaterBottle(playerIn) == null) {
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                    SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.4F, 1.8F);
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        NBTTagCompound nbt = getOrInitNBT(stack);
        nbt.setInteger("FireCooldown", 0);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof EntityLivingBase)) return;

        EntityLivingBase shooter = (EntityLivingBase) entityIn;
        NBTTagCompound nbt = getOrInitNBT(stack);

        int cooldown = nbt.getInteger("FireCooldown");
        if (cooldown > 0) {
            nbt.setInteger("FireCooldown", cooldown - 1);
        }

        int reloadProgress = nbt.getInteger("ReloadProgress");
        if (reloadProgress > 0) {
            reloadProgress++;
            nbt.setInteger("ReloadProgress", reloadProgress);

            if (reloadProgress >= RELOAD_TIME) {
                if (!worldIn.isRemote) {
                    this.setDamage(stack, 0);
                    worldIn.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                            SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }

                nbt.setInteger("ReloadProgress", 0);
                shooter.resetActiveHand();
            }
            return;
        }

        boolean isUsingThisItem = shooter.isHandActive() && shooter.getActiveItemStack() == stack;

        if (isUsingThisItem && !worldIn.isRemote) {
            boolean isLittleMaid = isLittleMaidEntity(shooter);

            if (shooter instanceof EntityPlayer || isLittleMaid) {
                if (stack.getItemDamage() < MAX_AMMO) {
                    this.fireWater(stack, worldIn, shooter);
                    updateCheckinghSlot(shooter, stack);
                } else {
                    if (nbt.getInteger("FireCooldown") <= 0) {
                        worldIn.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                                SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.4F, 1.8F);
                        nbt.setInteger("FireCooldown", 10);
                    }
                    shooter.resetActiveHand();
                }
            }
        }

        if (stack.getItemDamage() >= this.getMaxDamage() && isSelected && !worldIn.isRemote) {
            if (shooter instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) shooter;
                ItemStack waterBottle = this.findWaterBottle(player);

                if (waterBottle != null) {
                    boolean isCreative = player.capabilities.isCreativeMode;
                    if (!isCreative) {
                        waterBottle.shrink(1);
                        ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                        if (!player.inventory.addItemStackToInventory(glassBottle)) {
                            player.dropItem(glassBottle, false);
                        }
                    }

                    if (waterBottle.isEmpty()) {
                        player.inventory.deleteStack(waterBottle);
                    }

                    nbt.setInteger("ReloadProgress", 1);
                    shooter.resetActiveHand();
                }
            } else {
                this.reloadMobFree(stack, worldIn, shooter);
            }
        }

        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    private boolean isLittleMaidEntity(EntityLivingBase entity) {
        if (entity == null) return false;
        String className = entity.getClass().getName();
        return className.contains("littleMaid") || className.contains("LittleMaid") || className.contains("LMM_EntityLittleMaid");
    }

    public void fireWater(ItemStack stack, World worldIn, EntityLivingBase shooter) {
        NBTTagCompound nbt = getOrInitNBT(stack);

        if (nbt.getInteger("FireCooldown") > 0) return;

        shootWaterProjectile(worldIn, shooter, getMaidTarget(shooter));

        boolean isCreative = (shooter instanceof EntityPlayer)
                && ((EntityPlayer) shooter).capabilities.isCreativeMode;
        if (!isCreative) {
            int currentDamage = stack.getItemDamage();
            stack.setItemDamage(Math.min(MAX_AMMO, currentDamage + 1));
        }

        nbt.setInteger("FireCooldown", FIRE_COOLDOWN);
    }

    private void shootWaterProjectile(World world, EntityLivingBase shooter, EntityLivingBase target) {
        EntityWaterProjectile water = new EntityWaterProjectile(world, shooter);

        float pitch = shooter.rotationPitch;
        float yaw = shooter.rotationYaw;

        if (target != null) {
            double deltaX = target.posX - shooter.posX;
            double deltaY = (target.posY + (double) target.getEyeHeight()) - (shooter.posY + (double) shooter.getEyeHeight());
            double deltaZ = target.posZ - shooter.posZ;
            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            pitch = (float) (-Math.toDegrees(Math.atan2(deltaY, horizontalDistance)));
            yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX))) - 90.0F;
        }

        water.shoot(shooter, pitch, yaw, 0.0F, 2.5F, 1.0F);
        world.spawnEntity(water);

        world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                ModSounds.watergun_shoot, SoundCategory.PLAYERS, 0.9F, 1.0F + (itemRand.nextFloat() * 0.4F));
    }

    private EntityLivingBase getMaidTarget(EntityLivingBase shooter) {
        if (shooter instanceof EntityLiving) {
            return ((EntityLiving) shooter).getAttackTarget();
        }
        return null;
    }

    public void reloadMobFree(ItemStack stack, World world, EntityLivingBase shooter) {
        NBTTagCompound nbt = getOrInitNBT(stack);

        if (nbt.getInteger("ReloadProgress") <= 0) {
            nbt.setInteger("ReloadProgress", 1);
            world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                    SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    private ItemStack findWaterBottle(EntityPlayer player) {
        if (this.isWaterBottle(player.getHeldItem(EnumHand.OFF_HAND))) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        if (this.isWaterBottle(player.getHeldItem(EnumHand.MAIN_HAND))) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (this.isWaterBottle(stack)) {
                return stack;
            }
        }
        return null;
    }

    protected boolean isWaterBottle(ItemStack stack) {
        return !stack.isEmpty() &&
                stack.getItem() == Items.POTIONITEM &&
                PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER;
    }

    public static void updateCheckinghSlot(Entity pEntity, ItemStack pItemstack) {
        if (pEntity instanceof EntityPlayerMP) {
            EntityPlayerMP lep = (EntityPlayerMP) pEntity;
            Container lctr = lep.openContainer;
            for (int li = 0; li < lctr.inventorySlots.size(); ++li) {
                ItemStack lis = lctr.getSlot(li).getStack();
                if (lis == pItemstack) {
                    lctr.inventoryItemStacks.set(li, pItemstack.copy());
                    break;
                }
            }
        }
    }

    private NBTTagCompound getOrInitNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey("ReloadProgress")) {
            nbt.setInteger("ReloadProgress", 0);
        }
        if (!nbt.hasKey("FireCooldown")) {
            nbt.setInteger("FireCooldown", 0);
        }
        return nbt;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbt = getOrInitNBT(stack);
        int currentAmmo = MAX_AMMO - stack.getItemDamage();
        int reloadProgress = nbt.getInteger("ReloadProgress");

        if (reloadProgress > 0) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.gunner_gun.reloading"));
        } else {
            tooltip.add(TextFormatting.AQUA + I18n.format("tooltip.gunner_gun.ammo", currentAmmo, MAX_AMMO));
        }
    }
}