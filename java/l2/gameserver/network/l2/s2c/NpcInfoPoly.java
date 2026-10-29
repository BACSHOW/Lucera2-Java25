/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.npc.NpcTemplate;

public class NpcInfoPoly
extends L2GameServerPacket {
    private Creature akh;
    private int _x;
    private int _y;
    private int Di;
    private int Ef;
    private int _npcId;
    private boolean aki;
    private boolean akj;
    private boolean akk;
    private boolean akl;
    private int aef;
    private int aeg;
    private int aeh;
    private int aei;
    private int akm;
    private int akn;
    private int aek;
    private int ael;
    private int aem;
    private int aen;
    private int ajV;
    private int ajW;
    private String _name;
    private String _title;
    private int aeA;
    private int aeB;
    private double akb;
    private double aka;
    private TeamType _team;

    public NpcInfoPoly(Player player) {
        this.akh = player;
        this._npcId = player.getPolyId();
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(this._npcId);
        this.ajV = 0;
        this.ajW = 0;
        this.aki = false;
        this.akb = npcTemplate.collisionRadius;
        this.aka = npcTemplate.collisionHeight;
        this._x = this.akh.getX();
        this._y = this.akh.getY();
        this.Di = this.akh.getZ();
        this.ajV = npcTemplate.rhand;
        this.ajW = npcTemplate.lhand;
        this.Ef = player.getHeading();
        this.aef = player.getMAtkSpd();
        this.aeg = player.getPAtkSpd();
        this.aeh = player.getRunSpeed();
        this.aei = player.getWalkSpeed();
        this.aek = this.aem = this.aeh;
        this.akm = this.aem;
        this.ael = this.aen = this.aei;
        this.akn = this.aen;
        this.akj = player.isRunning();
        this.akk = player.isInCombat();
        this.akl = player.isAlikeDead();
        this._name = player.getName();
        this._title = player.getTitle();
        this.aeA = player.getAbnormalEffect();
        this.aeB = player.getAbnormalEffect2();
        this._team = player.getTeam();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(22);
        this.writeD(this.akh.getObjectId());
        this.writeD(this._npcId + 1000000);
        this.writeD(0);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
        this.writeD(this.Ef);
        this.writeD(0);
        this.writeD(this.aef);
        this.writeD(this.aeg);
        this.writeD(this.aeh);
        this.writeD(this.aei);
        this.writeD(this.akm);
        this.writeD(this.akn);
        this.writeD(this.aek);
        this.writeD(this.ael);
        this.writeD(this.aem);
        this.writeD(this.aen);
        this.writeF(1.0);
        this.writeF(1.0);
        this.writeF(this.akb);
        this.writeF(this.aka);
        this.writeD(this.ajV);
        this.writeD(0);
        this.writeD(this.ajW);
        this.writeC(1);
        this.writeC(this.akj ? 1 : 0);
        this.writeC(this.akk ? 1 : 0);
        this.writeC(this.akl ? 1 : 0);
        this.writeC(this.aki ? 2 : 0);
        this.writeS(this._name);
        this.writeS(this._title);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeD(this.aeA);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeC(0);
        this.writeC(this._team.ordinal());
        this.writeF(this.akb);
        this.writeF(this.aka);
        this.writeD(0);
        this.writeD(0);
    }
}

