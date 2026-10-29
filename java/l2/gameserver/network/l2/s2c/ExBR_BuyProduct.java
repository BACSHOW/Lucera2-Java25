/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExBR_BuyProduct
extends L2GameServerPacket {
    public static final int RESULT_OK = 1;
    public static final int RESULT_NOT_ENOUGH_POINTS = -1;
    public static final int RESULT_WRONG_PRODUCT = -2;
    public static final int RESULT_INVENTORY_FULL = -4;
    public static final int RESULT_SALE_PERIOD_ENDED = -7;
    public static final int RESULT_WRONG_USER_STATE = -9;
    public static final int RESULT_WRONG_PRODUCT_ITEM = -10;
    private final int oJ;

    public ExBR_BuyProduct(int n) {
        this.oJ = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(216);
        this.writeD(this.oJ);
    }
}

