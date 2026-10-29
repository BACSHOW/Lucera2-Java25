package l2.gameserver.network.l2.fermata;

import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;

/**
 * Snapshot canônico de 32 bytes para um {@link ItemInstance}, utilizado pelos pacotes
 * Fermata Social/Coordination para expor dados públicos de item em chat e marcadores.
 *
 * <p>O snapshot omite propositalmente o {@code objectId} (privado do dono) e qualquer
 * atributo sensível (ex.: localização interna do inventário), compartilhando apenas o
 * estado público do item que o dono legítimo é dono de fato.</p>
 *
 * <h2>Layout binário little-endian (32 bytes)</h2>
 * <pre>
 * offset  size  campo
 *   0      4    itemId           int
 *   4      2    enchant          short
 *   6      2    augmentStat1     short   (VariationStat1)
 *   8      2    augmentStat2     short   (VariationStat2)
 *  10      4    flags            int     (bit 0: equipped, bit 1: blessed, bit 2: damaged,
 *                                         bits 8-11: attackElementId, bits 12-15: defenseElementId,
 *                                         bits 16-19: charge ss, bits 20-23: charge sps)
 *  14      1    defenceFire      byte
 *  15      1    defenceWater     byte
 *  16      1    defenceWind      byte
 *  17      1    defenceEarth     byte
 *  18      1    defenceHoly      byte
 *  19      1    defenceUnholy    byte
 *  20      2    bodyPart         short
 *  22      4    visibleItemId    int     (skin/fake id quando armado)
 *  26      2    count            short
 *  28      2    reserved         short   (alinhamento; sempre 0)
 *  30      2    attackElement    short   (id em low byte, value em high byte)
 * </pre>
 */
public final class FermataItemSnapshot {

    public static final int SNAPSHOT_SIZE = 32;

    private static final int FLAG_EQUIPPED = 1 << 0;
    private static final int FLAG_BLESSED = 1 << 1;
    private static final int FLAG_DAMAGED = 1 << 2;
    private static final int FLAG_AUGMENTED = 1 << 3;

    private final int itemId;
    private final int visibleItemId;
    private final int bodyPart;
    private final int enchant;
    private final int blessed;
    private final int damaged;
    private final int augmentStat1;
    private final int augmentStat2;
    private final int attackElement;
    private final int attackElementValue;
    private final int defenceFire;
    private final int defenceWater;
    private final int defenceWind;
    private final int defenceEarth;
    private final int defenceHoly;
    private final int defenceUnholy;
    private final boolean equipped;
    private final int count;
    private final int chargedSoulshot;
    private final int chargedSpiritshot;

    public FermataItemSnapshot(int itemId, int visibleItemId, int bodyPart, int enchant,
                               int blessed, int damaged, int augmentStat1, int augmentStat2,
                               boolean equipped, int count) {
        this.itemId = itemId;
        this.visibleItemId = visibleItemId;
        this.bodyPart = bodyPart;
        this.enchant = enchant;
        this.blessed = blessed;
        this.damaged = damaged;
        this.augmentStat1 = augmentStat1;
        this.augmentStat2 = augmentStat2;
        this.equipped = equipped;
        this.count = (int) Math.min(count, Short.MAX_VALUE);
        this.attackElement = Element.NONE.getId();
        this.attackElementValue = 0;
        this.defenceFire = 0;
        this.defenceWater = 0;
        this.defenceWind = 0;
        this.defenceEarth = 0;
        this.defenceHoly = 0;
        this.defenceUnholy = 0;
        this.chargedSoulshot = 0;
        this.chargedSpiritshot = 0;
    }

