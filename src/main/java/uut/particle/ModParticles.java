package uut.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModParticles {

    public static final IParticleFactory DRAGONESS_FLAME_FACTORY = new FactoryDragonessFlame();
    public static final IParticleFactory SANDRONE_DOLL_MISSILE_FACTORY = new FactorySandroneDollMissile();

    public static class FactoryDragonessFlame implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticleDragonessFlame(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }

    public static class FactorySandroneDollMissile implements IParticleFactory {
        @Override
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new ParticleSandroneDollMissile(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }

    public static Particle spawnDragonessFlame(World world, double x, double y, double z, double vx, double vy, double vz) {
        if (world.isRemote) {
            Particle particle = DRAGONESS_FLAME_FACTORY.createParticle(0, world, x, y, z, vx, vy, vz);
            if (particle != null) {
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                return particle;
            }
        }
        return null;
    }

    public static Particle spawnSandroneDollMissile(World world, double x, double y, double z, double vx, double vy, double vz) {
        if (world.isRemote) {
            if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
                return null;
            }

            Particle particle = SANDRONE_DOLL_MISSILE_FACTORY.createParticle(0, world, x, y, z, vx, vy, vz);
            if (particle != null) {
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                return particle;
            }
        }
        return null;
    }
}