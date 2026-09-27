package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemBlazeRodEarring extends Item {


    public static final String REG_NAME = "blaze_rod_earring";

    public ItemBlazeRodEarring(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
    }
}
