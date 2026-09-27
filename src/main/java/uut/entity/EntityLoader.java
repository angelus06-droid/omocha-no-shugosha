package uut.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import uut.MainClass;
import uut.entity.projectiles.*;
import uut.entity.projectiles.arrows.EntityElfArcherBlazeRod;
import uut.entity.projectiles.arrows.EntityElfArcherEndDragon;
import uut.entity.projectiles.arrows.EntityElfArcherGhastTear;
import uut.entity.projectiles.arrows.EntityElfArcherWitherRose;

public class EntityLoader {

    private static int entityId = 0;
    private static final String MOD_ID = "uut";

    public static void init() {
        registerNoEgg(EntityToyYeti.class, "toy_yeti");
        registerNoEgg(EntityToyBomby.class, "toy_bomby");
        registerNoEgg(EntityToyKleeBomby.class, "toy_kleebomby");
        registerNoEgg(EntityToyArrowTower.class, "toy_arrowtower");
        registerNoEgg(EntityToySentinelTower.class, "toy_sentinel_tower");
        registerNoEgg(EntityToySpaceship.class, "toy_spaceship");
        registerNoEgg(EntityToyT58.class, "toy_t58");
        registerNoEgg(EntityToyGunner.class, "toy_gunner");
        registerNoEgg(EntityToyTank.class, "toy_tank");
        registerNoEgg(EntityToyTechnoDemon.class, "toy_technodemon");
        registerNoEgg(EntityToyAirBalloon.class, "toy_airballoon");
        registerNoEgg(EntityToyDummy.class, "toy_dummy");
        registerNoEgg(EntityToyTeddyBear.class, "toy_teddybear");
        registerNoEgg(EntityToyTeddyQilin.class, "toy_teddyqilin");
        registerNoEgg(EntityToyDummyExplosive.class, "toy_dummy_explosive");
        registerNoEgg(EntityToySandroneDoll.class, "toy_sandrone_doll");
        registerNoEgg(EntityToyHayFarmer.class, "toy_hay_farmer");
        registerNoEgg(EntityToyGardenGnome.class, "toy_garden_gnome");
        registerNoEgg(EntityToyTyrannosaur.class, "toy_tyrannosaur");
        registerNoEgg(EntityToyKitsune.class, "toy_kitsune");
        registerNoEgg(EntityToyDestroyerPity.class, "toy_destroyerpity");
        registerNoEgg(EntityToyHighPrincess.class, "toy_high_princess");
        registerNoEgg(EntityToyCrimsonBunny.class, "toy_crimsonbunny");
        registerNoEgg(EntityToyTinyDragoness.class, "toy_tinydragoness");
        registerNoEgg(EntityToyElfArcher.class, "toy_elf_archer");

        registerNoEgg(EntityToyPrincessGoblin.class, "toy_princess_goblin_minion");
        registerNoEgg(EntityToyPrincessRecruit.class, "toy_princess_recruit_minion");
        registerNoEgg(EntityToyPrincessOgre.class, "toy_princess_ogre_minion");
        registerNoEgg(EntityToyPrincessGuard.class, "toy_princess_guard_minion");
        registerNoEgg(EntityToyPrincessEnt.class, "toy_princess_ent_minion");
        registerNoEgg(EntityToyPrincessTroll.class, "toy_princess_troll_minion");
        registerNoEgg(EntityToyPrincessShooter.class, "toy_princess_shooter_minion");

        registerNoEgg(EntitySentinelBullet.class, "sentinel_bullet");
        registerNoEgg(EntitySandroneDollProjectile.class, "sandrone_doll_missile");
        registerNoEgg(EntityTankMissle.class, "tank_missile");
        registerNoEgg(EntityTechnoDemonRocket.class, "technodemon_rocket");
        registerNoEgg(EntityToyBullet.class, "toy_bullet");
        registerNoEgg(EntityGunnerGunBullet.class, "gunner_bullet");
        registerNoEgg(EntityT58Bullet.class, "t58_bullet");
        registerNoEgg(EntityShooterBullet.class, "shooter_bullet");
        registerNoEgg(EntityWaterProjectile.class, "water_projectile");
        registerNoEgg(EntityToyWaterProjectile.class, "toy_water_projectile");
        registerNoEgg(EntityElfArcherBlazeRod.class, "blaze_rod_arrow");
        registerNoEgg(EntityElfArcherWitherRose.class, "wither_rose_arrow");
        registerNoEgg(EntityElfArcherGhastTear.class, "ghast_tear_arrow");
        registerNoEgg(EntityElfArcherEndDragon.class, "end_dragon_arrow");

        registerSpawn();
    }

    public static void register(Class<? extends Entity> entityClass, String name, int primaryColor, int secondaryColor) {
        EntityRegistry.registerModEntity(
                new ResourceLocation(MOD_ID, name), entityClass, name, ++entityId, MainClass.instance, 64, 1, true, primaryColor, secondaryColor
        );
    }

    public static void registerNoEgg(Class<? extends Entity> entityClass, String name) {
        EntityRegistry.registerModEntity(
                new ResourceLocation(MOD_ID, name), entityClass, name, ++entityId, MainClass.instance, 64, 1, true
        );
    }

    public static void registerSpawn() {
    }
}