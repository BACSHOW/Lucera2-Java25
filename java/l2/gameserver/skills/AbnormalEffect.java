/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills;

import java.util.NoSuchElementException;

public enum AbnormalEffect {
    NULL("null", 0),
    BLEEDING("bleeding", 1),
    POISON("poison", 2),
    REDCIRCLE("redcircle", 4),
    ICE("ice", 8),
    AFFRAID("affraid", 16),
    CONFUSED("confused", 32),
    STUN("stun", 64),
    SLEEP("sleep", 128),
    MUTED("muted", 256),
    ROOT("root", 512),
    HOLD_1("hold1", 1024),
    HOLD_2("hold2", 2048),
    UNKNOWN_13("unk13", 4096),
    BIG_HEAD("bighead", 8192),
    FLAME("flame", 16384),
    UNKNOWN_16("unk16", 32768),
    GROW("grow", 65536),
    FLOATING_ROOT("floatroot", 131072),
    DANCE_STUNNED("dancestun", 262144),
    FIREROOT_STUN("firerootstun", 524288),
    STEALTH("shadow", 0x100000),
    IMPRISIONING_1("imprison1", 0x200000),
    IMPRISIONING_2("imprison2", 0x400000),
    MAGIC_CIRCLE("magiccircle", 0x800000),
    ICE2("ice2", 0x1000000),
    EARTHQUAKE("earthquake", 0x2000000),
    UNKNOWN_27("unk27", 0x4000000),
    INVULNERABLE("invul1", 0x8000000),
    VITALITY("vitality", 0x10000000),
    REAL_TARGET("realtarget", 0x20000000),
    DEATH_MARK("deathmark", 0x40000000),
    SOUL_SHOCK("soulshock", Integer.MIN_VALUE),
    S_INVULNERABLE("invul2", 1, true),
    S_AIR_STUN("redglow", 2, true),
    S_AIR_ROOT("redglow2", 4, true),
    S_BAGUETTE_SWORD("baguettesword", 8, true),
    S_YELLOW_AFFRO("yellowafro", 16, true),
    S_PINK_AFFRO("pinkafro", 32, true),
    S_BLACK_AFFRO("blackafro", 64, true),
    S_UNKNOWN8("sunk8", 128, true),
    S_STIGMA("stigma", 256, true),
    S_UNKNOWN10("sunk10", 512, true),
    FROZEN_PILLAR("frozenpillar", 1024, true),
    S_VESPER1("vesper1", 2048, true),
    S_VESPER2("vesper2", 4096, true),
    S_VESPER3("vesper3", 8192, true),
    S_SOA_RESP("soa_respawn", 16384, true),
    S_ARCANE_SHIELD("arcane_invul", 32768, true),
    S_UNKNOWN17("sunk17", 65536, true),
    S_UNKNOWN18("sunk18", 131072, true),
    S_UNKNOWN19("sunk19", 262144, true),
    S_NAVIT("nevitSystem", 524288, true),
    S_UNKNOWN21("sunk21", 0x100000, true),
    S_LETTER("letter_collection_buff", 0x200000, true),
    S_UNKNOWN23("sunk23", 0x400000, true),
    S_UNKNOWN24("sunk24", 0x800000, true),
    S_UNKNOWN25("sunk25", 0x1000000, true),
    S_UNKNOWN26("sunk26", 0x2000000, true),
    S_UNKNOWN27("sunk27", 0x4000000, true),
    S_UNKNOWN28("sunk28", 0x8000000, true),
    S_UNKNOWN29("sunk29", 0x10000000, true),
    S_UNKNOWN30("sunk30", 0x20000000, true),
    S_UNKNOWN31("sunk31", 0x40000000, true),
    S_UNKNOWN32("sunk32", Integer.MIN_VALUE, true),
    E_AFRO_1("afrobaguette1", 1, false, true),
    E_AFRO_2("afrobaguette2", 2, false, true),
    E_AFRO_3("afrobaguette3", 4, false, true),
    E_EVASWRATH("evaswrath", 8, false, true),
    E_HEADPHONE("headphone", 16, false, true),
    E_VESPER_1("vesper1", 32, false, true),
    E_VESPER_2("vesper2", 64, false, true),
    E_VESPER_3("vesper3", 128, false, true);

    private final int YK;
    private final String _name;
    private final boolean aog;
    private final boolean aoh;

    private AbnormalEffect(String string2, int n2) {
        this._name = string2;
        this.YK = n2;
        this.aog = false;
        this.aoh = false;
    }

    private AbnormalEffect(String string2, int n2, boolean bl) {
        this._name = string2;
        this.YK = n2;
        this.aog = bl;
        this.aoh = false;
    }

    private AbnormalEffect(String string2, int n2, boolean bl, boolean bl2) {
        this._name = string2;
        this.YK = n2;
        this.aog = bl;
        this.aoh = bl2;
    }

    public final int getMask() {
        return this.YK;
    }

    public final String getName() {
        return this._name;
    }

    public final boolean isSpecial() {
        return this.aog;
    }

    public final boolean isEvent() {
        return this.aoh;
    }

    public static AbnormalEffect getByName(String string) {
        for (AbnormalEffect abnormalEffect : AbnormalEffect.values()) {
            if (!abnormalEffect.getName().equals(string)) continue;
            return abnormalEffect;
        }
        throw new NoSuchElementException("AbnormalEffect not found for name: '" + string + "'.\n Please check " + AbnormalEffect.class.getCanonicalName());
    }
}

