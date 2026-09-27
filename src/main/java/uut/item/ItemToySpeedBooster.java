package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemToySpeedBooster extends Item {


    public static final String REG_NAME = "toy_speed_booster";

    public ItemToySpeedBooster(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(16);
    }
}
