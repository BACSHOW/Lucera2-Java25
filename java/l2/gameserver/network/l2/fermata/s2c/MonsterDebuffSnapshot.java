package l2.gameserver.network.l2.fermata.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * S2C Fermata (subopcode 0x0016) - lista compacta dos debuffs atualmente
 * ativos em um monstro (ou qualquer alvo NPC). Enviado aos viewers com
 * {@link l2.gameserver.network.l2.fermata.FermataCapabilities#BIT_MONSTER_DEBUFF_HUD}
 * sempre que a composicao de efeitos negativos muda no alvo.
 *
 * Layout do payload (little-endian):
 *   writeC - 0xFD (opcode estendido Fermata)
 *   writeH - subopcode (0x0016)
 *   writeD - targetId
 *   writeD - count
 *   Para cada debuff:
 *     writeD - skillId
 *     writeH - skillLevel
 *     writeD - remainingSeconds
 */
public final class MonsterDebuffSnapshot extends L2GameServerPacket {

    public static final int FERMATA_OPCODE = 0xFD;
    public static final int SUBOPCODE = 0x0016;

    private final int _targetId;
    private final int[] _skillIds;
    private final int[] _skillLevels;
    private final int[] _remainingSeconds;

    public MonsterDebuffSnapshot(int targetId, int[] skillIds, int[] skillLevels, int[] remainingSeconds) {
        this._targetId = targetId;
        this._skillIds = skillIds == null ? new int[0] : skillIds;
        this._skillLevels = skillLevels == null ? new int[0] : skillLevels;
        this._remainingSeconds = remainingSeconds == null ? new int[0] : remainingSeconds;
    }

    @Override
    protected void writeImpl() {
        int count = Math.min(this._skillIds.length, Math.min(this._skillLevels.length, this._remainingSeconds.length));
        if (count < 0) {
            count = 0;
        }
        this.writeC(FERMATA_OPCODE);
        this.writeH(SUBOPCODE);
        this.writeD(this._targetId);
        this.writeD(count);
        for (int i = 0; i < count; i++) {
            this.writeD(this._skillIds[i]);
            this.writeH(this._skillLevels[i] & 0xFFFF);
            this.writeD(this._remainingSeconds[i]);
        }
    }
}
