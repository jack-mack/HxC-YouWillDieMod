package YouWillDie.commands;

import YouWillDie.handlers.CommonTickHandler;
import YouWillDie.handlers.NewPacketHandler;
import YouWillDie.worldgen.WorldData;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class CommandCurrentWorldTask implements ICommand {

	@Override
	public int compareTo(Object arg0) {return 0;}

	@Override
	public String getCommandName() {return "currentGoal";}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {return "commands.currentGoal.usage";}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {return null;}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;

			String name = CommonTickHandler.worldData.currentTask.equals("Kill") ? WorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : WorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName();

			if(CommonTickHandler.worldData.currentTaskAmount > 1) {
				if(name.contains("Block")) {if(name.contains("Block")) {name = name.replace("Block", "Blocks").replace("block", "blocks");}}
				else {name += "s";}
			}

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, "\u00A7eWorld goal: " + CommonTickHandler.worldData.currentTask + " " + CommonTickHandler.worldData.currentTaskAmount + " " + name + ". (" + CommonTickHandler.worldData.progress + " " + CommonTickHandler.worldData.currentTask + "ed)");

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, "\u00A7f   Reward: " + CommonTickHandler.worldData.rewardLevels + " Experience Levels");

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, "\u00A7eGoals completed: " + CommonTickHandler.worldData.tasksCompleted);
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
