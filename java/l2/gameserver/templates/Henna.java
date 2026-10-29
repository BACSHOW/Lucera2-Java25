/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import gnu.trove.TIntArrayList;
import l2.gameserver.model.Player;

public class Henna {
    private final int _symbolId;
    private final int ash;
    private final long _price;
    private final long asi;
    private final int asj;
    private final int ask;
    private final int asl;
    private final int asm;
    private final int asn;
    private final int aso;
    private final TIntArrayList asp;

    public Henna(int n, int n2, long l, long l2, int n3, int n4, int n5, int n6, int n7, int n8, TIntArrayList tIntArrayList) {
        this._symbolId = n;
        this.ash = n2;
        this._price = l;
        this.asi = l2;
        this.asj = n4;
        this.ask = n6;
        this.asl = n5;
        this.asm = n8;
        this.asn = n7;
        this.aso = n3;
        this.asp = tIntArrayList;
    }

    public int getSymbolId() {
        return this._symbolId;
    }

    public int getDyeId() {
        return this.ash;
    }

    public long getPrice() {
        return this._price;
    }

    public int getStatINT() {
        return this.asj;
    }

    public int getStatSTR() {
        return this.ask;
    }

    public int getStatCON() {
        return this.asl;
    }

    public int getStatMEN() {
        return this.asm;
    }

    public int getStatDEX() {
        return this.asn;
    }

    public int getStatWIT() {
        return this.aso;
    }

    public boolean isForThisClass(Player player) {
        return this.asp.contains(player.getActiveClassId());
    }

    public long getDrawCount() {
        return this.asi;
    }
}

