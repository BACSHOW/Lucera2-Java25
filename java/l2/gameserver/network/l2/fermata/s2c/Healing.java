package l2.gameserver.network.l2.fermata.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * S2C Fermata (subopcode 0x0008) - feedback visual de cura recebida por um
 * alvo (HP/MP/CP) em razao de uma skill. Enviado aos viewers com
 * {@link l2.gameserver.network.l2.fermata.FermataCapabilities#BIT_HEALING_FEEDBACK}.
 *
 * Layout do payload (little-endian):
 *   writeC - 0xFD (opcode estendido do protocolo Fermata)
 *   writeH - subopcode (0x0008)
 *   writeD - sourceId
 *   writeD - targetId
 *   writeD - skillId
 *   writeH - skillLevel
 *   writeD - amount
 */
public final class Healing extends L2GameServerPacket {

    public static final int FERMATA_OPCODE = 0xFD;
    public static final int SUBOPCODE = 0x0008;

    private final int _sourceId;
    private final int _targetId;
    private final int _skillId;
    private final int _skillLevel;
    private final int _amount;

    public Healing(int sourceId, int targetId, int skillId, int skillLevel, int amount) {
        this._sourceId = sourceId;
        this._targetId = targetId;
        this._skillId = skillId;
        this._skillLevel = skillLevel & 0xFFFF;
        this._amount = amount;
    }

    @Override
    protected void writeImpl() {
        this.writeC(FERMATA_OPCODE);
        this.writeH(SUBOPCODE);
        this.writeD(this._sourceId);
        this.writeD(this._targetId);
        this.writeD(this._skillId);
        this.writeH(this._skillLevel);
        this.writeD(this._amount);
    }
}
