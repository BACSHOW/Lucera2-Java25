package l2.gameserver.network.l2.netty.disruptor;

import l2.gameserver.network.l2.netty.NettyGameConnection;

/**
 * Slot de evento mutável no RingBuffer do Disruptor.
 *
 * Zero-allocation:
 * Pré-alocado uma única vez na subida do servidor (2^16 = 65536 slots) e reutilizado indefinidamente.
 *
 * False-sharing mitigation:
 * Campos quentes intercalados por 56 bytes de padding antes e depois (p1..p7 / q1..q7),
 * isolando o estado mutável em sua própria linha de cache L1/L2 (64 bytes).
 */
public final class PacketEvent {
    // Leading cache-line padding (56 bytes)
    public long p1, p2, p3, p4, p5, p6, p7;

    public NettyGameConnection connection;
    public byte[] payload;
    public int length;

    // Trailing cache-line padding (56 bytes)
    public long q1, q2, q3, q4, q5, q6, q7;

    public void set(NettyGameConnection connection, byte[] payload, int length) {
        this.connection = connection;
        this.payload = payload;
        this.length = length;
    }

    public void clear() {
        this.connection = null;
        this.payload = null;
        this.length = 0;
    }

    public long sumPadding() {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + q1 + q2 + q3 + q4 + q5 + q6 + q7;
    }
}
