package l2.gameserver.network.l2.netty.disruptor.deterministic;

import org.agrona.DirectBuffer;

/**
 * Ponto de extensão para acoplamento da lógica de negócio e simulação mecânica a 120 Hz.
 * <p>
 * Executado estritamente mono-thread na CPU pinada do {@link BrCoreWorker}.
 * Não requer locks nem primitivas de concorrência para as entidades atribuídas a este worker.
 */
public interface BrTickSimulationHandler {
    /**
     * Invocado durante a fase de drenagem de entrada para cada mensagem do Inbound Ring Buffer.
     *
     * @param sessionId Identificador da sessão do cliente.
     * @param sequenceId Sequência de entrada garantida FIFO.
     * @param buffer Buffer off-heap contíguo contendo o payload bruto.
     * @param payloadOffset Offset inicial dos bytes do pacote no buffer.
     * @param payloadLength Comprimento em bytes do payload.
     */
    void onInboundPacket(int sessionId, int sequenceId, DirectBuffer buffer, int payloadOffset, int payloadLength);

    /**
     * Invocado na fase de simulação mecânica para avançar o estado do mundo/entidades neste tick.
     *
     * @param tickId Identificador sequencial do tick atual (120 Hz).
     * @param nanoTime Timestamp em nanossegundos do início do tick.
     * @param worker Outbound emitter para gravar deltas de estado e pacotes de resposta diretamente no anel de saída.
     */
    void onTick(long tickId, long nanoTime, BrCoreWorker worker);
}
