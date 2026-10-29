/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;

public class StaticObjectTemplate {
    private final int Rm;
    private final int _type;
    private final String atI;
    private final int atJ;
    private final int atK;
    private final String _name;
    private final int _x;
    private final int _y;
    private final int Di;
    private final boolean QC;

    public StaticObjectTemplate(StatsSet statsSet) {
        this.Rm = statsSet.getInteger("uid");
        this._type = statsSet.getInteger("stype");
        this.atJ = statsSet.getInteger("map_x");
        this.atK = statsSet.getInteger("map_y");
        this.atI = statsSet.getString("path");
        this._name = statsSet.getString("name");
        this._x = statsSet.getInteger("x");
        this._y = statsSet.getInteger("y");
        this.Di = statsSet.getInteger("z");
        this.QC = statsSet.getBool("spawn");
    }

    public int getUId() {
        return this.Rm;
    }

    public int getType() {
        return this._type;
    }

    public String getFilePath() {
        return this.atI;
    }

    public int getMapX() {
        return this.atJ;
    }

    public int getMapY() {
        return this.atK;
    }

    public String getName() {
        return this._name;
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }

    public int getZ() {
        return this.Di;
    }

    public boolean isSpawn() {
        return this.QC;
    }

    public StaticObjectInstance newInstance() {
        StaticObjectInstance staticObjectInstance = new StaticObjectInstance(IdFactory.getInstance().getNextId(), this);
        staticObjectInstance.spawnMe(new Location(this.getX(), this.getY(), this.getZ()));
        return staticObjectInstance;
    }
}

