/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class EnchantResult
extends L2GameServerPacket {
    private final int afF;
    private final int afG;
    private final long CE;
    public static final EnchantResult SUCESS = new EnchantResult(0, 0, 0L);
    public static final EnchantResult CANCEL = new EnchantResult(2, 0, 0L);
    public static final EnchantResult BLESSED_FAILED = new EnchantResult(3, 0, 0L);
    public static final EnchantResult FAILED_NO_CRYSTALS = new EnchantResult(4, 0, 0L);
    public static final EnchantResult ANCIENT_FAILED = new EnchantResult(5, 0, 0L);

    public EnchantResult(int n, int n2, long l) {
        this.afF = n;
        this.afG = n2;
        this.CE = l;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(129);
        this.writeD(this.afF);
        this.writeD(this.afG);
        this.writeQ(this.CE);
    }
}

