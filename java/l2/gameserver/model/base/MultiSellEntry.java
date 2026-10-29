/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.base.MultiSellIngredient;

public class MultiSellEntry {
    private int OB;
    private List<MultiSellIngredient> OC = new ArrayList<MultiSellIngredient>();
    private List<MultiSellIngredient> OD = new ArrayList<MultiSellIngredient>();
    private long OE;

    public MultiSellEntry() {
    }

    public MultiSellEntry(int n) {
        this.OB = n;
    }

    public MultiSellEntry(int n, int n2, int n3, int n4) {
        this.OB = n;
        this.addProduct(new MultiSellIngredient(n2, n3, n4));
    }

    public void setEntryId(int n) {
        this.OB = n;
    }

    public int getEntryId() {
        return this.OB;
    }

    public void addIngredient(MultiSellIngredient multiSellIngredient) {
        if (multiSellIngredient.getItemCount() > 0L) {
            this.OC.add(multiSellIngredient);
        }
    }

    public List<MultiSellIngredient> getIngredients() {
        return this.OC;
    }

    public void addProduct(MultiSellIngredient multiSellIngredient) {
        this.OD.add(multiSellIngredient);
    }

    public List<MultiSellIngredient> getProduction() {
        return this.OD;
    }

    public long getTax() {
        return this.OE;
    }

    public void setTax(long l) {
        this.OE = l;
    }

    public int hashCode() {
        return this.OB;
    }

    public MultiSellEntry clone() {
        MultiSellEntry multiSellEntry = new MultiSellEntry(this.OB);
        for (MultiSellIngredient multiSellIngredient : this.OC) {
            multiSellEntry.addIngredient(multiSellIngredient.clone());
        }
        for (MultiSellIngredient multiSellIngredient : this.OD) {
            multiSellEntry.addProduct(multiSellIngredient.clone());
        }
        return multiSellEntry;
    }
}

