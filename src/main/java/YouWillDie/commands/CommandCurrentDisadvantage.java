package YouWillDie.commands;

import YouWillDie.handlers.CommonTickHandler;
import YouWillDie.handlers.NewPacketHandler;
import YouWillDie.worldgen.WorldData;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class CommandCurrentDisadvantage implements ICommand {

	@Override
	public int compareTo(Object arg0) {return 0;}

	@Override
	public String getCommandName() {return "currentDisadvantage";}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {return "commands.currentDisadvantage.usage";}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {return null;}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;

			int index = -1;
			for(int i = 0; i < WorldData.validDisadvantages.length; i++) {if(WorldData.validDisadvantages[i].equals(CommonTickHandler.worldData.getDisadvantage())) {index = i; break;}}
			NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, "\u00A7eWorld disadvantage: " + CommonTickHandler.worldData.getDisadvantage() + (index == -1 ? "" : " (" + WorldData.disadvantageDescriptions[index] + ")"));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return true;}

	@Override
	@SuppressWarnings("rawtypes")
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {return false;}
}
