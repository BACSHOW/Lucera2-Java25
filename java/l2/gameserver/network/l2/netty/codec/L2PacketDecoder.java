package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.netty.NettyGameConnection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Decifra os pacotes recebidos do cliente L2 usando o GameCrypt ativo.
 */
public final class L2PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        int size = msg.readableBytes();
        if (size == 0) {
            return;
        }

        byte[] data = new byte[size];
        msg.readBytes(data);

        NettyGameConnection conn = NettyGameConnection.of(ctx.channel());
        GameClient client = conn != null ? conn.getClient() : null;

        if (client != null) {
            ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            client.decrypt(buf, size);
        }

        out.add(ctx.alloc().buffer(size).writeBytes(data));
    }
}
