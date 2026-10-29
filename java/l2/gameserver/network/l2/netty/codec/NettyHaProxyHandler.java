package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Decodificador de cabeçalho HAProxy v2 (PROXY protocol).
 * Permite obter o IP real do jogador quando o servidor está atrás de um proxy TCP / balanceador.
 */
public final class NettyHaProxyHandler extends ByteToMessageDecoder {
    private static final Logger _log = LoggerFactory.getLogger(NettyHaProxyHandler.class);

    private static final byte[] PROXY_V2_SIG = new byte[] {
        (byte) 0x0D, (byte) 0x0A, (byte) 0x0D, (byte) 0x0A,
        (byte) 0x00, (byte) 0x0D, (byte) 0x0A, (byte) 0x51,
        (byte) 0x55, (byte) 0x49, (byte) 0x54, (byte) 0x0A
    };

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!in.isReadable()) {
            return;
        }

        // Fast-path: Se o primeiro byte não for 0x0D, não é PROXY protocol v2.
        if (in.getByte(in.readerIndex()) != (byte) 0x0D) {
            ctx.pipeline().remove(this);
            out.add(in.readRetainedSlice(in.readableBytes()));
            return;
        }

        // Precisa de pelo menos 16 bytes (12 sig + 1 ver/cmd + 1 fam/proto + 2 len)
        if (in.readableBytes() < 16) {
            return;
        }

        in.markReaderIndex();

        boolean match = true;
        for (byte b : PROXY_V2_SIG) {
            if (in.readByte() != b) {
                match = false;
                break;
            }
        }

        if (!match) {
            // Não é cabeçalho PROXY v2 — rewind, emite bytes para o próximo decoder e remove do pipeline
            in.resetReaderIndex();
            ctx.pipeline().remove(this);
            if (in.isReadable()) {
                out.add(in.readRetainedSlice(in.readableBytes()));
            }
            return;
        }

        int verCmd = in.readByte() & 0xFF;
        int famProto = in.readByte() & 0xFF;
        int addrLen = in.readUnsignedShort();

        if (in.readableBytes() < addrLen) {
            in.resetReaderIndex();
            return;
        }

        int family = (famProto >> 4) & 0x0F;
        NettyGameConnection conn = NettyGameConnection.of(ctx.channel());

        if (family == 1 && addrLen >= 12) { // AF_INET (IPv4)
            int ip1 = in.readByte() & 0xFF;
            int ip2 = in.readByte() & 0xFF;
            int ip3 = in.readByte() & 0xFF;
            int ip4 = in.readByte() & 0xFF;
            String realIp = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
            if (conn != null) {
                conn.setOverrideIp(realIp);
            }
            in.skipBytes(addrLen - 4);
        } else {
            in.skipBytes(addrLen);
        }

        // Consumiu com sucesso o header do proxy — emite o restante e remove do pipeline
        ctx.pipeline().remove(this);
        if (in.isReadable()) {
            out.add(in.readRetainedSlice(in.readableBytes()));
        }
    }
}
