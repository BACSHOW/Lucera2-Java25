/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

public class LvlupData {
    private int FS;
    private int FT;
    private double FU;
    private double FV;
    private double FW;
    private double FX;
    private double FY;
    private double FZ;
    private double Ga;
    private double Gb;
    private double Gc;

    public double get_classHpAdd() {
        return this.FU;
    }

    public void set_classHpAdd(double d) {
        this.FU = d;
    }

    public double get_classHpBase() {
        return this.FV;
    }

    public void set_classHpBase(double d) {
        this.FV = d;
    }

    public double get_classHpModifier() {
        return this.FW;
    }

    public void set_classHpModifier(double d) {
        this.FW = d;
    }

    public double get_classCpAdd() {
        return this.FX;
    }

    public void set_classCpAdd(double d) {
        this.FX = d;
    }

    public double get_classCpBase() {
        return this.FY;
    }

    public void set_classCpBase(double d) {
        this.FY = d;
    }

    public double get_classCpModifier() {
        return this.FZ;
    }

    public void set_classCpModifier(double d) {
        this.FZ = d;
    }

    public int get_classid() {
        return this.FS;
    }

    public void set_classid(int n) {
        this.FS = n;
    }

    public int get_classLvl() {
        return this.FT;
    }

    public void set_classLvl(int n) {
        this.FT = n;
    }

    public double get_classMpAdd() {
        return this.Ga;
    }

    public void set_classMpAdd(double d) {
        this.Ga = d;
    }

    public double get_classMpBase() {
        return this.Gb;
    }

    public void set_classMpBase(double d) {
        this.Gb = d;
    }

    public double get_classMpModifier() {
        return this.Gc;
    }

    public void set_classMpModifier(double d) {
        this.Gc = d;
    }
}

