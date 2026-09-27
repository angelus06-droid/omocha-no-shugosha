package uut;

import uut.proxy.*;
import net.minecraftforge.fml.common.*;
import uut.entity.*;
import net.minecraftforge.fml.common.event.*;

@Mod(
        modid = "uut",
        version = "2.0.0",
        name = "Omocha no Shugosha",
        acceptedMinecraftVersions = "[1.12]",
        guiFactory = "uut.config.GuiFactory"
)
public class MainClass
{
    public static final String MODID = "uut";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "./";
    @SidedProxy(clientSide = "uut.proxy.ClientProxy", serverSide = "uut.proxy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance("uut")
    public static MainClass instance;
    public static final String ASSET_PREFIX = "uut";
    public static final String TEXTURE_PREFIX = "uut:";
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent e) {
        e.getModMetadata().logoFile = "assets/uut/logo.png";
        MainClass.proxy.preInit(e);
        MainClass.proxy.registerRenders();
        EntityLoader.init();
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent e) {
        MainClass.proxy.init(e);
    }
    
    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent e) {
        MainClass.proxy.postInit(e);
    }

}
