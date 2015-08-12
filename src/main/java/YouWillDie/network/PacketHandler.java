package YouWillDie.network;

import YouWillDie.ModRegistry;
import YouWillDie.YouWillDie;
import YouWillDie.blocks.BlockTrap;
import YouWillDie.handlers.CommonTickHandler;
import YouWillDie.misc.EntityStatHelper;
import YouWillDie.misc.ItemStatHelper;
import YouWillDie.tileentities.TileEntityTrap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

public class PacketHandler {
    static BasicPacket[] packetTypes = new BasicPacket[256];

    static int[] potionValues = new int[12];
    static int[] potionDurations = new int[12];

    static int[][] mushroomColors = new int[13][2];

    static String currentDisadvantage = null;

    static String currentTask = null;
    static int currentTaskID = -1;
    static int currentTaskAmount = 0;
    static int progress = 0;
    static int tasksCompleted = 0;
    static int rewardLevels = 0;

    static boolean enderDragonKilled = false;
    static boolean trapsDisabled = false;

    //Beginning Basic packet class
    public static class BasicPacket implements IPacket {
        public static Class[] validClassArray = {Integer.class, Boolean.class, String.class, Character.class, Byte.class, Float.class, Double.class, int[].class, int[][].class};
        public static ArrayList<Class> validClasses = new ArrayList<Class>();
        static {
            Collections.addAll(validClasses, validClassArray);
        }

        public Object[] data = null;
        public byte type;

        public BasicPacket() {}

        public BasicPacket(int type) {
            if(type == 0 || packetTypes[type] != null) {throw new RuntimeException("Packet slot " + type + " already in use.");}
            packetTypes[type] = this;
            this.type = (byte) type;
        }

        public BasicPacket(BasicPacket packet, Object... data) {
            if(packet.type > 0 && packet.type < packetTypes.length && packet == packetTypes[packet.type]) {type = packet.type;} else {throw new RuntimeException("Must supply valid packet type.");}

            Class[] classes = getExpectedClasses();

            if(classes != null) {
                for(Class c : classes) {
                    if(!validClasses.contains(c)) {throw new RuntimeException("Argument class not valid. (" + c + ")");}
                }

                if(data == null || data.length != classes.length) {
                    throw new RuntimeException("Wrong number of arguments provided.");
                } else {
                    for(int i = 0; i < data.length; i++) {
                        if(data[i].getClass() != classes[i]) {throw new RuntimeException("Wrong argument class provided. (" + data[i].getClass() + ", expected " + classes[i] + ")");}
                    }
                }
            }

            this.data = data;
        }

        @Override
        public void readBytes(ByteBuf bytes) {
            type = bytes.readByte();

            Class[] classes = getExpectedClasses();

            if(classes != null) {
                data = new Object[classes.length];

                for(int i = 0; i < classes.length; i++) {
                    if(classes[i] == Integer.class) {data[i] = bytes.readInt();}
                    if(classes[i] == int[].class) {int[] array = new int[bytes.readInt()]; for(int i2 = 0; i2 < array.length; i2++) {array[i2] = bytes.readInt();} data[i] = array;}
                    if(classes[i] == int[][].class) {
                        int[][] array = new int[bytes.readInt()][];

                        for(int i2 = 0; i2 < array.length; i2++) {
                            array[i2] = new int[bytes.readInt()];
                            for(int i3 = 0; i3 < array[i2].length; i3++) {array[i2][i3] = bytes.readInt();}
                        }

                        data[i] = array;
                    } else if(classes[i] == Boolean.class) {data[i] = bytes.readBoolean();}
                    else if(classes[i] == String.class) {
                        int length = bytes.readInt();
                        try{
                            byte[] stringBytes = new byte[length];
                            bytes.readBytes(stringBytes);
                            data[i] = new String(stringBytes, "UTF-8");
                        } catch(Exception e) {e.printStackTrace();}
                    } else if(classes[i] == Byte.class) {data[i] = bytes.readByte();}
                    else if(classes[i] == Float.class) {data[i] = bytes.readFloat();}
                    else if(classes[i] == Double.class) {data[i] = bytes.readDouble();}
                    else if(classes[i] == Character.class) {data[i] = bytes.readChar();}
                }
            }
        }

