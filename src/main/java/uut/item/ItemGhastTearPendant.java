package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemGhastTearPendant extends Item {


    public static final String REG_NAME = "ghast_tear_pendant";

    public ItemGhastTearPendant(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
    }
}
