package uut.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToySpaceship;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import uut.tabs.CreativeTabsOmochaToys;
import uut.util.ModSounds;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSpaceshipToy extends Item {

    public static final String REG_NAME = "spaceship_toy";

    public ItemSpaceshipToy(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(6);
    }

    public Entity spawnEntity(World world, BlockPos pos) {
        EntityToySpaceship entity = new EntityToySpaceship(world);
        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        world.spawnEntity(entity);
        return entity;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult result = playerIn.rayTrace(5, 1);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            playerIn.swingArm(handIn);
            if (!worldIn.isRemote) {
                BlockPos targetPos = result.getBlockPos().offset(result.sideHit);
                if (ModSounds.toy_box_open != null) {
                    worldIn.playSound(null, targetPos, ModSounds.toy_box_open, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }

                Entity spawnedEntity = spawnEntity(worldIn, targetPos);

                if (spawnedEntity instanceof EntityToySpaceship) {
                    EntityToySpaceship spaceship = (EntityToySpaceship) spawnedEntity;
                    spaceship.setTamed(true);
                    spaceship.setOwnerId(playerIn.getUniqueID());
                    worldIn.setEntityState(spaceship, (byte) 7);

                    if (playerIn instanceof EntityPlayerMP) {
                        CriteriaTriggers.TAME_ANIMAL.trigger((EntityPlayerMP) playerIn, spaceship);
                    }

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, itemstack);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.toy_description.health") + ": " + TextFormatting.WHITE + "20 HP");
        tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.toy_description.armor") + ": " + TextFormatting.WHITE + "10.0");
        tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.toy_description.attack_type") + ": " + TextFormatting.WHITE + I18n.format("tooltip.toy_description.melee"));
        tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.toy_description.ability") + ": " + TextFormatting.WHITE + I18n.format("tooltip.toy_spaceship.ability"));
        tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.toy_description.damage") + ": " + TextFormatting.WHITE + "7.0");
    }
}