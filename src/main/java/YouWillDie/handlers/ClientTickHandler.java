package YouWillDie.handlers;

import YouWillDie.ModRegistry;
import YouWillDie.misc.ItemStatHelper;
import YouWillDie.network.PacketHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ClientTickHandler {
	
	public static long time = System.currentTimeMillis();

	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if(System.currentTimeMillis() - time > 200 && player != null) {
			if(player.openContainer != null) {
				boolean sendPacket = false;

				for(Object object : player.inventory.mainInventory) {
					if(object == null || !(object instanceof ItemStack)) {continue;}

					ItemStack stack = (ItemStack) object;
					
					if(stack.getItem() != null && !ItemStatHelper.skip.contains(stack.getItem().getClass()) && (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("processed") || stack.getTagCompound().getString("processed").equals("false"))) {
						sendPacket = true;
					}
				}

				if(sendPacket) {
					PacketHandler.UPDATE_PLAYER_ITEMS.sendToServer((Object) null);
					time = System.currentTimeMillis();
				}
			}

			player.triggerAchievement(ModRegistry.startTheGame);
		}
	}
}
