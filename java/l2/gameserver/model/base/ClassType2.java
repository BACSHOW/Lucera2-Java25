/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

public enum ClassType2 {
    None(10280, 10612),
    Warrior(10281, 10289),
    Knight(10282, 10288),
    Rogue(10283, 10290),
    Healer(10285, 10291),
    Enchanter(10287, 10293),
    Summoner(10286, 10294),
    Wizard(10284, 10292);

    public static final ClassType2[] VALUES;
    private final int Oj;
    private final int Ol;

    private ClassType2(int n2, int n3) {
        this.Oj = n2;
        this.Ol = n3;
    }

    public int getCertificateId() {
        return this.Oj;
    }

    public int getTransformationId() {
        return this.Ol;
    }

    static {
        VALUES = ClassType2.values();
    }
}

