/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import l2.gameserver.Config;
import l2.gameserver.model.DeathPenalty;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;

public class SubClass {
    private int Lw = 0;
    private long _exp;
    private long Lx = Experience.LEVEL[Config.ALT_LEVEL_AFTER_GET_SUBCLASS];
    private long Ly = Experience.LEVEL[Experience.LEVEL.length - 1];
    private int _sp = 0;
    private int _level = Config.ALT_LEVEL_AFTER_GET_SUBCLASS;
    private double Lz = 1.0;
    private double LA = 1.0;
    private double LB = 1.0;
    private boolean _active = false;
    private boolean LC = false;
    private DeathPenalty LD;

    public SubClass() {
        this._exp = Math.max(this.Lx, Experience.LEVEL[this._level]);
    }

    public int getClassId() {
        return this.Lw;
    }

    public long getExp() {
        return this._exp;
    }

    public long getMaxExp() {
        return this.Ly;
    }

    public void addExp(long l) {
        this.setExp(this._exp + l);
    }

    public long getSp() {
        return Math.min(this._sp, Integer.MAX_VALUE);
    }

    public void addSp(long l) {
        this.setSp((long)this._sp + l);
    }

    public int getLevel() {
        return this._level;
    }

    public void setClassId(int n) {
        this.Lw = n;
    }

    public void setExp(long l) {
        l = Math.max(l, this.Lx);
        this._exp = l = Math.min(l, this.Ly);
        this._level = Experience.getLevel(this._exp);
    }

    public void setSp(long l) {
        l = Math.max(l, 0L);
        l = Math.min(l, Integer.MAX_VALUE);
        this._sp = (int)l;
    }

    public void setHp(double d) {
        this.Lz = d;
    }

    public double getHp() {
        return this.Lz;
    }

    public void setMp(double d) {
        this.LA = d;
    }

    public double getMp() {
        return this.LA;
    }

    public void setCp(double d) {
        this.LB = d;
    }

    public double getCp() {
        return this.LB;
    }

    public void setActive(boolean bl) {
        this._active = bl;
    }

    public boolean isActive() {
        return this._active;
    }

    public void setBase(boolean bl) {
        this.LC = bl;
        this.Lx = Experience.LEVEL[this.LC ? 1 : Config.ALT_LEVEL_AFTER_GET_SUBCLASS];
        this.Ly = Experience.LEVEL[(this.LC ? Experience.getMaxLevel() : Experience.getMaxSubLevel()) + 1] - 1L;
    }

    public boolean isBase() {
        return this.LC;
    }

    public DeathPenalty getDeathPenalty(Player player) {
        if (this.LD == null) {
            this.LD = new DeathPenalty(player, 0);
        }
        return this.LD;
    }

    public void setDeathPenalty(DeathPenalty deathPenalty) {
        this.LD = deathPenalty;
    }

    public String toString() {
        return ClassId.VALUES[this.Lw].toString() + " " + this._level;
    }
}

