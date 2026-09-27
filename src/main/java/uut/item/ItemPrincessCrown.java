package uut.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.render.model.ModelPrincessCrown;
import uut.tabs.CreativeTabsOmochaToys;

import java.util.Objects;

public class ItemPrincessCrown extends ItemArmor {
    public static final ArmorMaterial PRINCESS_ARMOR_MATERIAL = EnumHelper.addArmorMaterial(
            "princess_crown",
            "uut:princess_crown",
            0,
            new int[]{3, 6, 8, 3},
            25,
            net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            2.0F
    );

    public ItemPrincessCrown(final String unlocalizedName) {
        super(Objects.requireNonNull(PRINCESS_ARMOR_MATERIAL), 0, EntityEquipmentSlot.HEAD);
        this.setCreativeTab(CreativeTabsOmochaToys.TAB);
        this.setTranslationKey(unlocalizedName);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (armorSlot == EntityEquipmentSlot.HEAD) {
            ModelPrincessCrown crownModel = new ModelPrincessCrown();

            crownModel.bipedHead.showModel = true;
            crownModel.isSneak = _default.isSneak;
            crownModel.isRiding = _default.isRiding;
            crownModel.isChild = _default.isChild;

            return crownModel;
        }
        return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "uut:textures/armor/princess_crown.png";
    }
}