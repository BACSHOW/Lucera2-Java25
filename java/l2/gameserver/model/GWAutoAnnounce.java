/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;

public class GWAutoAnnounce {
    private final int _id;
    private ArrayList<String> Fq;
    private int Fr;
    private long Fs;
    private boolean Ft;

    public GWAutoAnnounce(int n) {
        this._id = n;
    }

    public int getId() {
        return this._id;
    }

    public void setScreenAnnounce(boolean bl) {
        this.Ft = bl;
    }

    public boolean isScreenAnnounce() {
        return this.Ft;
    }

    public void setAnnounce(int n, int n2, ArrayList<String> arrayList) {
        this.Fs = System.currentTimeMillis() + (long)(n * 1000);
        this.Fr = n2;
        this.Fq = arrayList;
    }

    public void updateRepeat() {
        this.Fs = System.currentTimeMillis() + (long)(this.Fr * 1000);
    }

    public boolean canAnnounce() {
        return System.currentTimeMillis() > this.Fs;
    }

    public ArrayList<String> getMessage() {
        return this.Fq;
    }
}

