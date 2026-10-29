package l2.gameserver.network.l2.netty.disruptor;

import com.lmax.disruptor.EventHandler;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Consumidor ordenado de pacotes de entrada do RingBuffer do Disruptor.
 *
 * Dhousefe Core Guarantee:
 * 1. Cópia defensiva na entrada: se o payload referenciar buffer compartilhado,
 *    entrega uma cópia exata isolada para a lógica do jogo.
 * 2. Limpeza atômica do slot: event.clear() no finally zera o slot antes de o produtor reutilizá-lo.
 * 3. Drenagem e coalescing de flush no endOfBatch para reduzir chamadas de sistema no socket.
 */
public final class InboundPacketHandler implements EventHandler<PacketEvent> {
    private static final Logger _log = LoggerFactory.getLogger(InboundPacketHandler.class);

    private final NettyPacketConsumer consumer;
    private final Set<NettyGameConnection> pendingFlush = new LinkedHashSet<>();

    public InboundPacketHandler(NettyPacketConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(PacketEvent event, long sequence, boolean endOfBatch) {
        NettyGameConnection connection = event.connection;
        byte[] payload = event.payload;
        int length = event.length;

        try {
            if (connection != null && payload != null && length > 0) {
                // Cópia defensiva de fallback: isola o payload para o game layer sem risco de atropelamento
                byte[] exact = payload.length == length ? payload : Arrays.copyOf(payload, length);
                this.consumer.onPacket(connection, exact);
                this.pendingFlush.add(connection);
            }
        } catch (Throwable t) {
            _log.error("[Disruptor] Error processing packet at seq=" + sequence, t);
        } finally {
            // Sempre limpa o slot do RingBuffer para o próximo ciclo
            event.clear();
        }

        if (endOfBatch && !this.pendingFlush.isEmpty()) {
            for (NettyGameConnection conn : this.pendingFlush) {
                if (conn.isConnected()) {
                    conn.getChannel().flush();
                }
            }
            this.pendingFlush.clear();
        }
    }
}
