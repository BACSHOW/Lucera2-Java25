/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class PlaySound
extends L2GameServerPacket {
    public static final L2GameServerPacket SIEGE_VICTORY = new PlaySound("Siege_Victory");
    public static final L2GameServerPacket B04_S01 = new PlaySound("B04_S01");
    public static final L2GameServerPacket HB01 = new PlaySound(Type.MUSIC, "HB01", 0, 0, 0, 0, 0);
    private Type Qy;
    private String akV;
    private int akW;
    private int CS;
    private int _x;
    private int _y;
    private int Di;
    private int _delay;

    public PlaySound(String string) {
        this(Type.SOUND, string, 0, 0, 0, 0, 0, 0);
    }

    public PlaySound(Type type, String string, int n, int n2, Location location) {
        this(type, string, n, n2, location == null ? 0 : location.x, location == null ? 0 : location.y, location == null ? 0 : location.z, 0);
    }

    public PlaySound(Type type, String string, int n, int n2, int n3, int n4, int n5) {
        this(type, string, n, n2, n3, n4, n5, 0);
    }

    public PlaySound(Type type, String string, int n, int n2, int n3, int n4, int n5, int delay) {
        this.Qy = type;
        this.akV = string;
        this.akW = n;
        this.CS = n2;
        this._x = n3;
        this._y = n4;
        this.Di = n5;
        this._delay = delay;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(152);
        this.writeD(this.Qy.ordinal());
        this.writeS(this.akV);
        this.writeD(this.akW);
        this.writeD(this.CS);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
        this.writeD(this._delay);
    }

    public static enum Type {
        SOUND,
        MUSIC,
        VOICE;
    }
}
