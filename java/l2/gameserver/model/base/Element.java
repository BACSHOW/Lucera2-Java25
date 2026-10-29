/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import java.util.Arrays;
import l2.gameserver.stats.Stats;

public enum Element {
    FIRE(0, Stats.ATTACK_FIRE, Stats.DEFENCE_FIRE),
    WATER(1, Stats.ATTACK_WATER, Stats.DEFENCE_WATER),
    WIND(2, Stats.ATTACK_WIND, Stats.DEFENCE_WIND),
    EARTH(3, Stats.ATTACK_EARTH, Stats.DEFENCE_EARTH),
    HOLY(4, Stats.ATTACK_HOLY, Stats.DEFENCE_HOLY),
    UNHOLY(5, Stats.ATTACK_UNHOLY, Stats.DEFENCE_UNHOLY),
    NONE(-2, null, null);

    public static final Element[] VALUES;
    private final byte On;
    private final Stats Oo;
    private final Stats Op;

    private Element(int n2, Stats stats, Stats stats2) {
        this.On = (byte)n2;
        this.Oo = stats;
        this.Op = stats2;
    }

    public byte getId() {
        return this.On;
    }

    public Stats getAttack() {
        return this.Oo;
    }

    public Stats getDefence() {
        return this.Op;
    }

    public static Element getElementById(int n) {
        for (Element element : VALUES) {
            if (element.getId() != n) continue;
            return element;
        }
        return NONE;
    }

    public static Element getReverseElement(Element element) {
        switch (element) {
            case WATER: {
                return FIRE;
            }
            case FIRE: {
                return WATER;
            }
            case WIND: {
                return EARTH;
            }
            case EARTH: {
                return WIND;
            }
            case HOLY: {
                return UNHOLY;
            }
            case UNHOLY: {
                return HOLY;
            }
        }
        return NONE;
    }

    public static Element getElementByName(String string) {
        for (Element element : VALUES) {
            if (!element.name().equalsIgnoreCase(string)) continue;
            return element;
        }
        return NONE;
    }

    static {
        VALUES = Arrays.copyOf(Element.values(), 6);
    }
}

