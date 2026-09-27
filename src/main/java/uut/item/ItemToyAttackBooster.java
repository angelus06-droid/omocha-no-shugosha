package uut.item;

import net.minecraft.item.Item;
import uut.tabs.CreativeTabsOmochaToys;

public class ItemToyAttackBooster extends Item {


    public static final String REG_NAME = "toy_attack_booster";

    public ItemToyAttackBooster(final String unlocalizedName) {
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(16);
    }
}
