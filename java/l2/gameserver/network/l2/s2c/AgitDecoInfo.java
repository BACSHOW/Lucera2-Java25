/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgitDecoInfo
extends L2GameServerPacket {
    private static final Logger _log = LoggerFactory.getLogger(AgitDecoInfo.class);
    private static int[] adz = new int[]{0, 1, 1, 1, 2, 2, 2, 2, 2, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2};
    private static int[] adA = new int[]{0, 1, 2, 2};
    private int _id;
    private int adB;
    private int adC;
    private int adD;
    private int adE;
    private int adF;
    private int adG;
    private int adH;
    private int adI;

    public AgitDecoInfo(ClanHall clanHall) {
        this._id = clanHall.getId();
        this.adB = AgitDecoInfo.R(clanHall.isFunctionActive(3) ? clanHall.getFunction(3).getLevel() : 0);
        this.adC = AgitDecoInfo.S(clanHall.isFunctionActive(4) ? clanHall.getFunction(4).getLevel() : 0);
        this.adD = AgitDecoInfo.T(clanHall.isFunctionActive(5) ? clanHall.getFunction(5).getLevel() : 0);
        this.adE = clanHall.isFunctionActive(1) ? clanHall.getFunction(1).getLevel() : 0;
        this.adF = clanHall.isFunctionActive(7) ? clanHall.getFunction(7).getLevel() : 0;
        this.adG = clanHall.isFunctionActive(2) ? adA[clanHall.getFunction(2).getLevel()] : 0;
        this.adH = clanHall.isFunctionActive(6) ? adz[clanHall.getFunction(6).getLevel()] : 0;
        this.adI = clanHall.isFunctionActive(8) ? clanHall.getFunction(8).getLevel() : 0;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(247);
        this.writeD(this._id);
        this.writeC(this.adB);
        this.writeC(this.adC);
        this.writeC(this.adC);
        this.writeC(this.adD);
        this.writeC(this.adE);
        this.writeC(0);
        this.writeC(this.adF);
        this.writeC(this.adG);
        this.writeC(this.adH);
        this.writeC(this.adH);
        this.writeC(this.adI);
        this.writeC(this.adG);
        this.writeD(0);
        this.writeD(0);
    }

    private static int R(int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 20: 
            case 40: 
            case 80: 
            case 120: 
            case 140: {
                return 1;
            }
            case 160: 
            case 180: 
            case 200: 
            case 220: 
            case 240: 
            case 260: 
            case 280: 
            case 300: {
                return 2;
            }
        }
        _log.warn("Unsupported percent " + n + " in hp recovery");
        return 0;
    }

    private static int S(int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 5: 
            case 10: 
            case 15: 
            case 20: {
                return 1;
            }
            case 25: 
            case 30: 
            case 35: 
            case 40: 
            case 45: 
            case 50: {
                return 2;
            }
        }
        _log.warn("Unsupported percent " + n + " in mp recovery");
        return 0;
    }

    private static int T(int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 5: 
            case 10: 
            case 15: 
            case 20: {
                return 1;
            }
            case 25: 
            case 30: 
            case 35: 
            case 40: 
            case 45: 
            case 50: {
                return 2;
            }
        }
        _log.warn("Unsupported percent " + n + " in exp recovery");
        return 0;
    }
}

