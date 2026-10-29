/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.io.Serializable;
import l2.gameserver.model.base.Element;

public class ItemAttributes
implements Serializable {
    private static final long serialVersionUID = 401594188363005415L;
    private int Wx;
    private int Wy;
    private int Wz;
    private int WA;
    private int WB;
    private int WC;

    public ItemAttributes() {
        this(0, 0, 0, 0, 0, 0);
    }

    public ItemAttributes(int n, int n2, int n3, int n4, int n5, int n6) {
        this.Wx = n;
        this.Wy = n2;
        this.Wz = n3;
        this.WA = n4;
        this.WB = n5;
        this.WC = n6;
    }

    public int getFire() {
        return this.Wx;
    }

    public void setFire(int n) {
        this.Wx = n;
    }

    public int getWater() {
        return this.Wy;
    }

    public void setWater(int n) {
        this.Wy = n;
    }

    public int getWind() {
        return this.Wz;
    }

    public void setWind(int n) {
        this.Wz = n;
    }

    public int getEarth() {
        return this.WA;
    }

    public void setEarth(int n) {
        this.WA = n;
    }

    public int getHoly() {
        return this.WB;
    }

    public void setHoly(int n) {
        this.WB = n;
    }

    public int getUnholy() {
        return this.WC;
    }

    public void setUnholy(int n) {
        this.WC = n;
    }

    public Element getElement() {
        if (this.Wx > 0) {
            return Element.FIRE;
        }
        if (this.Wy > 0) {
            return Element.WATER;
        }
        if (this.Wz > 0) {
            return Element.WIND;
        }
        if (this.WA > 0) {
            return Element.EARTH;
        }
        if (this.WB > 0) {
            return Element.HOLY;
        }
        if (this.WC > 0) {
            return Element.UNHOLY;
        }
        return Element.NONE;
    }

    public int getValue() {
        if (this.Wx > 0) {
            return this.Wx;
        }
        if (this.Wy > 0) {
            return this.Wy;
        }
        if (this.Wz > 0) {
            return this.Wz;
        }
        if (this.WA > 0) {
            return this.WA;
        }
        if (this.WB > 0) {
            return this.WB;
        }
        if (this.WC > 0) {
            return this.WC;
        }
        return 0;
    }

    public void setValue(Element element, int n) {
        switch (element) {
            case FIRE: {
                this.Wx = n;
                break;
            }
            case WATER: {
                this.Wy = n;
                break;
            }
            case WIND: {
                this.Wz = n;
                break;
            }
            case EARTH: {
                this.WA = n;
                break;
            }
            case HOLY: {
                this.WB = n;
                break;
            }
            case UNHOLY: {
                this.WC = n;
            }
        }
    }

    public int getValue(Element element) {
        switch (element) {
            case FIRE: {
                return this.Wx;
            }
            case WATER: {
                return this.Wy;
            }
            case WIND: {
                return this.Wz;
            }
            case EARTH: {
                return this.WA;
            }
            case HOLY: {
                return this.WB;
            }
            case UNHOLY: {
                return this.WC;
            }
        }
        return 0;
    }

    public ItemAttributes clone() {
        return new ItemAttributes(this.Wx, this.Wy, this.Wz, this.WA, this.WB, this.WC);
    }
}

