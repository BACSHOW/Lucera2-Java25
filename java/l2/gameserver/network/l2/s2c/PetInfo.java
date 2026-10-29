/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Summon;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.utils.Location;

public class PetInfo
extends L2GameServerPacket {
    private int aeh;
    private int aei;
    private int akz;
    private int akA;
    private int aeq;
    private int aer;
    private int akB;
    private int _type;
    private int afs;
    private int akC;
    private int akD;
    private int ajS;
    private int ajT;
    private int _sp;
    private int level;
    private int aeA;
    private int aeB;
    private int akE;
    private int akF;
    private int curHp;
    private int maxHp;
    private int curMp;
    private int maxMp;
    private int aiE;
    private int aiF;
    private int akG;
    private int akH;
    private int akI;
    private int akJ;
    private int akK;
    private int akL;
    private int akM;
    private int akN;
    private int akO;
    private int type;
    private int ajU;
    private Location cH;
    private double akP;
    private double aew;
    private long akQ;
    private long akR;
    private long akS;
    private String _name;
    private String title;
    private TeamType _team;

    public PetInfo(Summon summon) {
        this._type = summon.getSummonType();
        this.afs = summon.getObjectId();
        this.akC = summon.getTemplate().npcId;
        this.cH = summon.getLoc();
        this.akz = summon.getMAtkSpd();
        this.akA = summon.getPAtkSpd();
        this.aeh = summon.getRunSpeed();
        this.aei = summon.getWalkSpeed();
        this.akP = summon.getColRadius();
        this.aew = summon.getColHeight();
        this.akD = summon.isRunning() ? 1 : 0;
        this.ajS = summon.isInCombat() ? 1 : 0;
        this.ajT = summon.isAlikeDead() ? 1 : 0;
        this._name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();
        this.title = summon.getTitle();
        this.aeq = summon.getPvpFlag();
        this.aer = summon.getKarma();
        this.akE = summon.getCurrentFed();
        this.akF = summon.getMaxFed();
        this.curHp = (int)summon.getCurrentHp();
        this.maxHp = summon.getMaxHp();
        this.curMp = (int)summon.getCurrentMp();
        this.maxMp = summon.getMaxMp();
        this._sp = summon.getSp();
        this.level = summon.getLevel();
        this.akQ = summon.getExp();
        this.akR = summon.getExpForThisLevel();
        this.akS = summon.getExpForNextLevel();
        this.aiE = summon.isPet() ? summon.getInventory().getTotalWeight() : 0;
        this.aiF = summon.getMaxLoad();
        this.akG = summon.getPAtk(null);
        this.akH = summon.getPDef(null);
        this.akI = summon.getMAtk(null, null);
        this.akJ = summon.getMDef(null, null);
        this.akK = summon.getAccuracy();
        this.akL = summon.getEvasionRate(null);
        this.akM = summon.getCriticalHit(null, null);
        this.aeA = summon.getAbnormalEffect();
        this.aeB = summon.getAbnormalEffect2();
        this.akB = summon.getPlayer().getTransformation() != 0 || summon.getPlayer().isCursedWeaponEquipped() ? 0 : (PetDataTable.isMountable(this.akC) ? 1 : 0);
        this._team = summon.getTeam();
        this.akO = summon.getSoulshotConsumeCount();
        this.akN = summon.getSpiritshotConsumeCount();
        this.ajU = summon.getSpawnAnimation();
        this.type = summon.getFormId();
    }

    public PetInfo update() {
        this.ajU = 1;
        return this;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(177);
        this.writeD(this._type);
        this.writeD(this.afs);
        this.writeD(this.akC + 1000000);
        this.writeD(0);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.cH.h);
        this.writeD(0);
        this.writeD(this.akz);
        this.writeD(this.akA);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeF(1.0);
        this.writeF(1.0);
        this.writeF(this.akP);
        this.writeF(this.aew);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeC(1);
        this.writeC(this.akD);
        this.writeC(this.ajS);
        this.writeC(this.ajT);
        this.writeC(this.ajU);
        this.writeS(this._name);
        this.writeS(this.title);
        this.writeD(1);
        this.writeD(this.aeq);
        this.writeD(this.aer);
        this.writeD(this.akE);
        this.writeD(this.akF);
        this.writeD(this.curHp);
        this.writeD(this.maxHp);
        this.writeD(this.curMp);
        this.writeD(this.maxMp);
        this.writeD(this._sp);
        this.writeD(this.level);
        this.writeQ(this.akQ);
        this.writeQ(this.akR);
        this.writeQ(this.akS);
        this.writeD(this.aiE);
        this.writeD(this.aiF);
        this.writeD(this.akG);
        this.writeD(this.akH);
        this.writeD(this.akI);
        this.writeD(this.akJ);
        this.writeD(this.akK);
        this.writeD(this.akL);
        this.writeD(this.akM);
        this.writeD(this.aeh);
        this.writeD(this.akA);
        this.writeD(this.akz);
        this.writeD(this.aeA);
        this.writeH(this.akB);
        this.writeC(0);
        this.writeH(0);
        this.writeC(this._team.ordinal());
        this.writeD(this.akO);
        this.writeD(this.akN);
    }
}

