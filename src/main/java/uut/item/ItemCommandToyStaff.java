package uut.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemCommandToyStaff extends Item {

    public static final String REG_NAME = "toy_command_staff";

    public ItemCommandToyStaff(final String unlocalizedName) {
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                ItemStack held = player.getHeldItem(hand);
                if (!held.hasTagCompound()) {
                    held.setTagCompound(new NBTTagCompound());
                }
                NBTTagCompound nbt = held.getTagCompound();
                nbt.setInteger("ChestX", pos.getX());
                nbt.setInteger("ChestY", pos.getY());
                nbt.setInteger("ChestZ", pos.getZ());

                if (!worldIn.isRemote) {
                    player.sendStatusMessage(new TextComponentTranslation("message.toy_command_staff.bound", pos.getX(), pos.getY(), pos.getZ()), true);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }
}