package l2.gameserver.network.l2.netty.disruptor.deterministic;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

/**
 * Layout de mensagem off-heap com alinhamento mecânico estrito (Zero-GC).
 * <p>
 * Formato Binário do Frame (Cabeçalho de 12 bytes fixos):
 * <pre>
 * +------------------+------------------+-----------------------+---------------------+
 * |  sessionId (4B)  |  sequenceId (4B) |  payloadLength (4B)   |   payload (N bytes) |
 * |      offset 0    |      offset 4    |       offset 8        |      offset 12      |
 * +------------------+------------------+-----------------------+---------------------+
 * </pre>
 * <p>
 * Mechanical Sympathy:
 * - Todas as leituras e escritas são alinhadas em limites de palavra de 32/64 bits.
 * - Zero alocação de objetos no heap: trabalha diretamente com {@link DirectBuffer} e {@link MutableDirectBuffer}.
 */
public final class BrFrameHeader {
    public static final int MSG_TYPE_PACKET = 1;

    public static final int SESSION_ID_OFFSET = 0;
    public static final int SEQUENCE_ID_OFFSET = 4;
    public static final int PAYLOAD_LENGTH_OFFSET = 8;
    public static final int HEADER_LENGTH = 12;
    public static final int PAYLOAD_OFFSET = 12;

    private BrFrameHeader() {
        // Utilitário estático de constantes e offsets
    }

    /**
     * Calcula o comprimento total necessário no buffer para codificar o frame completo.
     */
    public static int frameLength(int payloadLength) {
        return HEADER_LENGTH + payloadLength;
    }

    /**
     * Escreve o cabeçalho binário diretamente no buffer off-heap sem alocação.
     */
    public static void encodeHeader(MutableDirectBuffer buffer, int offset, int sessionId, int sequenceId, int payloadLength) {
        buffer.putInt(offset + SESSION_ID_OFFSET, sessionId);
        buffer.putInt(offset + SEQUENCE_ID_OFFSET, sequenceId);
        buffer.putInt(offset + PAYLOAD_LENGTH_OFFSET, payloadLength);
    }

    /**
     * Lê o sessionId diretamente do buffer sem instanciar objetos.
     */
    public static int readSessionId(DirectBuffer buffer, int offset) {
        return buffer.getInt(offset + SESSION_ID_OFFSET);
    }

    /**
     * Lê o sequenceId diretamente do buffer sem instanciar objetos.
     */
    public static int readSequenceId(DirectBuffer buffer, int offset) {
        return buffer.getInt(offset + SEQUENCE_ID_OFFSET);
    }

    /**
     * Lê o payloadLength diretamente do buffer sem instanciar objetos.
     */
    public static int readPayloadLength(DirectBuffer buffer, int offset) {
        return buffer.getInt(offset + PAYLOAD_LENGTH_OFFSET);
    }
}
