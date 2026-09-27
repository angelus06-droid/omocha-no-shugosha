package uut.proxy;

import net.minecraft.world.World;
import uut.entity.render.*;
import net.minecraftforge.fml.common.event.*;
import uut.item.ModItems;
import uut.particle.ModParticles;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(final FMLPreInitializationEvent e) {
        EntityRenderRegistry.Load();
        super.preInit(e);
    }

    @Override
    public void init(final FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void registerRenders() {
        ModItems.registerRenders();
    }

    @Override
    public void postInit(final FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public void init() {
    }
    @Override
    public void spawnDragonessFlame(World world, double x, double y, double z, double vx, double vy, double vz) {
        ModParticles.spawnDragonessFlame(world, x, y, z, vx, vy, vz);
    }
}
