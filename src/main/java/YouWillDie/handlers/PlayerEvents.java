package YouWillDie.handlers;

import YouWillDie.Config;
import YouWillDie.ModRegistry;
import YouWillDie.misc.EntityStatHelper;
import YouWillDie.network.PacketHandler;
import YouWillDie.tileentities.TileEntityPillar;
import YouWillDie.worldgen.WorldData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class PlayerEvents {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;

        StatTrackers.TrackStats();
        if(!player.worldObj.isRemote) {
            PacketHandler.UPDATE_WORLD_DATA.sendToPlayer(player, CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, Config.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);

            String name = CommonTickHandler.worldData.currentTask.equals("Kill") ? WorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : WorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName();

            if(CommonTickHandler.worldData.currentTaskAmount > 1) {
                if(name.contains("Block")) {name = name.replace("Block", "Blocks").replace("block", "blocks");}
                else {name += "s";}
            }

            int index = -1;
            for(int i = 0; i < WorldData.validDisadvantages.length; i++) {if(WorldData.validDisadvantages[i].equals(CommonTickHandler.worldData.getDisadvantage())) {index = i; break;}}

            player.addChatComponentMessage(new ChatComponentText("\u00A7eWorld disadvantage: " + CommonTickHandler.worldData.getDisadvantage() + (index == -1 ? "" : " (" + WorldData.disadvantageDescriptions[index] + ")")));
            player.addChatComponentMessage(new ChatComponentText("\u00A7eWorld goal: " + CommonTickHandler.worldData.currentTask + " " + CommonTickHandler.worldData.currentTaskAmount + " " + name + ". (" + CommonTickHandler.worldData.progress + " " + CommonTickHandler.worldData.currentTask + "ed)"));

            if(!player.getEntityData().hasKey("Blessing")) {
                player.getEntityData().setString("Blessing", TileEntityPillar.validBlessings[YouWillDie.YouWillDie.r.nextInt(TileEntityPillar.validBlessings.length)]);
                while(player.getEntityData().getString("Blessing").equals("Inferno")) {player.getEntityData().setString("Blessing", TileEntityPillar.validBlessings[YouWillDie.YouWillDie.r.nextInt(TileEntityPillar.validBlessings.length)]);}

                PacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A72You've been granted the Blessing of the " + player.getEntityData().getString("Blessing") + ". (Use /currentBlessing to check effect)");
            }

            PacketHandler.UPDATE_BLESSING.sendToPlayer(player, player.getEntityData().getString("Blessing"));

            if(EntityStatHelper.hasStat(player, "BlessingCounter")) {
                PacketHandler.UPDATE_STAT.sendToPlayer(player, "BlessingCounter", EntityStatHelper.getStat(player, "BlessingCounter"));}

            if(!player.getEntityData().hasKey("PotionKnowledge")) {player.getEntityData().setIntArray("PotionKnowledge", new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});}

            PacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer(player, player.getEntityData().getIntArray("PotionKnowledge"));
        }
        player.triggerAchievement(ModRegistry.startTheGame);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;

        PacketHandler.UPDATE_WORLD_DATA.sendToPlayer(player, CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, Config.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);

        PacketHandler.UPDATE_BLESSING.sendToPlayer(player, player.getEntityData().getString("Blessing"));

        if(EntityStatHelper.hasStat(player, "BlessingCounter")) {
            PacketHandler.UPDATE_STAT.sendToPlayer(player, "BlessingCounter", EntityStatHelper.getStat(player, "BlessingCounter"));}

        if(!player.getEntityData().hasKey("PotionKnowledge")) {player.getEntityData().setIntArray("PotionKnowledge", new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});}

        PacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer(player, player.getEntityData().getIntArray("PotionKnowledge"));
    }

    @SubscribeEvent
    public void checkBreakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        if(event.entityPlayer != null && event.entityPlayer.getEntityData().hasKey("Blessing")) {
            String blessing = event.entityPlayer.getEntityData().getString("Blessing");

            if(blessing.equals("Miner")) {
                if(event.block.getMaterial() == Material.rock || event.block.getMaterial() == Material.iron) {event.newSpeed = event.originalSpeed * 1.25F;}
            } else if(blessing.equals("Lumberjack")) {
                if(event.block.getMaterial() == Material.wood) {event.newSpeed = event.originalSpeed * 1.25F;}
            }
        }
    }

}
