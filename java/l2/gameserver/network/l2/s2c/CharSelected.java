/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.GameTimeController;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class CharSelected extends L2GameServerPacket {
    private int pp;
    private int char_id;
    private int aeC;
    private int sex;
    private int race;
    private int class_id;
    private String _name;
    private String _title;
    private Location cH;
    private double afe;
    private double aff;
    private int _sp;
    private int level;
    private int aer;
    private int afg;
    private int afh;
    private int afi;
    private int afj;
    private int afk;
    private int afl;
    private int Df;
    private long _exp;

    public CharSelected(Player player, int n) {
        this.pp = n;
        this._name = player.getName();
        this.char_id = player.getObjectId();
        this._title = player.getTitle();
        this.aeC = player.getClanId();
        this.sex = player.getSex();
        this.race = player.getRace().ordinal();
        this.class_id = player.getClassId().getId();
        this.cH = player.getLoc();
        this.afe = player.getCurrentHp();
        this.aff = player.getCurrentMp();
        this._sp = player.getIntSp();
        this._exp = player.getExp();
        this.level = player.getLevel();
        this.aer = player.getKarma();
        this.Df = player.getPkKills();
        this.afg = player.getINT();
        this.afh = player.getSTR();
        this.afi = player.getCON();
        this.afj = player.getMEN();
        this.afk = player.getDEX();
        this.afl = player.getWIT();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(21);
        this.writeS(this._name);
        this.writeD(this.char_id);
        this.writeS(this._title);
        this.writeD(this.pp);
        this.writeD(this.aeC);
        this.writeD(0);
        this.writeD(this.sex);
        this.writeD(this.race);
        this.writeD(this.class_id);
        this.writeD(1);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeF(this.afe);
        this.writeF(this.aff);
        this.writeD(this._sp);
        this.writeQ(this._exp);
        this.writeD(this.level);
        this.writeD(this.aer);
        this.writeD(this.Df);
        this.writeD(this.afg);
        this.writeD(this.afh);
        this.writeD(this.afi);
        this.writeD(this.afj);
        this.writeD(this.afk);
        this.writeD(this.afl);
        for (int i = 0; i < 30; ++i) {
            this.writeD(0);
        }
        this.writeD(0); // c3 work
        this.writeD(0); // c3 work
        this.writeD(GameTimeController.getInstance().getGameTime());
        this.writeD(0); // c3
        this.writeD(this.class_id);
        this.writeD(0); // c3 InspectorBin
        this.writeD(0); // c3
        this.writeD(0); // c3
        this.writeD(0); // c3
    }
}
