/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExSetCompassZoneCode
extends L2GameServerPacket {
    public static final int ZONE_ALTERED = 8;
    public static final int ZONE_ALTERED2 = 9;
    public static final int ZONE_REMINDER = 10;
    public static final int ZONE_SIEGE = 11;
    public static final int ZONE_PEACE = 12;
    public static final int ZONE_SSQ = 13;
    public static final int ZONE_PVP = 14;
    public static final int ZONE_GENERAL_FIELD = 15;
    public static final int ZONE_PVP_FLAG = 16384;
    public static final int ZONE_ALTERED_FLAG = 256;
    public static final int ZONE_SIEGE_FLAG = 2048;
    public static final int ZONE_PEACE_FLAG = 4096;
    public static final int ZONE_SSQ_FLAG = 8192;
    private final int ahZ;

    public ExSetCompassZoneCode(Player player) {
        this(player.getZoneMask());
    }

    public ExSetCompassZoneCode(int n) {
        this.ahZ = (n & 0x100) == 256 ? 8 : ((n & 0x800) == 2048 ? 11 : ((n & 0x4000) == 16384 ? 14 : ((n & 0x1000) == 4096 ? 12 : ((n & 0x2000) == 8192 ? 13 : 15))));
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(50);
        this.writeD(this.ahZ);
    }
}

