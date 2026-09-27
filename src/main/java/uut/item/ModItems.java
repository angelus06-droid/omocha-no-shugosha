package uut.item;

import net.minecraft.item.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraftforge.client.model.*;

public class ModItems {
	public static Item toy_box;
	public static Item yeti_toy;
	public static Item bomby_toy;
	public static Item arrowtower_toy;
	public static Item spaceship_toy;
	public static Item gunner_toy;
	public static Item tank_toy;
	public static Item airballoon_toy;
	public static Item dummy_toy;
	public static Item dummy_toy_explosive;
	public static Item teddybear_toy;
	public static Item sandrone_toy;
	public static Item gnome_toy;
	public static Item teddyqilin_toy;
	public static Item tyrannosaur_toy;
	public static Item t58_toy;
	public static Item sentinel_tower_toy;
	public static Item hay_farmer_toy;
	public static Item kleebomby_toy;
	public static Item technodemon_toy;
	public static Item kitsune_toy;
	public static Item destroyerpity_toy;
	public static Item highprincess_toy;
	public static Item crimsonbunny_toy;
	public static Item tinydragoness_toy;
	public static Item elfarcher_toy;
	public static Item toy_command_staff;
	public static Item toy_capsule;
	public static Item toy_upgrading_list;
	public static Item toy_heart_booster;
	public static Item toy_defense_booster;
	public static Item toy_attack_booster;
	public static Item toy_speed_booster;
	public static Item blaze_rod_earring;
	public static Item wither_rose_brooch;
	public static Item ghast_tear_pendant;
	public static Item dragon_hairpin;
	public static Item gunner_gun;
	public static Item watergun;
	public static Item omocha_icon;
	public static Item omocha_variant_icon;
	public static Item princess_crown;

	public static final void register() {
		registerItem(ModItems.toy_box = new ItemToyBox("toy_box"));
		registerItem(ModItems.yeti_toy = new ItemYetiToy("yeti_toy"));
		registerItem(ModItems.bomby_toy = new ItemBombyToy("bomby_toy"));
		registerItem(ModItems.arrowtower_toy = new ItemArrowTowerToy("arrowtower_toy"));
		registerItem(ModItems.spaceship_toy = new ItemSpaceshipToy("spaceship_toy"));
		registerItem(ModItems.gunner_toy = new ItemGunnerToy("gunner_toy"));
		registerItem(ModItems.tank_toy = new ItemTankToy("tank_toy"));
		registerItem(ModItems.airballoon_toy = new ItemAirBalloonToy("airballoon_toy"));
		registerItem(ModItems.dummy_toy = new ItemDummyToy("dummy_toy"));
		registerItem(ModItems.dummy_toy_explosive = new ItemDummyExplosiveToy("dummy_toy_explosive"));
		registerItem(ModItems.teddybear_toy = new ItemTeddyBearToy("teddybear_toy"));
		registerItem(ModItems.sandrone_toy = new ItemSandroneToy("sandrone_toy"));
		registerItem(ModItems.gnome_toy = new ItemGardenGnomeToy("gnome_toy"));
		registerItem(ModItems.teddyqilin_toy = new ItemTeddyQilinToy("teddyqilin_toy"));
		registerItem(ModItems.tyrannosaur_toy = new ItemTyrannosaurToy("tyrannosaur_toy"));
		registerItem(ModItems.t58_toy = new ItemT58Toy("t58_toy"));
		registerItem(ModItems.sentinel_tower_toy = new ItemSentinelTowerToy("sentinel_tower_toy"));
		registerItem(ModItems.hay_farmer_toy = new ItemHayFarmerToy("hay_farmer_toy"));
		registerItem(ModItems.kleebomby_toy = new ItemKleeBombyToy("kleebomby_toy"));
		registerItem(ModItems.technodemon_toy = new ItemTechnoDemonToy("technodemon_toy"));
		registerItem(ModItems.kitsune_toy = new ItemKitsuneToy("kitsune_toy"));
		registerItem(ModItems.destroyerpity_toy = new ItemDestroyerPityToy("destroyerpity_toy"));
		registerItem(ModItems.highprincess_toy = new ItemHighPrincessToy("highprincess_toy"));
		registerItem(ModItems.crimsonbunny_toy = new ItemCrimsonBunnyToy("crimsonbunny_toy"));
		registerItem(ModItems.tinydragoness_toy = new ItemTinyDragonessToy("tinydragoness_toy"));
		registerItem(ModItems.elfarcher_toy = new ItemElfArcherToy("elfarcher_toy"));
		registerItem(ModItems.toy_command_staff = new ItemCommandToyStaff("toy_command_staff"));
		registerItem(ModItems.toy_capsule = new ItemToyCapsule("toy_capsule"));
		registerItem(ModItems.toy_upgrading_list = new ItemToyUpgradingList("toy_upgrading_list"));
		registerItem(ModItems.toy_heart_booster = new ItemToyHeartBooster("toy_heart_booster"));
		registerItem(ModItems.toy_defense_booster = new ItemToyDefenseBooster("toy_defense_booster"));
		registerItem(ModItems.toy_attack_booster = new ItemToyAttackBooster("toy_attack_booster"));
		registerItem(ModItems.toy_speed_booster = new ItemToySpeedBooster("toy_speed_booster"));
		registerItem(ModItems.blaze_rod_earring = new ItemBlazeRodEarring("blaze_rod_earring"));
		registerItem(ModItems.wither_rose_brooch = new ItemWitherRoseBrooch("wither_rose_brooch"));
		registerItem(ModItems.ghast_tear_pendant = new ItemGhastTearPendant("ghast_tear_pendant"));
		registerItem(ModItems.dragon_hairpin = new ItemDragonHairpin("dragon_hairpin"));
		registerItem(ModItems.gunner_gun = new ItemGunnerGun("gunner_gun"));
		registerItem(ModItems.watergun = new ItemWaterGun("watergun"));
		registerItem(ModItems.omocha_icon = new ItemOmochaIcon("omocha_icon"));
		registerItem(ModItems.omocha_variant_icon = new ItemOmochaVariantIcon("omocha_variant_icon"));
		registerItem(ModItems.princess_crown = new ItemPrincessCrown("princess_crown"));
	}

