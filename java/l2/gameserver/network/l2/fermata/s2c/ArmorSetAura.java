package l2.gameserver.network.l2.fermata.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * S2C Fermata (subopcode 0x000C) - aura visual emitida quando o jogador
 * equipa um set de armaduras em tier de enchant elevado. O tier e derivado
 * do menor enchant entre as pecas equipadas:
 *   +6  ou maior -> tier 1
 *   +10 ou maior -> tier 2
 *   +16 ou maior -> tier 3
 *
 * Enviado em unicast para o proprio jogador (cuja decisao estetica de aura)
 * sempre que ele tiver
 * {@link l2.gameserver.network.l2.fermata.FermataCapabilities#BIT_ARMOR_SET_AURA}.
 *
 * Layout do payload (little-endian):
 *   writeC - 0xFD (opcode estendido Fermata)
 *   writeH - subopcode (0x000C)
 *   writeD - objectId do jogador
 *   writeD - tier (1, 2 ou 3; 0 desliga a aura)
 */
public final class ArmorSetAura extends L2GameServerPacket {

    public static final int FERMATA_OPCODE = 0xFD;
    public static final int SUBOPCODE = 0x000C;

    public static final int TIER_NONE = 0;
    public static final int TIER_BASIC = 1;
    public static final int TIER_ADVANCED = 2;
    public static final int TIER_MYTHIC = 3;

    private final int _objectId;
    private final int _tier;

    public ArmorSetAura(int objectId, int tier) {
        this._objectId = objectId;
        this._tier = clampTier(tier);
    }

    /**
     * Converte o menor enchant do set em um tier de aura.
     */
    public static int tierFromEnchant(int minEnchant) {
        if (minEnchant >= 16) {
            return TIER_MYTHIC;
        }
        if (minEnchant >= 10) {
            return TIER_ADVANCED;
        }
        if (minEnchant >= 6) {
            return TIER_BASIC;
        }
        return TIER_NONE;
    }

    private static int clampTier(int tier) {
        if (tier < TIER_NONE) {
            return TIER_NONE;
        }
        if (tier > TIER_MYTHIC) {
            return TIER_MYTHIC;
        }
        return tier;
    }

    @Override
    protected void writeImpl() {
        this.writeC(FERMATA_OPCODE);
        this.writeH(SUBOPCODE);
        this.writeD(this._objectId);
        this.writeD(this._tier);
    }
}