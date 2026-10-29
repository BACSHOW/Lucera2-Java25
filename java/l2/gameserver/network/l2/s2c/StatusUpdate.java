/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class StatusUpdate
extends L2GameServerPacket {
    public static final int CUR_HP = 9;
    public static final int MAX_HP = 10;
    public static final int CUR_MP = 11;
    public static final int MAX_MP = 12;
    public static final int CUR_LOAD = 14;
    public static final int MAX_LOAD = 15;
    public static final int PVP_FLAG = 26;
    public static final int KARMA = 27;
    public static final int CUR_CP = 33;
    public static final int MAX_CP = 34;
    private final int CS;
    private final List<Attribute> amV = new ArrayList<Attribute>();

    public StatusUpdate(Creature creature) {
        this.CS = creature.getObjectId();
    }

    public StatusUpdate(int n) {
        this.CS = n;
    }

    public StatusUpdate addAttribute(int n, int n2) {
        this.amV.add(new Attribute(n, n2));
        return this;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(14);
        this.writeD(this.CS);
        this.writeD(this.amV.size());
        for (Attribute attribute : this.amV) {
            this.writeD(attribute.id);
            this.writeD(attribute.value);
        }
    }

    public boolean hasAttributes() {
        return !this.amV.isEmpty();
    }

    class Attribute {
        public final int id;
        public final int value;

        Attribute(int n, int n2) {
            this.id = n;
            this.value = n2;
        }
    }
}

