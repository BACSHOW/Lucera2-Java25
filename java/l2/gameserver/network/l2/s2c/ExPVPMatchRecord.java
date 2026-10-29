/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.LinkedList;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPVPMatchRecord
extends L2GameServerPacket {
    private PVPMatchAction ahk;
    private LinkedList<PVPMatchRecord> ahl;
    private LinkedList<PVPMatchRecord> ahm;
    private int ahn;
    private int aho;
    private TeamType _winner;
    private int ahp;
    private int ahq;

    public ExPVPMatchRecord(PVPMatchAction pVPMatchAction, TeamType teamType, int n, int n2) {
        this.ahk = pVPMatchAction;
        this.ahl = new LinkedList();
        this.ahm = new LinkedList();
        this._winner = teamType;
        this.ahp = n;
        this.ahq = n2;
    }

    public void addRecord(Player player, int n, int n2) {
        if (player.getTeam() == TeamType.RED) {
            this.ahl.add(new PVPMatchRecord(player.getName(), n, n2));
            ++this.aho;
        } else if (player.getTeam() == TeamType.BLUE) {
            this.ahm.add(new PVPMatchRecord(player.getName(), n, n2));
            ++this.ahn;
        }
    }

    @Override
    protected void writeImpl() {
        this.writeEx(126);
        this.writeD(this.ahk.getVal());
        if (this._winner == TeamType.RED) {
            this.writeD(2);
            this.writeD(1);
        } else if (this._winner == TeamType.BLUE) {
            this.writeD(1);
            this.writeD(2);
        } else {
            this.writeD(0);
            this.writeD(0);
        }
        this.writeD(this.ahp);
        this.writeD(this.ahq);
        if (this.ahn > 0) {
            this.writeD(this.ahm.size());
            for (PVPMatchRecord pVPMatchRecord : this.ahm) {
                this.writeS(pVPMatchRecord.name);
                this.writeD(pVPMatchRecord.kill);
                this.writeD(pVPMatchRecord.die);
            }
        } else {
            this.writeD(0);
        }
        if (this.aho > 0) {
            this.writeD(this.ahl.size());
            for (PVPMatchRecord pVPMatchRecord : this.ahl) {
                this.writeS(pVPMatchRecord.name);
                this.writeD(pVPMatchRecord.kill);
                this.writeD(pVPMatchRecord.die);
            }
        } else {
            this.writeD(0);
        }
    }

    private class PVPMatchRecord {
        public final String name;
        public final int kill;
        public final int die;

        public PVPMatchRecord(String string, int n, int n2) {
            this.name = string;
            this.kill = n;
            this.die = n2;
        }
    }

    public static enum PVPMatchAction {
        INIT(0),
        UPDATE(1),
        DONE(2);

        private final int afZ;

        private PVPMatchAction(int n2) {
            this.afZ = n2;
        }

        public int getVal() {
            return this.afZ;
        }
    }
}

