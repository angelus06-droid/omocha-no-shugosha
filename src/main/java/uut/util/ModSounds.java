package uut.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod.EventBusSubscriber(modid = "uut")
public class ModSounds {
	@ObjectHolder("uut:item.toy_box_open")
	public static final SoundEvent toy_box_open = null;

	@ObjectHolder("uut:item.gunner_gun.shoot")
	public static final SoundEvent gunner_gun_shoot = null;

	@ObjectHolder("uut:item.watergun.shoot")
	public static final SoundEvent watergun_shoot = null;

	@ObjectHolder("uut:entity.t58.death")
	public static final SoundEvent t58_death = null;

	@ObjectHolder("uut:entity.t58.shoot")
	public static final SoundEvent t58_shoot = null;

	@ObjectHolder("uut:entity.sentinel_tower.shoot")
	public static final SoundEvent sentinel_tower_shoot = null;

	@ObjectHolder("uut:entity.teddyqilin.say")
	public static final SoundEvent teddyqilin_say = null;

	@ObjectHolder("uut:entity.teddyqilin.hurt")
	public static final SoundEvent teddyqilin_hurt = null;

	@ObjectHolder("uut:entity.teddyqilin.death")
	public static final SoundEvent teddyqilin_death = null;

	@ObjectHolder("uut:entity.tyrannosaur.say")
	public static final SoundEvent tyrannosaur_say = null;

	@ObjectHolder("uut:entity.tyrannosaur.hurt")
	public static final SoundEvent tyrannosaur_hurt = null;

	@ObjectHolder("uut:entity.tyrannosaur.death")
	public static final SoundEvent tyrannosaur_death = null;

	@ObjectHolder("uut:entity.kleebomby.say")
	public static final SoundEvent kleebomby_say = null;

	@ObjectHolder("uut:entity.kleebomby.hurt")
	public static final SoundEvent kleebomby_hurt = null;

	@ObjectHolder("uut:entity.kleebomby.death")
	public static final SoundEvent kleebomby_death = null;

	@ObjectHolder("uut:entity.kleebomby.attack")
	public static final SoundEvent kleebomby_attack = null;

	@ObjectHolder("uut:entity.kitsune.say")
	public static final SoundEvent kitsune_say = null;

	@ObjectHolder("uut:entity.kitsune.hurt")
	public static final SoundEvent kitsune_hurt = null;

	@ObjectHolder("uut:entity.kitsune.death")
	public static final SoundEvent kitsune_death = null;

	@ObjectHolder("uut:entity.destroyer_pity.say")
	public static final SoundEvent destroyer_pity_say = null;

	@ObjectHolder("uut:entity.destroyer_pity.hurt")
	public static final SoundEvent destroyer_pity_hurt = null;

	@ObjectHolder("uut:entity.destroyer_pity.death")
	public static final SoundEvent destroyer_pity_death = null;

	@ObjectHolder("uut:entity.high_princess.prepare_summon")
	public static final SoundEvent high_princess_prepare_summon = null;

	@ObjectHolder("uut:entity.tiny_dragoness.say")
	public static final SoundEvent tiny_dragoness_say = null;

	@ObjectHolder("uut:entity.tiny_dragoness.hurt")
	public static final SoundEvent tiny_dragoness_hurt = null;

	@ObjectHolder("uut:entity.tiny_dragoness.death")
	public static final SoundEvent tiny_dragoness_death = null;

	@ObjectHolder("uut:entity.sandrone_doll.shoot")
	public static final SoundEvent sandrone_doll_shoot = null;

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(
				createSound("item.toy_box_open"),
				createSound("item.gunner_gun.shoot"),
				createSound("item.watergun.shoot"),
				createSound("entity.t58.death"),
				createSound("entity.t58.shoot"),
				createSound("entity.sentinel_tower.shoot"),
				createSound("entity.teddyqilin.say"),
				createSound("entity.teddyqilin.hurt"),
				createSound("entity.teddyqilin.death"),
				createSound("entity.tyrannosaur.say"),
				createSound("entity.tyrannosaur.hurt"),
				createSound("entity.tyrannosaur.death"),
				createSound("entity.kleebomby.say"),
				createSound("entity.kleebomby.hurt"),
				createSound("entity.kleebomby.death"),
				createSound("entity.kleebomby.attack"),
				createSound("entity.kitsune.say"),
				createSound("entity.kitsune.hurt"),
				createSound("entity.kitsune.death"),
				createSound("entity.destroyer_pity.say"),
				createSound("entity.destroyer_pity.hurt"),
				createSound("entity.destroyer_pity.death"),
				createSound("entity.high_princess.prepare_summon"),
				createSound("entity.tiny_dragoness.say"),
				createSound("entity.tiny_dragoness.hurt"),
				createSound("entity.tiny_dragoness.death"),
				createSound("entity.sandrone_doll.shoot")
		);
	}

	private static SoundEvent createSound(String name) {
		ResourceLocation id = new ResourceLocation("uut", name);
		return new SoundEvent(id).setRegistryName(id);
	}
}