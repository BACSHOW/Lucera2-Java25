/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class GMViewCharacterInfo
extends L2GameServerPacket {
    private Location cH;
    private int[][] aee;
    private int afs;
    private int CV;
    private int Dd;
    private int class_id;
    private int aeq;
    private int aer;
    private int level;
    private int aeP;
    private int afh;
    private int afi;
    private int afk;
    private int afg;
    private int afl;
    private int afj;
    private int _sp;
    private int curHp;
    private int maxHp;
    private int curMp;
    private int maxMp;
    private int curCp;
    private int maxCp;
    private int aiE;
    private int aiF;
    private int aiG;
    private int aes;
    private int aiH;
    private int aiI;
    private int aiJ;
    private int aiK;
    private int aiL;
    private int aiM;
    private int aiN;
    private int aiO;
    private int aiP;
    private int aex;
    private int aey;
    private int aez;
    private int aiQ;
    private int aeC;
    private int aeD;
    private int aeF;
    private int aiR;
    private int aiS;
    private int aiT;
    private int aeL;
    private int aiU;
    private int aiV;
    private int aiW;
    private int aeh;
    private int aei;
    private int aej;
    private int aiX;
    private int aiY;
    private int aiZ;
    private String _name;
    private String title;
    private long _exp;
    private double aja;
    private double ajb;
    private double aev;
    private double aew;
    private Element ajc;

    public GMViewCharacterInfo(Player player) {
        this.cH = player.getLoc();
        this.afs = player.getObjectId();
        this._name = player.getName();
        this.CV = player.getRace().ordinal();
        this.Dd = player.getSex();
        this.class_id = player.getClassId().getId();
        this.level = player.getLevel();
        this._exp = player.getExp();
        this.afh = player.getSTR();
        this.afk = player.getDEX();
        this.afi = player.getCON();
        this.afg = player.getINT();
        this.afl = player.getWIT();
        this.afj = player.getMEN();
        this.curHp = (int)player.getCurrentHp();
        this.maxHp = player.getMaxHp();
        this.curMp = (int)player.getCurrentMp();
        this.maxMp = player.getMaxMp();
        this._sp = player.getIntSp();
        this.aiE = player.getCurrentLoad();
        this.aiF = player.getMaxLoad();
        this.aiH = player.getPAtk(null);
        this.aiI = player.getPAtkSpd();
        this.aiJ = player.getPDef(null);
        this.aiK = player.getEvasionRate(null);
        this.aiL = player.getAccuracy();
        this.aiM = player.getCriticalHit(null, null);
        this.aiN = player.getMAtk(null, null);
        this.aiO = player.getMAtkSpd();
        this.aiP = player.getMDef(null, null);
        this.aeq = player.getPvpFlag();
        this.aer = player.getKarma();
        this.aeh = player.getRunSpeed();
        this.aei = player.getWalkSpeed();
        this.aej = player.getSwimSpeed();
        this.aja = player.getMovementSpeedMultiplier();
        this.ajb = player.getAttackSpeedMultiplier();
        this.aeP = player.getMountType();
        this.aev = player.getColRadius();
        this.aew = player.getColHeight();
        this.aex = player.getHairStyle();
        this.aey = player.getHairColor();
        this.aez = player.getFace();
        this.aiQ = player.isGM() ? 1 : 0;
        this.title = player.getTitle();
        Clan clan = player.getClan();
        Alliance alliance = clan == null ? null : clan.getAlliance();
        this.aeC = clan == null ? 0 : clan.getClanId();
        this.aeD = clan == null ? 0 : clan.getCrestId();
        this.aeF = alliance == null ? 0 : alliance.getAllyId();
        this.aeL = player.isInObserverMode() ? 7 : player.getPrivateStoreType();
        this.aiX = Math.max(player.getSkillLevel(1320), 0);
        this.aiV = player.getPkKills();
        this.aiW = player.getPvpKills();
        this.aiG = player.getGivableRec();
        this.aes = player.getReceivedRec();
        this.curCp = (int)player.getCurrentCp();
        this.maxCp = player.getMaxCp();
        this.aiY = player.isRunning() ? 1 : 0;
        this.aiZ = player.getPledgeClass();
        this.aiS = player.isNoble() ? 1 : 0;
        this.aiT = player.isHero() ? 1 : 0;
        this.aiU = player.getNameColor();
        this.aiR = player.getTitleColor();
        this.ajc = player.getAttackElement();
        this.aee = new int[17][3];
        for (int n : Inventory.PAPERDOLL_ORDER) {
            this.aee[n][0] = player.getInventory().getPaperdollObjectId(n);
            this.aee[n][1] = player.getInventory().getPaperdollItemId(n);
            this.aee[n][2] = player.getInventory().getPaperdollAugmentationId(n);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(143);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.cH.h);
        this.writeD(this.afs);
        this.writeS(this._name);
        this.writeD(this.CV);
        this.writeD(this.Dd);
        this.writeD(this.class_id);
        this.writeD(this.level);
        this.writeQ(this._exp);
        this.writeD(this.afh);
        this.writeD(this.afk);
        this.writeD(this.afi);
        this.writeD(this.afg);
        this.writeD(this.afl);
        this.writeD(this.afj);
        this.writeD(this.maxHp);
        this.writeD(this.curHp);
        this.writeD(this.maxMp);
        this.writeD(this.curMp);
        this.writeD(this._sp);
        this.writeD(this.aiE);
        this.writeD(this.aiF);
        this.writeD(this.aiV);
        for (int n : Inventory.PAPERDOLL_ORDER) {
            this.writeD(this.aee[n][0]);
        }
        for (int n : Inventory.PAPERDOLL_ORDER) {
            this.writeD(this.aee[n][1]);
        }
        for (int n : Inventory.PAPERDOLL_ORDER) {
            this.writeD(this.aee[n][2]);
        }
        this.writeD(this.aiH);
        this.writeD(this.aiI);
        this.writeD(this.aiJ);
        this.writeD(this.aiK);
        this.writeD(this.aiL);
        this.writeD(this.aiM);
        this.writeD(this.aiN);
        this.writeD(this.aiO);
        this.writeD(this.aiI);
        this.writeD(this.aiP);
        this.writeD(this.aeq);
        this.writeD(this.aer);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aej);
        this.writeD(this.aej);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeF(this.aja);
        this.writeF(this.ajb);
        this.writeF(this.aev);
        this.writeF(this.aew);
        this.writeD(this.aex);
        this.writeD(this.aey);
        this.writeD(this.aez);
        this.writeD(this.aiQ);
        this.writeS(this.title);
        this.writeD(this.aeC);
        this.writeD(this.aeD);
        this.writeD(this.aeF);
        this.writeC(this.aeP);
        this.writeC(this.aeL);
        this.writeC(this.aiX);
        this.writeD(this.aiV);
        this.writeD(this.aiW);
        this.writeH(this.aiG);
        this.writeH(this.aes);
        this.writeD(this.class_id);
        this.writeD(0);
        this.writeD(this.maxCp);
        this.writeD(this.curCp);
        this.writeC(this.aiY);
        this.writeC(321);
        this.writeD(this.aiZ);
        this.writeC(this.aiS);
        this.writeC(this.aiT);
        this.writeD(this.aiU);
        this.writeD(this.aiR);
    }
}

