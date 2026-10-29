/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.mapregion;

import l2.gameserver.model.Territory;
import l2.gameserver.templates.mapregion.RegionData;

public class DomainArea
implements RegionData {
    private final int _id;
    private final Territory Au;

    public DomainArea(int n, Territory territory) {
        this._id = n;
        this.Au = territory;
    }

    public int getId() {
        return this._id;
    }

    @Override
    public Territory getTerritory() {
        return this.Au;
    }
}

