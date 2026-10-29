/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.entity.oly.Stadium;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExReceiveOlympiadGameList
extends L2GameServerPacket {
    private ArrayList<GameRec> ahE = new ArrayList();

    public void add(Stadium stadium, CompetitionType competitionType, int n, String string, String string2) {
        this.ahE.add(new GameRec(stadium, competitionType, n, string, string2));
    }

    @Override
    protected void writeImpl() {
        this.writeEx(212);
        this.writeD(0);
        this.writeD(this.ahE.size());
        this.writeD(0);
        for (GameRec gameRec : this.ahE) {
            this.writeD(gameRec.stadium_id);
            this.writeD(gameRec.type);
            this.writeD(gameRec.state);
            this.writeS(gameRec.player0name);
            this.writeS(gameRec.player1name);
        }
    }

    private class GameRec {
        int stadium_id;
        int type;
        int state;
        String player0name;
        String player1name;

        public GameRec(Stadium stadium, CompetitionType competitionType, int n, String string, String string2) {
            this.stadium_id = stadium.getStadiumId();
            this.type = competitionType.getTypeIdx();
            this.state = n;
            this.player0name = string;
            this.player1name = string2;
        }
    }
}

