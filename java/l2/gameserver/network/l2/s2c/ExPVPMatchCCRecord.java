/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.LinkedList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ExPVPMatchCCRecord
extends L2GameServerPacket {
    private List<Pair<String, Integer>> ahg;
    private int ahh;
    private PVPMatchCCAction ahi;

    public ExPVPMatchCCRecord(PVPMatchCCAction pVPMatchCCAction) {
        this.ahi = pVPMatchCCAction;
        this.ahh = 0;
        this.ahg = new LinkedList<Pair<String, Integer>>();
    }

    public void addPlayer(Player player, int n) {
        ++this.ahh;
        this.ahg.add(new ImmutablePair<String, Integer>(player.getName(), n));
    }

    @Override
    public void writeImpl() {
        this.writeEx(137);
        this.writeD(this.ahi.getVal());
        this.writeD(this.ahh);
        for (Pair<String, Integer> pair : this.ahg) {
            this.writeS(pair.getLeft());
            this.writeD(pair.getRight());
        }
    }

    public static enum PVPMatchCCAction {
        INIT(0),
        UPDATE(1),
        DONE(2);

        private final int afZ;

        private PVPMatchCCAction(int n2) {
            this.afZ = n2;
        }

        public int getVal() {
            return this.afZ;
        }
    }
}

