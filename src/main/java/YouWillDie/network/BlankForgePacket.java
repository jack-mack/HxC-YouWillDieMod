package YouWillDie.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BlankForgePacket implements IMessage {
    public BlankForgePacket() {}

    @Override
    public void toBytes(ByteBuf byteBuf) {
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
    }

    public static class handler implements IMessageHandler<BlankForgePacket, IMessage> {
        @Override
        public IMessage onMessage(BlankForgePacket message, MessageContext ctx) {
            return null;
        }
    }
}