        @Override
        public void writeBytes(ByteBuf bytes) {
            bytes.writeByte(type);

            if(data != null) {
                for (Object aData : data) {
                    if (aData instanceof Integer) {
                        bytes.writeInt((Integer) aData);
                    } else if (aData instanceof int[]) {
                        bytes.writeInt(((int[]) aData).length);
                        for (int i2 = 0; i2 < ((int[]) aData).length; i2++) {
                            bytes.writeInt(((int[]) aData)[i2]);
                        }
                    } else if (aData instanceof int[][]) {
                        int[][] values = (int[][]) aData;

                        bytes.writeInt(values.length);

                        for (int[] value : values) {
                            bytes.writeInt(value.length);
                            for (int aValue : value) {
                                bytes.writeInt(aValue);
                            }
                        }
                    } else if (aData instanceof Boolean) {
                        bytes.writeBoolean((Boolean) aData);
                    } else if (aData instanceof String) {
                        byte[] stringBytes = ((String) aData).getBytes(Charset.forName("UTF-8"));
                        bytes.writeInt(stringBytes.length);
                        bytes.writeBytes(stringBytes);
                    } else if (aData instanceof Byte) {
                        bytes.writeByte((Byte) aData);
                    } else if (aData instanceof Float) {
                        bytes.writeFloat((Float) aData);
                    } else if (aData instanceof Double) {
                        bytes.writeDouble((Double) aData);
                    } else if (aData instanceof Character) {
                        bytes.writeChar((Character) aData);
                    }
                }
            }
        }

        @Override
        public void executeClient(EntityPlayer player) {
            if(packetTypes[type] != this) {packetTypes[type].executeClient(player);}
        }

        @Override
        public void executeServer(EntityPlayer player) {
            if(packetTypes[type] != this) {packetTypes[type].executeServer(player);}
        }

        @Override
        public void executeBoth(EntityPlayer player) {
            if(packetTypes[type] != this) {packetTypes[type].executeBoth(player);}
        }

        public Class[] getExpectedClasses() {if(packetTypes[type] != this) {return packetTypes[type].getExpectedClasses();} else {return null;}}

        public void sendToPlayer(EntityPlayer player, Object... data) {
            sendPacketToPlayer(new BasicPacket(this, data), player);
        }

        public void sendToAllPlayers(Object... data) {
            sendPacketToAllPlayers(new BasicPacket(this, data));
        }

        public void sendToServer(Object... data) {
            sendPacketToServer(new BasicPacket(this, data));
        }
    }
    //End of Basic Packet Class

    //begin of Channel Handler Class
    public static class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket> {

        public ChannelHandler() {
            addDiscriminator(0, BasicPacket.class);
        }

        @Override
        public void encodeInto(ChannelHandlerContext ctx, IPacket packet, ByteBuf data) throws Exception {
            packet.writeBytes(data);
        }

        @Override
        public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, IPacket packet) {
            packet.readBytes(data);

            if(packet instanceof BasicPacket) {packetTypes[((BasicPacket) packet).type].data = ((BasicPacket) packet).data;}

            EntityPlayer player;

            switch (FMLCommonHandler.instance().getEffectiveSide()) {

                case CLIENT:

                    player = getClientPlayer();

                    if(player != null) {
                        packet.executeClient(player);
                        packet.executeBoth(player);
                    }

                    break;

                case SERVER:

                    INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                    player = ((NetHandlerPlayServer) netHandler).playerEntity;

                    if(player != null) {
                        packet.executeServer(player);
                        packet.executeBoth(player);
                    }

                    break;

                default: break;

            }
        }

