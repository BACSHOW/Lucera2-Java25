/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

public class PetData {
    private int _id;
    private int _level;
    private int Gz;
    private int GA;
    private int GB;
    private int GC;
    private int GD;
    private int GE;
    private int GF;
    private int _hp;
    private int GG;
    private int GH;
    private int GI;
    private long _exp;
    private int _accuracy;
    private int _evasion;
    private int GJ;
    private int GK;
    private int GL;
    private int _castSpeed;
    private int GN;
    private int GO;
    private int GP;
    private int p;
    private int GQ;
    private boolean GR;

    public int getFeedBattle() {
        return this.GA;
    }

    public void setFeedBattle(int n) {
        this.GA = n;
    }

    public int getFeedNormal() {
        return this.GB;
    }

    public void setFeedNormal(int n) {
        this.GB = n;
    }

    public int getHP() {
        return this._hp;
    }

    public void setHP(int n) {
        this._hp = n;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int n) {
        this._id = n;
    }

    public int getLevel() {
        return this._level;
    }

    public void setLevel(int n) {
        this._level = n;
    }

    public int getMAtk() {
        return this.GE;
    }

    public void setMAtk(int n) {
        this.GE = n;
    }

    public int getFeedMax() {
        return this.Gz;
    }

    public void setFeedMax(int n) {
        this.Gz = n;
    }

    public int getMDef() {
        return this.GF;
    }

    public void setMDef(int n) {
        this.GF = n;
    }

    public long getExp() {
        return this._exp;
    }

    public void setExp(long l) {
        this._exp = l;
    }

    public int getMP() {
        return this.GG;
    }

    public void setMP(int n) {
        this.GG = n;
    }

    public int getPAtk() {
        return this.GC;
    }

    public void setPAtk(int n) {
        this.GC = n;
    }

    public int getPDef() {
        return this.GD;
    }

    public int getAccuracy() {
        return this._accuracy;
    }

    public int getEvasion() {
        return this._evasion;
    }

    public int getCritical() {
        return this.GJ;
    }

    public int getSpeed() {
        return this.GK;
    }

    public int getAtkSpeed() {
        return this.GL;
    }

    public int getCastSpeed() {
        return this._castSpeed;
    }

    public int getMaxLoad() {
        return this.GN != 0 ? this.GN : this._level * 300;
    }

    public void setPDef(int n) {
        this.GD = n;
    }

    public int getHpRegen() {
        return this.GH;
    }

    public void setHpRegen(int n) {
        this.GH = n;
    }

    public int getMpRegen() {
        return this.GI;
    }

    public void setMpRegen(int n) {
        this.GI = n;
    }

    public void setAccuracy(int n) {
        this._accuracy = n;
    }

    public void setEvasion(int n) {
        this._evasion = n;
    }

    public void setCritical(int n) {
        this.GJ = n;
    }

    public void setSpeed(int n) {
        this.GK = n;
    }

    public void setAtkSpeed(int n) {
        this.GL = n;
    }

    public void setCastSpeed(int n) {
        this._castSpeed = n;
    }

    public void setMaxLoad(int n) {
        this.GN = n;
    }

    public int getControlItemId() {
        return this.GO;
    }

    public void setControlItemId(int n) {
        this.GO = n;
    }

    public int getFoodId() {
        return this.GP;
    }

    public void setFoodId(int n) {
        this.GP = n;
    }

    public int getMinLevel() {
        return this.p;
    }

    public void setMinLevel(int n) {
        this.p = n;
    }

    public int getAddFed() {
        return this.GQ;
    }

    public void setAddFed(int n) {
        this.GQ = n;
    }

    public boolean isMountable() {
        return this.GR;
    }

    public void setMountable(boolean bl) {
        this.GR = bl;
    }
}

