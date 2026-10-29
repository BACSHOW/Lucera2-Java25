/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.World;
import l2.gameserver.templates.mapregion.RegionData;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

public class MapRegionManager
extends AbstractHolder {
    private static final MapRegionManager Ay = new MapRegionManager();
    private RegionData[][][] Az = new RegionData[World.WORLD_SIZE_X][World.WORLD_SIZE_Y][0];

    public static MapRegionManager getInstance() {
        return Ay;
    }

    private MapRegionManager() {
    }

    private int regionX(int n) {
        return n - World.MAP_MIN_X >> 15;
    }

    private int regionY(int n) {
        return n - World.MAP_MIN_Y >> 15;
    }

    public void addRegionData(RegionData regionData) {
        for (int i = this.regionX(regionData.getTerritory().getXmin()); i <= this.regionX(regionData.getTerritory().getXmax()); ++i) {
            for (int j = this.regionY(regionData.getTerritory().getYmin()); j <= this.regionY(regionData.getTerritory().getYmax()); ++j) {
                this.Az[i][j] = ArrayUtils.add(this.Az[i][j], regionData);
            }
        }
    }

    public <T extends RegionData> T getRegionData(Class<T> clazz, GameObject gameObject) {
        return this.getRegionData(clazz, gameObject.getX(), gameObject.getY(), gameObject.getZ());
    }

    public <T extends RegionData> T getRegionData(Class<T> clazz, Location location) {
        return this.getRegionData(clazz, location.getX(), location.getY(), location.getZ());
    }

    public <T extends RegionData> T getRegionData(Class<T> clazz, int n, int n2, int n3) {
        for (RegionData regionData : this.Az[this.regionX(n)][this.regionY(n2)]) {
            if (regionData.getClass() != clazz || !regionData.getTerritory().isInside(n, n2, n3)) continue;
            return (T)regionData;
        }
        return null;
    }

    @Override
    public int size() {
        return World.WORLD_SIZE_X * World.WORLD_SIZE_Y;
    }

    @Override
    public void clear() {
    }
}

