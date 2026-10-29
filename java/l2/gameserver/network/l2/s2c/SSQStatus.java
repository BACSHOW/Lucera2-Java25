/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.StatsSet;

public class SSQStatus
extends L2GameServerPacket {
    private Player _player;
    private int _page;
    private int alZ;

    public SSQStatus(Player player, int n) {
        this._player = player;
        this._page = n;
        this.alZ = SevenSigns.getInstance().getCurrentPeriod();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(245);
        this.writeC(this._page);
        this.writeC(this.alZ);
        switch (this._page) {
            case 1: {
                this.writeD(SevenSigns.getInstance().getCurrentCycle());
                switch (this.alZ) {
                    case 0: {
                        this.writeD(1183);
                        break;
                    }
                    case 1: {
                        this.writeD(1176);
                        break;
                    }
                    case 2: {
                        this.writeD(1184);
                        break;
                    }
                    case 3: {
                        this.writeD(1177);
                    }
                }
                switch (this.alZ) {
                    case 0: 
                    case 2: {
                        this.writeD(1287);
                        break;
                    }
                    case 1: 
                    case 3: {
                        this.writeD(1286);
                    }
                }
                this.writeC(SevenSigns.getInstance().getPlayerCabal(this._player));
                this.writeC(SevenSigns.getInstance().getPlayerSeal(this._player));
                this.writeD((int)SevenSigns.getInstance().getPlayerStoneContrib(this._player));
                this.writeD((int)SevenSigns.getInstance().getPlayerAdenaCollect(this._player));
                long l = SevenSigns.getInstance().getCurrentStoneScore(2);
                long l2 = SevenSigns.getInstance().getCurrentFestivalScore(2);
                long l3 = SevenSigns.getInstance().getCurrentScore(2);
                long l4 = SevenSigns.getInstance().getCurrentStoneScore(1);
                long l5 = SevenSigns.getInstance().getCurrentFestivalScore(1);
                long l6 = SevenSigns.getInstance().getCurrentScore(1);
                long l7 = l4 + l;
                l7 = l7 == 0L ? 1L : l7;
                long l8 = Math.round((double)l4 * 500.0 / (double)l7);
                long l9 = Math.round((double)l * 500.0 / (double)l7);
                long l10 = l6 + l3;
                l10 = l10 == 0L ? 1L : l10;
                long l11 = Math.round((double)l3 * 110.0 / (double)l10);
                long l12 = Math.round((double)l6 * 110.0 / (double)l10);
                this.writeD((int)l8);
                this.writeD((int)l5);
                this.writeD((int)l6);
                this.writeC((int)l12);
                this.writeD((int)l9);
                this.writeD((int)l2);
                this.writeD((int)l3);
                this.writeC((int)l11);
                break;
            }
            case 2: {
                this.writeH(1);
                this.writeC(5);
                for (int i = 0; i < 5; ++i) {
                    String[] stringArray;
                    this.writeC(i + 1);
                    this.writeD(SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i]);
                    long l = SevenSignsFestival.getInstance().getHighestScore(1, i);
                    long l13 = SevenSignsFestival.getInstance().getHighestScore(2, i);
                    this.writeQ(l);
                    StatsSet statsSet = SevenSignsFestival.getInstance().getHighestScoreData(1, i);
                    if (l > 0L) {
                        stringArray = statsSet.getString("names").split(",");
                        this.writeC(stringArray.length);
                        for (String string : stringArray) {
                            this.writeS(string);
                        }
                    } else {
                        this.writeC(0);
                    }
                    this.writeQ(l13);
                    statsSet = SevenSignsFestival.getInstance().getHighestScoreData(2, i);
                    if (l13 > 0L) {
                        stringArray = statsSet.getString("names").split(",");
                        this.writeC(stringArray.length);
                        for (String string : stringArray) {
                            this.writeS(string);
                        }
                        continue;
                    }
                    this.writeC(0);
                }
                break;
            }
            case 3: {
                int n;
                this.writeC(10);
                this.writeC(35);
                this.writeC(3);
                int n2 = 1;
                int n3 = 1;
                for (n = 1; n <= 3; ++n) {
                    n2 += SevenSigns.getInstance().getSealProportion(n, 2);
                    n3 += SevenSigns.getInstance().getSealProportion(n, 1);
                }
                n2 = Math.max(1, n2);
                n3 = Math.max(1, n3);
                for (n = 1; n <= 3; ++n) {
                    int n4 = SevenSigns.getInstance().getSealProportion(n, 2);
                    int n5 = SevenSigns.getInstance().getSealProportion(n, 1);
                    this.writeC(n);
                    this.writeC(SevenSigns.getInstance().getSealOwner(n));
                    this.writeC(n5 * 100 / n3);
                    this.writeC(n4 * 100 / n2);
                }
                break;
            }
            case 4: {
                int n = SevenSigns.getInstance().getCabalHighestScore();
                this.writeC(n);
                this.writeC(3);
                int n6 = SevenSigns.getInstance().getTotalMembers(2);
                int n7 = SevenSigns.getInstance().getTotalMembers(1);
                for (int i = 1; i < 4; ++i) {
                    this.writeC(i);
                    int n8 = SevenSigns.getInstance().getSealProportion(i, 2);
                    int n9 = SevenSigns.getInstance().getSealProportion(i, 1);
                    int n10 = n6 > 0 ? n8 * 100 / n6 : 0;
                    int n11 = n7 > 0 ? n9 * 100 / n7 : 0;
                    int n12 = SevenSigns.getInstance().getSealOwner(i);
                    if (Math.max(n10, n11) < 10) {
                        this.writeC(0);
                        if (n12 == 0) {
                            this.writeD(1292);
                            continue;
                        }
                        this.writeD(1291);
                        continue;
                    }
                    if (Math.max(n10, n11) < 35) {
                        this.writeC(n12);
                        if (n12 == 0) {
                            this.writeD(1292);
                            continue;
                        }
                        this.writeD(1289);
                        continue;
                    }
                    if (n10 == n11) {
                        this.writeC(0);
                        this.writeD(1293);
                        continue;
                    }
                    int n13 = n10 > n11 ? 2 : 1;
                    this.writeC(n13);
                    if (n13 == n12) {
                        this.writeD(1289);
                        continue;
                    }
                    this.writeD(1290);
                }
                break;
            }
        }
        this._player = null;
    }
}

