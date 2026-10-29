/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import java.util.Map;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.templates.item.support.FishGroup;
import l2.gameserver.templates.item.support.LureType;

public class LureTemplate {
    private final int _itemId;
    private final int avs;
    private final double avt;
    private final double avu;
    private final LureType avv;
    private final Map<FishGroup, Integer> auX;

    public LureTemplate(MultiValueSet<String> multiValueSet) {
        this._itemId = multiValueSet.getInteger("item_id");
        this.avs = multiValueSet.getInteger("length_bonus");
        this.avt = multiValueSet.getDouble("revision_number");
        this.avu = multiValueSet.getDouble("rate_bonus");
        this.avv = multiValueSet.getEnum("type", LureType.class);
        this.auX = (Map)multiValueSet.get("chances");
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getLengthBonus() {
        return this.avs;
    }

    public double getRevisionNumber() {
        return this.avt;
    }

    public double getRateBonus() {
        return this.avu;
    }

    public LureType getLureType() {
        return this.avv;
    }

    public Map<FishGroup, Integer> getChances() {
        return this.auX;
    }
}

