package uut.entity.render;

import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.Entity;
import uut.entity.*;
import uut.entity.projectiles.*;
import uut.entity.projectiles.arrows.EntityElfArcherBlazeRod;
import uut.entity.projectiles.arrows.EntityElfArcherEndDragon;
import uut.entity.projectiles.arrows.EntityElfArcherGhastTear;
import uut.entity.projectiles.arrows.EntityElfArcherWitherRose;

public class EntityRenderRegistry {

    public static void Load() {
        register(EntityToyYeti.class, RenderToyYeti::new);
        register(EntityToyBomby.class, RenderToyBomby::new);
        register(EntityToyKleeBomby.class, RenderToyKleeBomby::new);
        register(EntityToyArrowTower.class, RenderToyArrowTower::new);
        register(EntityToySentinelTower.class, RenderToySentinelTower::new);
        register(EntityToySpaceship.class, RenderToySpaceship::new);
        register(EntityToyT58.class, RenderToyT58::new);
        register(EntityToyGunner.class, RenderToyGunner::new);
        register(EntityToyTank.class, RenderToyTank::new);
        register(EntityToyTechnoDemon.class, RenderToyTechnoDemon::new);
        register(EntityToyAirBalloon.class, RenderToyAirBalloon::new);
        register(EntityToyDummy.class, RenderToyDummy::new);
        register(EntityToyDummyExplosive.class, RenderToyDummyExplosive::new);
        register(EntityToyTeddyBear.class, RenderToyTeddyBear::new);
        register(EntityToyTeddyQilin.class, RenderToyTeddyQilin::new);
        register(EntityToySandroneDoll.class, RenderToySandroneDoll::new);
        register(EntityToyHayFarmer.class, RenderToyHayFarmer::new);
        register(EntityToyGardenGnome.class, RenderToyGardenGnome::new);
        register(EntityToyTyrannosaur.class, RenderToyTyrannosaur::new);
        register(EntityToyKitsune.class, RenderToyKitsune::new);
        register(EntityToyDestroyerPity.class, RenderToyDestroyerPity::new);
        register(EntityToyHighPrincess.class, RenderToyHighPrincess::new);
        register(EntityToyCrimsonBunny.class, RenderToyCrimsonBunny::new);
        register(EntityToyTinyDragoness.class, RenderToyTinyDragoness::new);
        register(EntityToyElfArcher.class, RenderToyElfArcher::new);

        register(EntityToyPrincessGoblin.class, RenderToyPrincessGoblin::new);
        register(EntityToyPrincessRecruit.class, RenderToyPrincessRecruit::new);
        register(EntityToyPrincessOgre.class, RenderToyPrincessOgre::new);
        register(EntityToyPrincessGuard.class, RenderToyPrincessGuard::new);
        register(EntityToyPrincessEnt.class, RenderToyPrincessEnt::new);
        register(EntityToyPrincessTroll.class, RenderToyPrincessTroll::new);
        register(EntityToyPrincessShooter.class, RenderToyPrincessShooter::new);

        register(EntityTankMissle.class, RenderTankMissle::new);
        register(EntityTechnoDemonRocket.class, RenderTechnoDemonRocket::new);
        register(EntityToyBullet.class, RenderToyBullet::new);
        register(EntityGunnerGunBullet.class, RenderGunnerGunBullet::new);
        register(EntityT58Bullet.class, RenderT58Bullet::new);
        register(EntitySentinelBullet.class, RenderSentinelMissle::new);
        register(EntitySandroneDollProjectile.class, RenderSandroneDollProjectile::new);
        register(EntityWaterProjectile.class, RenderWaterProjectile::new);
        register(EntityToyWaterProjectile.class, RenderToyWaterProjectile::new);
        register(EntityShooterBullet.class, manager -> new RenderShooterBullet(manager, 0.3f));

        register(EntityElfArcherBlazeRod.class, RenderElfArcherBlazeRod::new);
        register(EntityElfArcherWitherRose.class, RenderElfArcherWitherRose::new);
        register(EntityElfArcherGhastTear.class, RenderElfArcherGhastTear::new);
        register(EntityElfArcherEndDragon.class, RenderElfArcherEndDragon::new);
    }

    private static <T extends Entity> void register(Class<T> entityClass, IRenderFactory<? super T> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
    }
}