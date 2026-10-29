package l2.gameserver.network.l2.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.l2.CGModule;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GamePacketHandler;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.netty.disruptor.DisruptorPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Despachante de pacotes recebidos pelo Netty para o GameServer do L2JLucera2.
 * Suporta processamento direto ou desacoplado via anel circular LMAX Disruptor.
 */
public final class NettyGameDispatcher extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger _log = LoggerFactory.getLogger(NettyGameDispatcher.class);

    private final GamePacketHandler packetHandler;
    private final DisruptorPipeline disruptorPipeline;

    public NettyGameDispatcher(GamePacketHandler packetHandler) {
        this(packetHandler, null);
    }

    public NettyGameDispatcher(GamePacketHandler packetHandler, DisruptorPipeline disruptorPipeline) {
        this.packetHandler = packetHandler;
        this.disruptorPipeline = disruptorPipeline;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyGameConnection conn = new NettyGameConnection(ctx.channel());
        GameClient client = new GameClient(conn);
        conn.attachClient(client);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        NettyGameConnection conn = NettyGameConnection.of(ctx.channel());
        if (conn == null) {
            return;
        }

        int readable = msg.readableBytes();
        if (readable == 0) {
            return;
        }

        byte[] data = new byte[readable];
        msg.readBytes(data);

        if (this.disruptorPipeline != null) {
            // Publica no anel circular lock-free e retorna imediatamente a thread de I/O
            this.disruptorPipeline.publish(conn, data, readable);
        } else {
            // Fallback direto
            this.dispatchPacket(conn, data);
        }
    }

    /**
     * Despacha o pacote (chamado pelo consumidor do Disruptor ou diretamente pelo handler).
     */
    public void dispatchPacket(NettyGameConnection conn, byte[] payload) {
        GameClient client = conn != null ? conn.getClient() : null;
        if (client == null || payload == null || payload.length == 0) {
            return;
        }

        ByteBuffer buf = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN);

        // Interceptação pré-packet do CGModule se ativo
        if (CGModule.getInstance().isActive() && !CGModule.getInstance().checkPacket(client, buf)) {
            return;
        }

        try {
            int opcode = payload[0] & 0xFF;
            L2GameClientPacket packet = (L2GameClientPacket) this.packetHandler.handlePacket(buf, client);
            if (packet != null) {
                if (packet.readFrom(buf, client)) {
                    conn.recvPacket(packet);
                } else {
                    _log.warn("[Netty] Packet readFrom failed: " + packet.getClass().getSimpleName() + " (opcode 0x" + Integer.toHexString(opcode) + ") from " + client);
                    client.onPacketReadFail();
                }
            } else {
                _log.warn("[Netty] Unknown packet opcode 0x" + Integer.toHexString(opcode) + " (len=" + payload.length + ") from " + client);
            }
        } catch (Exception e) {
            _log.error("Error reading packet from Netty client: " + client, e);
            client.onPacketReadFail();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyGameConnection conn = NettyGameConnection.of(ctx.channel());
        if (conn != null) {
            GameClient client = conn.getClient();
            if (client != null) {
                _log.warn("[Netty] Client channelInactive: " + client + " | Player=" + (client.getActiveChar() != null ? client.getActiveChar().getName() : "null"));
                try {
                    client.closeNow(false);
                } catch (Exception e) {
                    _log.error("Error during Netty client disconnect: " + client, e);
                }
            }
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        NettyGameConnection conn = NettyGameConnection.of(ctx.channel());
        GameClient client = conn != null ? conn.getClient() : null;
        _log.error("Netty channel exception for client " + client + ": " + cause.getMessage(), cause);
        ctx.close();
    }
}
