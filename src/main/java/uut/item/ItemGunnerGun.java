package uut.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import uut.entity.EntityToyGunner;
import uut.entity.projectiles.EntityGunnerGunBullet;
import uut.tabs.CreativeTabsOmochaToys;
import uut.util.ModSounds;

import java.util.List;

public class ItemGunnerGun extends ItemBow {

    public static final String REG_NAME = "gunner_gun";

    private static final int MAX_AMMO = 8;
    private static final int RELOAD_TIME = 40;
    private static final int MIN_CHARGE_TICKS = 4;

    public ItemGunnerGun(final String unlocalizedName) {
        super();
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxDamage(MAX_AMMO);
        this.setMaxStackSize(1);
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
    public boolean isArrow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Items.IRON_NUGGET;
    }

    private int countIronNuggets(EntityLivingBase shooter) {
        int total = 0;

        if (shooter instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) shooter;

            ItemStack offhandStack = player.getHeldItem(EnumHand.OFF_HAND);
            if (!offhandStack.isEmpty() && offhandStack.getItem() == Items.IRON_NUGGET) {
                total += offhandStack.getCount();
            }

            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == Items.IRON_NUGGET) {
                    total += stack.getCount();
                }
            }
        } else if (shooter instanceof EntityLiving) {
            EntityLiving mob = (EntityLiving) shooter;
            for (ItemStack stack : mob.getHeldEquipment()) {
                if (!stack.isEmpty() && stack.getItem() == Items.IRON_NUGGET) {
                    total += stack.getCount();
                }
            }
        }

        return total;
    }

    private int consumeIronNuggets(EntityLivingBase shooter, int maxAmount) {
        if (shooter instanceof EntityPlayer && ((EntityPlayer) shooter).capabilities.isCreativeMode) {
            return maxAmount;
        }

        int remainingToConsume = maxAmount;

        if (shooter instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) shooter;

            ItemStack offhandStack = player.getHeldItem(EnumHand.OFF_HAND);
            if (remainingToConsume > 0 && !offhandStack.isEmpty() && offhandStack.getItem() == Items.IRON_NUGGET) {
                int count = offhandStack.getCount();
                int toTake = Math.min(count, remainingToConsume);
                offhandStack.shrink(toTake);
                remainingToConsume -= toTake;
            }

            for (int i = 0; i < player.inventory.getSizeInventory() && remainingToConsume > 0; ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == Items.IRON_NUGGET) {
                    int count = stack.getCount();
                    int toTake = Math.min(count, remainingToConsume);
                    stack.shrink(toTake);
                    remainingToConsume -= toTake;
                }
            }
        } else if (shooter instanceof EntityLiving) {
            EntityLiving mob = (EntityLiving) shooter;

            for (ItemStack stack : mob.getHeldEquipment()) {
                if (remainingToConsume <= 0) break;
                if (!stack.isEmpty() && stack.getItem() == Items.IRON_NUGGET) {
                    int count = stack.getCount();
                    int toTake = Math.min(count, remainingToConsume);
                    stack.shrink(toTake);
                    remainingToConsume -= toTake;
                }
            }
        }

        return maxAmount - remainingToConsume;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            EntityLivingBase shooter = (EntityLivingBase) entity;
            NBTTagCompound nbt = getOrInitNBT(stack);

            int reloadTimer = nbt.getInteger("ReloadTimer");

            if (reloadTimer > 0) {
                reloadTimer--;
                nbt.setInteger("ReloadTimer", reloadTimer);

                if (reloadTimer <= 0) {
                    int loadedAmmo = nbt.hasKey("PendingAmmo") ? nbt.getInteger("PendingAmmo") : MAX_AMMO;
                    loadedAmmo = Math.min(loadedAmmo, MAX_AMMO);

                    nbt.setInteger("Ammo", loadedAmmo);
                    nbt.removeTag("PendingAmmo");
                    stack.setItemDamage(MAX_AMMO - loadedAmmo);

                    world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                            SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 1.0F, 1.2F);
                }
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (worldIn.isRemote || !(entityLiving instanceof EntityPlayer)) return;

        NBTTagCompound nbt = getOrInitNBT(stack);

        if (nbt.getInteger("ReloadTimer") > 0) {
            return;
        }

        int useDuration = this.getMaxItemUseDuration(stack) - timeLeft;
        if (useDuration < MIN_CHARGE_TICKS) {
            return;
        }

        long currentTick = worldIn.getTotalWorldTime();
        long lastShotTick = nbt.getLong("LastShotTick");
        if (currentTick - lastShotTick < 2) {
            return;
        }

        int currentAmmo = nbt.getInteger("Ammo");

        if (currentAmmo > 0) {
            EntityLivingBase target = getMaidTarget(entityLiving);
            this.shootBullet(worldIn, entityLiving, stack, target);

            nbt.setLong("LastShotTick", currentTick);

            boolean isCreative = entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode;
            if (!isCreative) {
                currentAmmo--;
                nbt.setInteger("Ammo", currentAmmo);
                stack.setItemDamage(MAX_AMMO - currentAmmo);
            }

            if (entityLiving instanceof EntityPlayer) {
                ((EntityPlayer) entityLiving).getCooldownTracker().setCooldown(this, 5);
            }
        } else {
            tryStartReload(stack, worldIn, entityLiving, nbt);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound nbt = getOrInitNBT(stack);

        if (nbt.getInteger("ReloadTimer") > 0) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (nbt.getInteger("Ammo") <= 0) {
            if (!world.isRemote) {
                boolean started = tryStartReload(stack, world, player, nbt);
                return new ActionResult<>(started ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private boolean tryStartReload(ItemStack stack, World world, EntityLivingBase shooter, NBTTagCompound nbt) {
        int available = countIronNuggets(shooter);

        if (available <= 0) {
            world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                    SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0F, 1.2F);
            return false;
        }

        int toConsume = Math.min(available, MAX_AMMO);
        int consumed = consumeIronNuggets(shooter, toConsume);

        if (consumed <= 0) {
            world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                    SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0F, 1.2F);
            return false;
        }

        startReload(stack, world, shooter, nbt, consumed);
        return true;
    }

    private void shootBullet(World world, EntityLivingBase shooter, ItemStack stack, EntityLivingBase target) {
        EntityGunnerGunBullet bullet = new EntityGunnerGunBullet(world, shooter);

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

        bullet.shoot(shooter, pitch, yaw, 0.0F, 3.5F, 0.5F);
        world.spawnEntity(bullet);

        float pitchsound = 1.0F + world.rand.nextFloat() * 0.4F;
        world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                ModSounds.gunner_gun_shoot, SoundCategory.PLAYERS, 0.8F, pitchsound);
    }

    private void startReload(ItemStack stack, World world, EntityLivingBase shooter, NBTTagCompound nbt, int ammoToLoad) {
        nbt.setInteger("ReloadTimer", RELOAD_TIME);
        nbt.setInteger("PendingAmmo", ammoToLoad);
        world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.PLAYERS, 1.0F, 0.8F);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!slotChanged && oldStack.getItem() == newStack.getItem()) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    private NBTTagCompound getOrInitNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey("Ammo")) {
            nbt.setInteger("Ammo", MAX_AMMO);
        }
        return nbt;
    }

    private EntityLivingBase getMaidTarget(EntityLivingBase shooter) {
        if (shooter instanceof EntityLiving) {
            return ((EntityLiving) shooter).getAttackTarget();
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbt = getOrInitNBT(stack);
        int ammo = nbt.getInteger("Ammo");
        int reloadTimer = nbt.getInteger("ReloadTimer");

        if (reloadTimer > 0) {
            tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.gunner_gun.reloading"));
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.gunner_gun.ammo", ammo, MAX_AMMO));
        }
    }

    public void reloadMobFree(ItemStack stack, World world, EntityLivingBase shooter) {
        if (world.isRemote) return;

        NBTTagCompound nbt = getOrInitNBT(stack);

        if (nbt.getInteger("ReloadTimer") <= 0) {
            nbt.setInteger("ReloadTimer", RELOAD_TIME);
            nbt.setInteger("PendingAmmo", MAX_AMMO);

            world.playSound(null, shooter.posX, shooter.posY, shooter.posZ,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.PLAYERS, 1.0F, 0.8F);
        }
    }
}