    public FermataItemSnapshot(ItemInstance item) {
        if (item == null) {
            throw new IllegalArgumentException("item must not be null");
        }
        this.itemId = item.getItemId();
        this.visibleItemId = item.getVisibleItemId();
        this.bodyPart = item.getTemplate().getBodyPart();
        this.enchant = item.getEnchantLevel();
        this.blessed = item.getBlessed();
        this.damaged = item.getDamaged();
        this.augmentStat1 = item.getVariationStat1();
        this.augmentStat2 = item.getVariationStat2();
        this.equipped = item.isEquipped();
        this.count = (int) Math.min(item.getCount(), Short.MAX_VALUE);

        Element atk = item.getAttackElement();
        this.attackElement = atk == null ? Element.NONE.getId() : atk.getId();
        this.attackElementValue = Math.min(255, Math.max(0, item.getAttackElementValue()));

        this.defenceFire = clamp(item.getDefenceFire());
        this.defenceWater = clamp(item.getDefenceWater());
        this.defenceWind = clamp(item.getDefenceWind());
        this.defenceEarth = clamp(item.getDefenceEarth());
        this.defenceHoly = clamp(item.getDefenceHoly());
        this.defenceUnholy = clamp(item.getDefenceUnholy());

        this.chargedSoulshot = Math.min(0xF, Math.max(0, item.getChargedSoulshot()));
        this.chargedSpiritshot = Math.min(0xF, Math.max(0, item.getChargedSpiritshot()));
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(0xFF, v);
    }

    /**
     * Serializa o snapshot em exatamente {@value #SNAPSHOT_SIZE} bytes little-endian em {@code out}.
     *
     * @param out buffer com pelo menos 32 bytes livres a partir da posição atual
     */
    public void writeTo(java.nio.ByteBuffer out) {
        if (out.remaining() < SNAPSHOT_SIZE) {
            throw new java.nio.BufferOverflowException();
        }

        int flags = 0;
        if (this.equipped) flags |= FLAG_EQUIPPED;
        if (this.blessed != 0) flags |= FLAG_BLESSED;
        if (this.damaged != 0) flags |= FLAG_DAMAGED;
        if (this.augmentStat1 != 0 || this.augmentStat2 != 0) flags |= FLAG_AUGMENTED;
        flags |= (this.attackElement & 0xF) << 8;
        flags |= 0 << 12; // bits reservados no protocolo (defenseElement legado fica no payload direto)
        flags |= (this.chargedSoulshot & 0xF) << 16;
        flags |= (this.chargedSpiritshot & 0xF) << 20;

        out.putInt(this.itemId);            // 0..3
        out.putShort((short) this.enchant);  // 4..5
        out.putShort((short) this.augmentStat1); // 6..7
        out.putShort((short) this.augmentStat2); // 8..9
        out.putInt(flags);                   // 10..13
        out.put((byte) this.defenceFire);    // 14
        out.put((byte) this.defenceWater);   // 15
        out.put((byte) this.defenceWind);    // 16
        out.put((byte) this.defenceEarth);   // 17
        out.put((byte) this.defenceHoly);    // 18
        out.put((byte) this.defenceUnholy);  // 19
        out.putShort((short) this.bodyPart); // 20..21
        out.putInt(this.visibleItemId);      // 22..25
        out.putShort((short) this.count);    // 26..27
        out.putShort((short) 0);             // 28..29 reserved
        out.putShort((short) ((this.attackElement & 0xFF) | ((this.attackElementValue & 0xFF) << 8))); // 30..31
    }

    public int getItemId() {
        return itemId;
    }

    public int getEnchant() {
        return enchant;
    }

    public int getAugmentStat1() {
        return augmentStat1;
    }

    public int getAugmentStat2() {
        return augmentStat2;
    }

    public int getBodyPart() {
        return bodyPart;
    }

    public int getCount() {
        return count;
    }

    public int getAttackElement() {
        return attackElement;
    }

    public int getAttackElementValue() {
        return attackElementValue;
    }

    public int getDefenceFire() {
        return defenceFire;
    }

    public int getDefenceWater() {
        return defenceWater;
    }

    public int getDefenceWind() {
        return defenceWind;
    }

    public int getDefenceEarth() {
        return defenceEarth;
    }

    public int getDefenceHoly() {
        return defenceHoly;
    }

    public int getDefenceUnholy() {
        return defenceUnholy;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public boolean isAugmented() {
        return (augmentStat1 | augmentStat2) != 0;
    }

    public int getVisibleItemId() {
        return visibleItemId;
    }
}