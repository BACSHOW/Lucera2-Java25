/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.entity.events.objects.ZoneObject;

public class CastleDamageZoneObject
extends ZoneObject {
    private final long _price;

    public CastleDamageZoneObject(String string, long l) {
        super(string);
        this._price = l;
    }

    public long getPrice() {
        return this._price;
    }
}

