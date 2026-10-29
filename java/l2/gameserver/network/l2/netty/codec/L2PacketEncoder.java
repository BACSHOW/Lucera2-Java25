package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.netty.NettyGameConnection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Codifica os pacotes s2c (servidor -> cliente):
 * 1. Cifra o payload com a chave ativa do cliente (se ativada).
 * 2. Precede com 2 bytes little-endian com o total (tamanho + 2).
 */
public final class L2PacketEncoder extends MessageToByteEncoder<ByteBuf> {
    public static final int HEADER_SIZE = 2;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int size = msg.readableBytes();
        if (size <= 0) {
            return;
        }
        int totalLength = size + HEADER_SIZE;

        byte[] data = new byte[size];
        msg.readBytes(data);

        NettyGameConnection conn = NettyGameConnection.of(ctx.channel());
        GameClient client = conn != null ? conn.getClient() : null;

        if (client != null) {
            ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            client.encrypt(buf, size);
        }

        // Escreve header de 2 bytes Little-Endian
        out.writeByte(totalLength & 0xFF);
        out.writeByte((totalLength >> 8) & 0xFF);
        out.writeBytes(data);
    }
}
