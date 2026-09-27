package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemWitherRoseBrooch extends Item {


    public static final String REG_NAME = "wither_rose_brooch";

    public ItemWitherRoseBrooch(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
    }
}
