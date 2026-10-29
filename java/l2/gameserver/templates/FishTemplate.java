/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

public class FishTemplate {
    private final int _id;
    private final int _level;
    private final String _name;
    private final int asa;
    private final int asb;
    private final int _type;
    private final int asc;
    private final int asd;
    private final int ase;
    private final int asf;
    private final int asg;

    public FishTemplate(int n, int n2, String string, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
        this._id = n;
        this._level = n2;
        this._name = string.intern();
        this.asa = n3;
        this.asb = n4;
        this._type = n5;
        this.asc = n6;
        this.asd = n7;
        this.ase = n8;
        this.asf = n9;
        this.asg = n10;
    }

    public int getId() {
        return this._id;
    }

    public int getLevel() {
        return this._level;
    }

    public String getName() {
        return this._name;
    }

    public int getHP() {
        return this.asa;
    }

    public int getHpRegen() {
        return this.asb;
    }

    public int getType() {
        return this._type;
    }

    public int getGroup() {
        return this.asc;
    }

    public int getFishGuts() {
        return this.asd;
    }

    public int getGutsCheckTime() {
        return this.ase;
    }

    public int getWaitTime() {
        return this.asf;
    }

    public int getCombatTime() {
        return this.asg;
    }
}

