package uut.proxy;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.item.*;
import uut.item.ModItems;

public abstract class CommonProxy
{
    public void preInit(final FMLPreInitializationEvent e) {
        ModItems.register();
    }
    
    public void init(final FMLInitializationEvent e) {
    }

    
    public void postInit(final FMLPostInitializationEvent e) {
    }

    public abstract void init();

    public void registerRenders() {
    }
    
    public void registerItemRenderer(final Item item, final int meta, final String id) {
    }
    public void spawnDragonessFlame(World world, double x, double y, double z, double vx, double vy, double vz) {}
}
