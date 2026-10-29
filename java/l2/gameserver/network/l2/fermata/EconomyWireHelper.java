package l2.gameserver.network.l2.fermata;

import java.nio.ByteBuffer;

public final class EconomyWireHelper {

    public static final int MONSTER_RACE_TICKET_ID = 4443;

    private EconomyWireHelper() {
    }

    public static void writeItemMetadata(ByteBuffer buf, int itemId, int race, int lane, long stake) {
        if (itemId == MONSTER_RACE_TICKET_ID) {
            buf.put((byte) 1); // Tag 1 obrigatorio para Monster Race Tickets
            buf.putShort((short) race);
            buf.putShort((short) lane);
            buf.putLong(stake);
        } else {
            buf.put((byte) 0); // Tag 0 para itens padrão
        }
    }

    public static long safeMultiply(long a, long b) {
        return Math.multiplyExact(a, b);
    }

    public static long safeAdd(long a, long b) {
        return Math.addExact(a, b);
    }
}