	public static void registerItem(final Item Item) {
		Item.setRegistryName(Item.getTranslationKey().substring(5));
		ForgeRegistries.ITEMS.register(Item);
	}

	public static void registerRenders() {
		registerRender(ModItems.toy_box);
		registerRender(ModItems.yeti_toy);
		registerRender(ModItems.bomby_toy);
		registerRender(ModItems.arrowtower_toy);
		registerRender(ModItems.spaceship_toy);
		registerRender(ModItems.gunner_toy);
		registerRender(ModItems.tank_toy);
		registerRender(ModItems.airballoon_toy);
		registerRender(ModItems.dummy_toy);
		registerRender(ModItems.dummy_toy_explosive);

		for (int i = 0; i < 16; i++) {
			String colorName = EnumDyeColor.byMetadata(i).getTranslationKey();
			registerRender(ModItems.teddybear_toy, i, "teddybear_toy_" + colorName);
		}

		registerRender(ModItems.sandrone_toy);
		registerRender(ModItems.gnome_toy);
		registerRender(ModItems.teddyqilin_toy);
		registerRender(ModItems.tyrannosaur_toy);
		registerRender(ModItems.t58_toy);
		registerRender(ModItems.sentinel_tower_toy);
		registerRender(ModItems.hay_farmer_toy);
		registerRender(ModItems.kleebomby_toy);
		registerRender(ModItems.technodemon_toy);
		registerRender(ModItems.kitsune_toy);
		registerRender(ModItems.destroyerpity_toy);
		registerRender(ModItems.highprincess_toy);
		registerRender(ModItems.crimsonbunny_toy);
		registerRender(ModItems.tinydragoness_toy);
		registerRender(ModItems.elfarcher_toy);
		registerRender(ModItems.toy_command_staff);
		registerRender(ModItems.toy_capsule);
		registerRender(ModItems.toy_upgrading_list);
		registerRender(ModItems.toy_heart_booster);
		registerRender(ModItems.toy_defense_booster);
		registerRender(ModItems.toy_attack_booster);
		registerRender(ModItems.toy_speed_booster);
		registerRender(ModItems.blaze_rod_earring);
		registerRender(ModItems.wither_rose_brooch);
		registerRender(ModItems.ghast_tear_pendant);
		registerRender(ModItems.dragon_hairpin);
		registerRender(ModItems.gunner_gun);
		registerRender(ModItems.watergun);
		registerRender(ModItems.omocha_icon);
		registerRender(ModItems.omocha_variant_icon);
		registerRender(ModItems.princess_crown);
	}

	public static void registerRender(final Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation("uut", item.getTranslationKey().substring(5)), "inventory"));
	}

	public static void registerRender(final Item item, final int meta, final String fileName) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation("uut", fileName), "inventory"));
	}
}