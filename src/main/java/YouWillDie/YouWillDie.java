package YouWillDie;

import YouWillDie.handlers.CommonTickHandler;
import YouWillDie.handlers.GUIHandler;
import YouWillDie.handlers.NewPacketHandler.ChannelHandler;
import YouWillDie.handlers.PlayerEvents;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.CommandHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

@Mod(modid = "YouWillDie", name = "You Will Die Mod (HxCEdition)", version = "1.0.1")
public class YouWillDie extends CommandHandler {

	@SidedProxy(clientSide = "YouWillDie.ClientProxy", serverSide = "YouWillDie.CommonProxy")
	public static CommonProxy proxy;
	@Instance("YouWillDie")
	public static YouWillDie instance;
	public static MinecraftServer server;
	
	public static EnumMap<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel("YWDMod", new ChannelHandler());

	public static Random r = new Random();
    public static Config config;

	public static List<Item> whitelistedItems = Arrays.asList(Items.diamond_sword, Items.golden_sword, Items.iron_sword, Items.stone_sword, Items.wooden_sword, Items.diamond_axe, Items.golden_axe, Items.iron_axe, Items.stone_axe, Items.wooden_axe, Items.diamond_pickaxe, Items.golden_pickaxe, Items.iron_pickaxe, Items.stone_pickaxe, Items.wooden_pickaxe, Items.diamond_shovel, Items.golden_shovel, Items.iron_shovel, Items.stone_shovel, Items.wooden_shovel, Items.bow);

	CommonTickHandler commonHandler = new CommonTickHandler();
	PlayerEvents pevents = new PlayerEvents();
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
        config = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        ModRegistry.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(commonHandler);
		FMLCommonHandler.instance().bus().register(pevents);
        ModRegistry.init();

		new ItemStatHelper().register();
		new EntityStatHelper().register();

		for (String str : Config.bannedItems) {
			if (str.length() != 0 && GameRegistry.findItem(str.split(":")[0], str.split(":")[1]) != null)
				whitelistedItems.add(GameRegistry.findItem(str.split(":")[0], str.split(":")[1]));
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());
		proxy.register();
	}

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
		ModRegistry.initCommands(event);
	}
}
