/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.mapregion;

import java.util.List;
import l2.gameserver.utils.Location;

public class RestartPoint {
    private final String _name;
    private final int avF;
    private final int avG;
    private final List<Location> atL;
    private final List<Location> atM;

    public RestartPoint(String string, int n, int n2, List<Location> list, List<Location> list2) {
        this._name = string;
        this.avF = n;
        this.avG = n2;
        this.atL = list;
        this.atM = list2;
    }

    public String getName() {
        return this._name;
    }

    public int getBbs() {
        return this.avF;
    }

    public int getMsgId() {
        return this.avG;
    }

    public List<Location> getRestartPoints() {
        return this.atL;
    }

    public List<Location> getPKrestartPoints() {
        return this.atM;
    }
}