        @SideOnly(Side.CLIENT)
        public EntityPlayer getClientPlayer() {return Minecraft.getMinecraft().thePlayer;}
    }
    //end of Channel handler class

    public static void sendPacketToPlayer(IPacket packet, EntityPlayer player) {
        YouWillDie.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        YouWillDie.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        YouWillDie.channels.get(Side.SERVER).writeOutbound(packet);
    }

    public static void sendPacketToAllPlayers(IPacket packet) {
        YouWillDie.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        YouWillDie.channels.get(Side.SERVER).writeOutbound(packet);
    }

    public static void sendPacketToServer(IPacket packet) {
        YouWillDie.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        YouWillDie.channels.get(Side.CLIENT).writeOutbound(packet);
    }

    public static final BasicPacket

    UPDATE_BLESSING = new BasicPacket(1) {
        @Override
        public void executeBoth(EntityPlayer player) {player.getEntityData().setString("Blessing", (String) data[0]);}
        @Override
        public Class[] getExpectedClasses() {return new Class[] {String.class};}
    },

    PLAY_SOUND = new BasicPacket(2) {
        @Override
        public void executeServer(EntityPlayer player) {
            String sound = (String) data[0];
            int x = (Integer) data[1], y = (Integer) data[2], z = (Integer) data[3];
            player.worldObj.playSound(x, y, z, "youwilldie:" + sound, 1.0F, 1.0F, false);
        }

        @Override
        public Class[] getExpectedClasses() {return new Class[] {String.class, Integer.class, Integer.class, Integer.class};}
    },

    UPDATE_POTION_KNOWLEDGE = new BasicPacket(3) {
        @Override
        public void executeBoth(EntityPlayer player) {
            player.getEntityData().setIntArray("PotionKnowledge", (int[]) data[0]);
        }
        @Override
        public Class[] getExpectedClasses() {return new Class[] {int[].class};}
    },

    SEND_MESSAGE = new BasicPacket(4) {
        @Override
        public void executeClient(EntityPlayer player) {
            String style = "";

            for(String s : ((String) data[0]).split("@")) {
                String[] words = s.split(" ");
                s = "";

                for(String word : words) {
                    s += style + word + " ";

                    while(word.contains("\u00A7")) {
                        int firstOccurance = word.indexOf("\u00A7");
                        String string = word.substring(firstOccurance, firstOccurance + 2);

                        if(style.contains(string)) {style = style.replace(string, "");}
                        style += string;
                        if(string.equals("\u00A7r")) {style = "";}

                        word = word.replaceFirst(string, "");
                    }
                }

                player.addChatComponentMessage(new ChatComponentText(s));
            }
        }

        @Override
        public Class[] getExpectedClasses() {return new Class[] {String.class};}
    },

    UPDATE_WORLD_DATA = new BasicPacket(5) {
        @Override
        public void executeClient(EntityPlayer player) {
            potionValues = (int[]) data[0];
            potionDurations = (int[]) data[1];
            currentDisadvantage = (String) data[2];
            currentTask = (String) data[3];
            currentTaskID = (Integer) data[4];
            currentTaskAmount = (Integer) data[5];
            progress = (Integer) data[6];
            tasksCompleted = (Integer) data[7];
            enderDragonKilled = (Boolean) data[8];
            trapsDisabled = !((Boolean) data[9]);
            rewardLevels = (Integer) data[10];
            mushroomColors = (int[][]) data[11];
        }

        @Override
        public Class[] getExpectedClasses() {return new Class[] {int[].class, int[].class, String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Boolean.class, Boolean.class, Integer.class, int[][].class};}
    },

    UPDATE_PLAYER_ITEMS = new BasicPacket(6) {
        @Override
        public void executeServer(EntityPlayer player) {
            for(Object stack : player.inventory.mainInventory) {
                if(stack == null || !(stack instanceof ItemStack) || !YouWillDie.whitelistedItems.contains(stack) ) {continue;}
                ItemStatHelper.processItemStack((ItemStack) stack, YouWillDie.r);
            }
        }

        @Override
        public Class[] getExpectedClasses() {return null;}
    },

    DISARM_TRAP = new BasicPacket(7) {
        @Override
        public void executeServer(EntityPlayer player) {
            int blockX = (Integer) data[0], blockY = (Integer) data[1], blockZ = (Integer) data[2];
            boolean mechanic = (Boolean) data[3];

            String blessing = null;
            if(player.getEntityData().hasKey("Blessing")) {blessing = player.getEntityData().getString("Blessing");}
            boolean scout = blessing != null && blessing.equals("Scout");

            TileEntity te = player.worldObj.getTileEntity(blockX, blockY, blockZ);

            boolean yours = (!(te == null || !(te instanceof TileEntityTrap))) && player.getCommandSenderName().equals(((TileEntityTrap) te).placedBy);

            if(yours || (mechanic ? YouWillDie.r.nextInt(4) != 0 : YouWillDie.r.nextInt(4) == 0)) {
                boolean salvage = yours || (mechanic ? YouWillDie.r.nextBoolean() : (YouWillDie.r.nextInt(4) == 0));

                SEND_MESSAGE.sendToPlayer(player, "\u00A7e\u00A7o" + (!salvage ? "You disarmed the trap." : "You disarm and salvage the trap."));

                if(salvage) {player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, blockX + 0.5F, blockY, blockZ + 0.5F, new ItemStack(ModRegistry.itemTrap, 1, player.worldObj.getBlockMetadata(blockX, blockY, blockZ) % BlockTrap.trapTypes)));}
                player.worldObj.setBlockToAir(blockX, blockY, blockZ);
            } else {
                int trapType = player.worldObj.getBlockMetadata(blockX, blockY, blockZ);

                if(trapType % BlockTrap.trapTypes == 0) {
                    player.attackEntityFrom(DamageSource.cactus, 4.0F + (scout ? 1 : 0));
                    if(YouWillDie.r.nextInt(16 - (scout ? 4 : 0)) == 0) {
                        player.addPotionEffect(new PotionEffect(Potion.poison.id, 100 + (scout ? 25 : 0), 1));}
                } else if(trapType % BlockTrap.trapTypes == 1) {
                    if(!player.isBurning()) {player.setFire(5 + (scout ? 1 : 0));}
                } else if(trapType % BlockTrap.trapTypes == 2) {
                    player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100 + (scout ? 25 : 0), 1));
                    player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100 + (scout ? 25 : 0), 1));
                }

                player.worldObj.setBlockToAir(blockX, blockY, blockZ);

                SEND_MESSAGE.sendToPlayer(player, "\u00A7c\u00A7oYou failed to disarm the trap.");

                if(CommonTickHandler.worldData.getDisadvantage().equals("Explosive Traps")) {player.worldObj.setBlockToAir(blockX, blockY, blockZ); player.worldObj.createExplosion(null, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, 1.33F, true);}
                player.triggerAchievement(ModRegistry.whoops);
            }
        }

        @Override
        public Class[] getExpectedClasses() {return new Class[] {Integer.class, Integer.class, Integer.class, Boolean.class};}
    },

    EXAMINE_MOB = new BasicPacket(8) {
        @Override
        public void executeServer(EntityPlayer player) {
            int dimension = (Integer) data[0];
            int entityID = (Integer) data[1];

            WorldServer server = null;

            for(WorldServer s : MinecraftServer.getServer().worldServers) {if(s.provider.dimensionId == dimension) {server = s; break;}}

            if(server != null) {
                Entity entity = server.getEntityByID(entityID);

                if(entity != null) {
                    String blessing2 = entity.getEntityData().hasKey("Blessing") ? entity.getEntityData().getString("Blessing") : null;

                    if(blessing2 != null) {
                        SEND_MESSAGE.sendToPlayer(player, "\u00A7eYou notice " + entity.getCommandSenderName() + "\u00A7e is using Blessing of the " + blessing2 + ".");
                    } else {
                        SEND_MESSAGE.sendToPlayer(player, "\u00A7eThere doesn't seem to be anything special about " + (entity instanceof EntityPlayer ? "" : "this ") + entity.getCommandSenderName() + "\u00A7e.");
                    }
                }
            }
        }

        @Override
        public Class[] getExpectedClasses() {return new Class[] {Integer.class, Integer.class};}
    },

    LEVEL_UP = new BasicPacket(9) {
        @Override
        public void executeBoth(EntityPlayer player) {player.addExperienceLevel((Integer) data[0]);}
        @Override
        public Class[] getExpectedClasses() {return new Class[] {Integer.class};}
    },

    ACTIVATE_BLESSING = new BasicPacket(10) {
        @Override
        public void executeServer(EntityPlayer player) {
            int x = (Integer) data[0], y = (Integer) data[1], z = (Integer) data[2];

            String blessing = EntityStatHelper.getStat(player, "Blessing");
            boolean blessingActive = EntityStatHelper.hasStat(player, "BlessingActive") && Boolean.parseBoolean(EntityStatHelper.getStat(player, "BlessingActive"));

            if(!EntityStatHelper.hasStat(player, "BlessingCooldown")) {EntityStatHelper.giveStat(player, "BlessingCooldown", 0);}

            long time = (CommonTickHandler.worldData != null && CommonTickHandler.worldData.getDisadvantage().equals("Neverending Rain")) ? player.worldObj.getTotalWorldTime() : player.worldObj.getWorldTime();

            if(EntityStatHelper.getStat(player, "BlessingCooldown").equals("0")) {
                if(!blessingActive) {
                    if(blessing != null) {
                        if(blessing.equals("Berserker")) {
                            if(EntityStatHelper.hasStat(player, "BlessingCounter") && Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCounter")) > 0) {
                                blessingActive =  true;
                                SEND_MESSAGE.sendToPlayer(player, "\u00A7cYou enter berserk mode.");
                            } else {
                                SEND_MESSAGE.sendToPlayer(player, "\u00A7cYou have no berserk counters.");
                            }
                        } else if(blessing.equals("Mechanic")) {
                            TileEntity te = player.worldObj.getTileEntity(x, y, z);

                            if(te != null && te instanceof TileEntityTrap) {
                                SEND_MESSAGE.sendToPlayer(player, "\u00A7e\u00A7oYou disarm and salvage the trap.");

                                player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, x + 0.5F, y, z + 0.5F, new ItemStack(ModRegistry.itemTrap, 1, player.worldObj.getBlockMetadata(x, y, z) % BlockTrap.trapTypes)));
                                player.worldObj.setBlockToAir(x, y, z);

                                EntityStatHelper.giveStat(player, "BlessingCooldown", 24000 - (time % 24000));
                            } else {
                                SEND_MESSAGE.sendToPlayer(player, "\u00A7e\u00A7oNo selected trap.");
                            }
                        }
                    }
                } else {
                    blessingActive = false;

                    if(blessing != null) {
                        if(blessing.equals("Berserker")) {
                            SEND_MESSAGE.sendToPlayer(player, "\u00A7cYou calm down.");

                            EntityStatHelper.giveStat(player, "BlessingCooldown", 1200);
                        }
                    }

                    EntityStatHelper.giveStat(player, "BlessingTimer", 0);
                }
            } else {
                SEND_MESSAGE.sendToPlayer(player, "\u00A7cBlessing is on cooldown. (" + (Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCooldown")))/20 + "s)");
            }

            EntityStatHelper.giveStat(player, "BlessingActive", blessingActive);
        }

        @Override
        public Class[] getExpectedClasses() {return new Class[] {Integer.class, Integer.class, Integer.class};}
    },

    UPDATE_STAT = new BasicPacket(11) {
        @Override
        public void executeBoth(EntityPlayer player) {EntityStatHelper.giveStat(player, (String) data[0], data[1]);}
        @Override
        public Class[] getExpectedClasses() {return new Class[] {String.class, String.class};}
    };
}