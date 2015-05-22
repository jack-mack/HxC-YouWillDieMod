package YouWillDie;

import YouWillDie.handlers.CommonTickHandler;
import YouWillDie.handlers.GUIHandler;
import YouWillDie.handlers.NewPacketHandler.ChannelHandler;
import YouWillDie.misc.EntityStatHelper;
import YouWillDie.misc.ItemStatHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.CommandHandler;
import net.minecraftforge.common.config.Configuration;

import java.util.EnumMap;
import java.util.Random;

@Mod(modid = "YouWillDie", name = "You Will Die Mod", version = "0.0.4")
public class YouWillDie extends CommandHandler {

	@SidedProxy(clientSide = "YouWillDie.ClientProxy", serverSide = "YouWillDie.CommonProxy")
	public static CommonProxy proxy;

	@Instance("YouWillDie")
	public static YouWillDie instance;
	
	public static EnumMap<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel("YWDMod", new ChannelHandler());

	public static Random r = new Random();
    public static Config config;
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
        config = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        ModRegistry.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		CommonTickHandler commonHandler = new CommonTickHandler();
		FMLCommonHandler.instance().bus().register(commonHandler);
        ModRegistry.init();

		new ItemStatHelper().register();
		new EntityStatHelper().register();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
		proxy.register();
	}

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
		ModRegistry.initCommands(event);
	}
}
