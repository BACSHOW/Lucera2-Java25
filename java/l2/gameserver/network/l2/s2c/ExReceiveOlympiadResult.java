/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExReceiveOlympiadResult
extends L2GameServerPacket {
    private String ahF;
    private int ahG;
    private ArrayList<ExReceiveOlympiadResultRecord> ahH;
    private ArrayList<ExReceiveOlympiadResultRecord> ahI;

    public ExReceiveOlympiadResult(int n, String string) {
        this.ahF = string;
        this.ahG = n;
        this.ahH = new ArrayList();
        this.ahI = new ArrayList();
    }

    public void add(int n, Player player, int n2, int n3, int n4) {
        if (n == Participant.SIDE_RED) {
            this.ahH.add(new ExReceiveOlympiadResultRecord(player, n2, n3, n4));
        }
        if (n == Participant.SIDE_BLUE) {
            this.ahI.add(new ExReceiveOlympiadResultRecord(player, n2, n3, n4));
        }
    }

    @Override
    protected void writeImpl() {
        this.writeEx(212);
        this.writeD(1);
        if (this.ahG != 0) {
            this.writeD(0);
            this.writeS(this.ahF);
        } else {
            this.writeD(1);
            this.writeS("");
        }
        if (this.ahG == Participant.SIDE_RED) {
            this.writeD(1);
            this.writeD(this.ahH.size());
            for (ExReceiveOlympiadResultRecord exReceiveOlympiadResultRecord : this.ahH) {
                this.writeS(exReceiveOlympiadResultRecord.name);
                this.writeS(exReceiveOlympiadResultRecord.clan);
                this.writeD(exReceiveOlympiadResultRecord.crest_id);
                this.writeD(exReceiveOlympiadResultRecord.class_id);
                this.writeD(exReceiveOlympiadResultRecord.dmg);
                this.writeD(exReceiveOlympiadResultRecord.points);
                this.writeD(exReceiveOlympiadResultRecord.delta);
            }
            this.writeD(0);
            this.writeD(this.ahI.size());
            for (ExReceiveOlympiadResultRecord exReceiveOlympiadResultRecord : this.ahI) {
                this.writeS(exReceiveOlympiadResultRecord.name);
                this.writeS(exReceiveOlympiadResultRecord.clan);
                this.writeD(exReceiveOlympiadResultRecord.crest_id);
                this.writeD(exReceiveOlympiadResultRecord.class_id);
                this.writeD(exReceiveOlympiadResultRecord.dmg);
                this.writeD(exReceiveOlympiadResultRecord.points);
                this.writeD(exReceiveOlympiadResultRecord.delta);
            }
        } else {
            this.writeD(0);
            this.writeD(this.ahI.size());
            for (ExReceiveOlympiadResultRecord exReceiveOlympiadResultRecord : this.ahI) {
                this.writeS(exReceiveOlympiadResultRecord.name);
                this.writeS(exReceiveOlympiadResultRecord.clan);
                this.writeD(exReceiveOlympiadResultRecord.crest_id);
                this.writeD(exReceiveOlympiadResultRecord.class_id);
                this.writeD(exReceiveOlympiadResultRecord.dmg);
                this.writeD(exReceiveOlympiadResultRecord.points);
                this.writeD(exReceiveOlympiadResultRecord.delta);
            }
            this.writeD(1);
            this.writeD(this.ahH.size());
            for (ExReceiveOlympiadResultRecord exReceiveOlympiadResultRecord : this.ahH) {
                this.writeS(exReceiveOlympiadResultRecord.name);
                this.writeS(exReceiveOlympiadResultRecord.clan);
                this.writeD(exReceiveOlympiadResultRecord.crest_id);
                this.writeD(exReceiveOlympiadResultRecord.class_id);
                this.writeD(exReceiveOlympiadResultRecord.dmg);
                this.writeD(exReceiveOlympiadResultRecord.points);
                this.writeD(exReceiveOlympiadResultRecord.delta);
            }
        }
    }

    private class ExReceiveOlympiadResultRecord {
        String name;
        String clan;
        int class_id;
        int crest_id;
        int dmg;
        int points;
        int delta;

        public ExReceiveOlympiadResultRecord(Player player, int n, int n2, int n3) {
            this.name = player.getName();
            this.class_id = player.getClassId().getId();
            this.clan = player.getClan() != null ? player.getClan().getName() : "";
            this.crest_id = player.getClanId();
            this.dmg = n;
            this.points = n2;
            this.delta = n3;
        }
    }
}

