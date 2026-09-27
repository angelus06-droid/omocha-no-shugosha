package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemToyDefenseBooster extends Item {


    public static final String REG_NAME = "toy_defense_booster";

    public ItemToyDefenseBooster(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(16);
    }
}
