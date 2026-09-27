package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemToyUpgradingList extends Item {


    public static final String REG_NAME = "toy_upgrading_list";

    public ItemToyUpgradingList(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(16);
    }
}
