/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;

public class TeleportLocation
extends Location {
    private final long _price;
    private final int p;
    private final int s;
    private final ItemTemplate KP;
    private final String _name;
    private final int LM;
    private final int LN;

    public TeleportLocation(int n, long l, int n2, int n3, String string, int n4, int n5) {
        this._price = l;
        this.p = n2;
        this.s = n3;
        this._name = string;
        this.KP = ItemHolder.getInstance().getTemplate(n);
        this.LM = n4;
        this.LN = n5;
    }

    public TeleportLocation(int n, long l, String string, int n2, int n3) {
        this._price = l;
        this.LN = n3;
        this.p = 0;
        this.s = 0;
        this._name = string;
        this.KP = ItemHolder.getInstance().getTemplate(n);
        this.LM = n2;
    }

    public int getMinLevel() {
        return this.p;
    }

    public int getMaxLevel() {
        return this.s;
    }

    public long getPrice() {
        return this._price;
    }

    public ItemTemplate getItem() {
        return this.KP;
    }

    public String getName() {
        return this._name;
    }

    public int getCastleId() {
        return this.LM;
    }

    public int getKeyItemId() {
        return this.LN;
    }
}

