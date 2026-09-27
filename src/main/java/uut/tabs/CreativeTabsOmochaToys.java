package uut.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import uut.item.ModItems;

public class CreativeTabsOmochaToys extends CreativeTabs {

    public static final CreativeTabsOmochaToys TAB = new CreativeTabsOmochaToys("uut");

    public static final CreativeTabsOmochaToys TAB_VARIANTS = new CreativeTabsOmochaToys("uut_variants") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.omocha_variant_icon);
        }
    };

    public CreativeTabsOmochaToys(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.omocha_icon);
    }
}