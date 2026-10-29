/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Calendar;
import l2.gameserver.model.ProductItemComponent;

public class ProductItem {
    public static final long NOT_LIMITED_START_TIME = 315547200000L;
    public static final long NOT_LIMITED_END_TIME = 2127445200000L;
    public static final int NOT_LIMITED_START_HOUR = 0;
    public static final int NOT_LIMITED_END_HOUR = 23;
    public static final int NOT_LIMITED_START_MIN = 0;
    public static final int NOT_LIMITED_END_MIN = 59;
    private final int Kw;
    private final int jr;
    private final int _points;
    private final int Kx;
    private final long Ky;
    private final long Kz;
    private final int KA;
    private final int KB;
    private final int KC;
    private final int KD;
    private ArrayList<ProductItemComponent> KE;

    public ProductItem(int n, int n2, int n3, int n4, long l, long l2) {
        Calendar calendar;
        this.Kw = n;
        this.jr = n2;
        this._points = n3;
        this.Kx = n4;
        if (l > 0L) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(l);
            this.Ky = l;
            this.KA = calendar.get(11);
            this.KC = calendar.get(12);
        } else {
            this.Ky = 315547200000L;
            this.KA = 0;
            this.KC = 0;
        }
        if (l2 > 0L) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(l2);
            this.Kz = l2;
            this.KB = calendar.get(11);
            this.KD = calendar.get(12);
        } else {
            this.Kz = 2127445200000L;
            this.KB = 23;
            this.KD = 59;
        }
    }

    public void setComponents(ArrayList<ProductItemComponent> arrayList) {
        this.KE = arrayList;
    }

    public ArrayList<ProductItemComponent> getComponents() {
        if (this.KE == null) {
            this.KE = new ArrayList();
        }
        return this.KE;
    }

    public int getProductId() {
        return this.Kw;
    }

    public int getCategory() {
        return this.jr;
    }

    public int getPoints() {
        return this._points;
    }

    public int getTabId() {
        return this.Kx;
    }

    public long getStartTimeSale() {
        return this.Ky;
    }

    public int getStartHour() {
        return this.KA;
    }

    public int getStartMin() {
        return this.KC;
    }

    public long getEndTimeSale() {
        return this.Kz;
    }

    public int getEndHour() {
        return this.KB;
    }

    public int getEndMin() {
        return this.KD;
    }
}

