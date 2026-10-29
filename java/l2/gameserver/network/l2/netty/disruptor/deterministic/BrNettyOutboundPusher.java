package l2.gameserver.network.l2.netty.disruptor.deterministic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Despachante Outbound em Lote do Netty (Batch Gathering Writes via writev).
 * <p>
 * Mechanical Sympathy:
 * 1. <b>Batch Draining</b>: Esvazia os Ring Buffers de saída de todos os workers em blocos contíguos.
 * 2. <b>Agrupamento de Syscalls</b>: Executa múltiplos {@code channel.write()} e consolida em um único
 *    {@code channel.flush()}, instruindo o kernel a usar chamadas de sistema agregadas (ex: {@code writev}).
 */
public final class BrNettyOutboundPusher implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(BrNettyOutboundPusher.class);
    public static final int BATCH_DRAIN_LIMIT = 512;

    private final BrCoreWorker[] workers;
    private final Map<Integer, Channel> activeSessions = new ConcurrentHashMap<>();
    private final Set<Channel> touchedChannels = new HashSet<>();

    public BrNettyOutboundPusher(BrCoreWorker[] workers) {
        this.workers = workers;
    }

    public void registerSession(int sessionId, Channel channel) {
        this.activeSessions.put(sessionId, channel);
    }

    public void unregisterSession(int sessionId) {
        this.activeSessions.remove(sessionId);
    }

    @Override
    public void run() {
        this.touchedChannels.clear();

        for (BrCoreWorker worker : this.workers) {
            ManyToOneRingBuffer outboundRing = worker.getOutboundRingBuffer();
            if (outboundRing == null) {
                continue;
            }

            MessageHandler drainHandler = (msgTypeId, buffer, index, length) -> {
                if (msgTypeId == BrFrameHeader.MSG_TYPE_PACKET) {
                    int sessionId = BrFrameHeader.readSessionId(buffer, index);
                    int payloadLength = BrFrameHeader.readPayloadLength(buffer, index);
                    int payloadOffset = index + BrFrameHeader.PAYLOAD_OFFSET;

                    Channel channel = this.activeSessions.get(sessionId);
                    if (channel != null && channel.isActive()) {
                        ByteBuf nettyOutBuf = channel.alloc().directBuffer(payloadLength);
                        byte[] temp = new byte[payloadLength];
                        buffer.getBytes(payloadOffset, temp);
                        nettyOutBuf.writeBytes(temp);

                        channel.write(nettyOutBuf);
                        this.touchedChannels.add(channel);
                    }
                }
            };

            // Drena até BATCH_DRAIN_LIMIT pacotes por worker por passada de I/O
            outboundRing.read(drainHandler, BATCH_DRAIN_LIMIT);
        }

        // Flush agrupado em todos os canais que receberam pacotes neste ciclo
        if (!this.touchedChannels.isEmpty()) {
            for (Channel channel : this.touchedChannels) {
                channel.flush();
            }
        }
    }
}
