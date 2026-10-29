/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.Location;

public class NpcInfo
extends L2GameServerPacket {
    private boolean ajR = false;
    private int _npcObjId;
    private int _npcId;
    private int aiY;
    private int ajS;
    private int ajT;
    private int ajU;
    private int aeh;
    private int aei;
    private int aef;
    private int aeg;
    private int ajV;
    private int ajW;
    private int ajX;
    private int aer;
    private int aeq;
    private int aeA;
    private int aeB;
    private int aeC;
    private int aeD;
    private int aeF;
    private int aeG;
    private int ajY;
    private int ajZ;
    private double aka;
    private double akb;
    private double akc;
    private double akd;
    private double aet;
    private double aeu;
    private boolean ake;
    private boolean akf;
    private boolean akg;
    private Location cH;
    private String _name = "";
    private String _title = "";
    private boolean UB;
    private int _state;
    private TeamType _team;

    public int getObjectId() { return this._npcObjId; }

    public NpcInfo(NpcInstance npcInstance, Creature creature) {
        this._npcId = npcInstance.getDisplayId() != 0 ? npcInstance.getDisplayId() : npcInstance.getTemplate().npcId;
        this.ake = creature != null && npcInstance.isAutoAttackable(creature);
        this.ajV = npcInstance.getRightHandItem();
        this.ajW = npcInstance.getLeftHandItem();
        int n = this.ajX = this.ajW > 0 || this.ajV > 0 ? npcInstance.getTemplate().weaponEnchant : 0;
        if (Config.SERVER_SIDE_NPC_NAME || npcInstance.getTemplate().displayId != 0 || npcInstance.getName() != npcInstance.getTemplate().name) {
            this._name = npcInstance.getName();
        }
        if (Config.SERVER_SIDE_NPC_TITLE || npcInstance.getTemplate().displayId != 0 || npcInstance.getTitle() != npcInstance.getTemplate().title) {
            this._title = npcInstance.getTitle();
        }
        if (Config.SERVER_SIDE_MONSTER_LEVEL_TITLE && npcInstance instanceof MonsterInstance) {
            this._title = "Lv " + npcInstance.getLevel() + (npcInstance.getAggroRange() > 0 ? "*" : "") + " " + npcInstance.getTitle();
        }
        this.ajU = npcInstance.getSpawnAnimation();
        this.UB = npcInstance.isShowName();
        this._state = npcInstance.getNpcState();
        this.k(npcInstance);
    }

    public NpcInfo(Summon summon, Creature creature) {
        if (summon.getPlayer() != null && summon.getPlayer().isInvisible()) {
            return;
        }
        this._npcId = summon.getTemplate().npcId;
        this.ake = summon.isAutoAttackable(creature);
        this.ajV = 0;
        this.ajW = 0;
        this.ajX = 0;
        this.UB = true;
        this._name = summon.getName();
        this._title = summon.getTitle();
        this.ajU = summon.getSpawnAnimation();
        this.k(summon);
    }

    private void k(Creature creature) {
        this.aka = creature.getTemplate().collisionHeight;
        this.akb = creature.getTemplate().collisionRadius;
        this.akc = creature.getColHeight();
        this.akd = creature.getColRadius();
        this._npcObjId = creature.getObjectId();
        this.cH = creature.getLoc();
        this.aef = creature.getMAtkSpd();
        if (Config.ALT_NPC_CLAN == 0) {
            Clan clan = creature.getClan();
            Alliance alliance = clan == null ? null : clan.getAlliance();
            this.aeC = clan == null ? 0 : clan.getClanId();
            this.aeD = clan == null ? 0 : clan.getCrestId();
            this.aeF = alliance == null ? 0 : alliance.getAllyId();
            this.aeG = alliance == null ? 0 : alliance.getAllyCrestId();
        } else if (creature instanceof NpcInstance && Config.ALT_NPC_CLAN > 0 && ((NpcInstance)creature).getCastle() != null) {
            Clan clan = ClanTable.getInstance().getClan(Config.ALT_NPC_CLAN);
            Alliance alliance = clan == null ? null : clan.getAlliance();
            this.aeC = clan == null ? 0 : clan.getClanId();
            this.aeD = clan == null ? 0 : clan.getCrestId();
            this.aeF = alliance == null ? 0 : alliance.getAllyId();
            this.aeG = alliance == null ? 0 : alliance.getAllyCrestId();
        } else {
            this.aeC = 0;
            this.aeD = 0;
            this.aeF = 0;
            this.aeG = 0;
        }
        this.aet = (double)creature.getRunSpeed() * 1.0 / (double)creature.getTemplate().baseRunSpd;
        this.aeu = creature.getAttackSpeedMultiplier();
        this.aeh = (int)((double)creature.getRunSpeed() / this.aet);
        this.aei = (int)((double)creature.getWalkSpeed() / this.aet);
        this.aer = creature.getKarma();
        this.aeq = creature.getPvpFlag();
        this.aeg = creature.getPAtkSpd();
        this.aiY = creature.isRunning() ? 1 : 0;
        this.ajS = creature.isInCombat() ? 1 : 0;
        this.ajT = creature.isAlikeDead() ? 1 : 0;
        this.aeA = creature.getAbnormalEffect();
        this.aeB = creature.getAbnormalEffect2();
        this.akg = creature.isFlying();
        this._team = creature.getTeam();
        this.ajY = creature.getFormId();
        this.akf = creature.isNameAbove();
        this.ajZ = creature.isSummon() || creature.isPet() ? 1 : 0;
        this.ajR = true;
    }

    public NpcInfo update() {
        this.ajU = 1;
        return this;
    }

    @Override
    protected final void writeImpl() {
        if (!this.ajR) {
            return;
        }
        this.writeC(22);
        this.writeD(this._npcObjId);
        this.writeD(this._npcId + 1000000);
        this.writeD(this.ake ? 1 : 0);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z + Config.CLIENT_Z_SHIFT);
        this.writeD(this.cH.h);
        this.writeD(0);
        this.writeD(this.aef);
        this.writeD(this.aeg);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeF(this.aet);
        this.writeF(this.aeu);
        this.writeF(this.akb);
        this.writeF(this.aka);
        this.writeD(this.ajV);
        this.writeD(0);
        this.writeD(this.ajW);
        this.writeC(this.akf ? 1 : 0);
        this.writeC(this.aiY);
        this.writeC(this.ajS);
        this.writeC(this.ajT);
        this.writeC(this.ajU);
        this.writeS(this._name);
        this.writeS(this._title);
        this.writeD(this.ajZ);
        this.writeD(this.aeq);
        this.writeD(this.aer);
        this.writeD(this.aeA);
        this.writeD(this.aeC);
        this.writeD(this.aeD);
        this.writeD(this.aeF);
        this.writeD(this.aeG);
        this.writeC(this.akg ? 2 : 0);
        this.writeC(this._team.ordinal());
        this.writeF(this.akd);
        this.writeF(this.akc);
        this.writeD(this.ajX);
        this.writeD(this.akg ? 1 : 0);
    }
}
