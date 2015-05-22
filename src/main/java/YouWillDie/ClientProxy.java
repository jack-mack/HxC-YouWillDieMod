package YouWillDie;

import YouWillDie.entities.EntityMysteryPotion;
import YouWillDie.entities.renderers.RenderMysteryPotion;
import YouWillDie.handlers.ClientTickHandler;
import YouWillDie.handlers.KeyHandler;
import YouWillDie.misc.EntityStatHelper;
import YouWillDie.tileentities.TileEntityCrystal;
import YouWillDie.tileentities.TileEntityCrystalStand;
import YouWillDie.tileentities.TileEntityPillar;
import YouWillDie.tileentities.TileEntityTrap;
import YouWillDie.tileentities.renderers.TileEntityCrystalRenderer;
import YouWillDie.tileentities.renderers.TileEntityCrystalStandRenderer;
import YouWillDie.tileentities.renderers.TileEntityPillarRenderer;
import YouWillDie.tileentities.renderers.TileEntityTrapRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClientProxy extends CommonProxy {

	@SubscribeEvent
	public void guiRenderEvent(RenderGameOverlayEvent.Post event) {
		if(event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
			MovingObjectPosition mouse = Minecraft.getMinecraft().objectMouseOver;

			World world = Minecraft.getMinecraft().theWorld;

			if(mouse != null && world != null && mouse.typeOfHit == MovingObjectType.BLOCK) {
				TileEntity te = world.getTileEntity(mouse.blockX, mouse.blockY, mouse.blockZ);
				Block id = world.getBlock(mouse.blockX, mouse.blockY, mouse.blockZ);

				if(id == ModRegistry.blockPillar || (id == ModRegistry.blockTrap && te != null && te instanceof TileEntityTrap && ((TileEntityTrap) te).placedBy != null)) {
					String key = Keyboard.getKeyName(KeyHandler.examine.getKeyCode());
					String string = "Press " + key + " to Examine";

					if(te != null && Minecraft.getMinecraft().thePlayer != null && te instanceof TileEntityTrap && ((TileEntityTrap) te).placedBy.equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())) {
						string = Minecraft.getMinecraft().thePlayer.isSneaking() ? "Use to disarm (Stand to toggle setting)" : "Use to toggle setting (Sneak to disarm)";
					}

					Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (event.resolution.getScaledWidth() / 2) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(string) / 2), event.resolution.getScaledHeight() / 2 + 16, Color.WHITE.getRGB());
				}
			}

			if(Minecraft.getMinecraft().thePlayer != null) {
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				String blessing = EntityStatHelper.getStat(player, "Blessing");

				if(blessing != null) {
					if(blessing.equals("Berserker")) {
						if(!EntityStatHelper.hasStat(player, "BlessingCounter")) {EntityStatHelper.giveStat(player, "BlessingCounter", 0);}
						String string = EntityStatHelper.getStat(player, "BlessingCounter");
						Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (event.resolution.getScaledWidth() / 2) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(string) / 2), event.resolution.getScaledHeight() - 48 + (player.capabilities.isCreativeMode ? 16 : 0), Color.RED.getRGB());
					}
				}
			}
		}
	}

	@Override
	public void register() {
		ClientTickHandler clientHandler = new ClientTickHandler();
		FMLCommonHandler.instance().bus().register(clientHandler);

		FMLCommonHandler.instance().bus().register(new KeyHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPillar.class, new TileEntityPillarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrap.class, new TileEntityTrapRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystal.class, new TileEntityCrystalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalStand.class, new TileEntityCrystalStandRenderer());

		RenderingRegistry.registerEntityRenderingHandler(EntityMysteryPotion.class, new RenderMysteryPotion(ModRegistry.mysteryPotion));

		MinecraftForge.EVENT_BUS.register(this);
	}
}
