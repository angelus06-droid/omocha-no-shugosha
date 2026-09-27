package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemDragonHairpin extends Item {


    public static final String REG_NAME = "dragon_hairpin";

    public ItemDragonHairpin(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
    }
}
