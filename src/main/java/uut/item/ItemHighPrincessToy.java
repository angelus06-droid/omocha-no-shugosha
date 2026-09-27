package uut.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyHighPrincess;
import uut.tabs.CreativeTabsOmochaToys;
import uut.util.ModSounds;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHighPrincessToy extends Item {

    public static final String REG_NAME = "highprincess_toy";

    public ItemHighPrincessToy(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(6);
    }

    public Entity spawnEntity(World world, BlockPos pos) {
        EntityToyHighPrincess entity = new EntityToyHighPrincess(world);
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

                if (spawnedEntity instanceof EntityToyHighPrincess) {
                    EntityToyHighPrincess pity = (EntityToyHighPrincess) spawnedEntity;
                    pity.setTamed(true);
                    pity.setOwnerId(playerIn.getUniqueID());
                    worldIn.setEntityState(pity, (byte) 7);

                    if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }

                    if (playerIn instanceof EntityPlayerMP) {
                        CriteriaTriggers.TAME_ANIMAL.trigger((EntityPlayerMP) playerIn, pity);
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

        tooltip.add(TextFormatting.GREEN + I18n.format("tooltip.toy_description.health") + ": " + TextFormatting.WHITE + "60 HP");
        tooltip.add(TextFormatting.GREEN + I18n.format("tooltip.toy_description.armor") + ": " + TextFormatting.WHITE + "15.0");
        tooltip.add(TextFormatting.GREEN + I18n.format("tooltip.toy_description.attack_type") + ": " + TextFormatting.WHITE + I18n.format("tooltip.toy_description.summon"));
        tooltip.add(TextFormatting.GREEN + I18n.format("tooltip.toy_description.ability") + ": " + TextFormatting.WHITE + I18n.format("tooltip.toy_high_princess.ability"));
    }
}