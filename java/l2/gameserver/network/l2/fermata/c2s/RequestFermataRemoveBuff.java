package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

/**
 * C2S Fermata (subopcode 0x0001) - o jogador solicita a remocao manual de
 * um buff ativo no proprio personagem, enviando o {@code skill_id} e o
 * {@code skill_level}. O servidor apenas cancela o efeito se o cliente
 * declarou
 * {@link l2.gameserver.network.l2.fermata.FermataCapabilities#BIT_BUFF_REMOVE_SELF}
 * e se o efeito esta realmente ativo.
 *
 * Layout do payload (little-endian):
 *   readD - skillId
 *   readH - skillLevel (nao usado para remocao mas mantido para simetria)
 */
public class RequestFermataRemoveBuff extends L2GameClientPacket {

    public static final int SUBOPCODE = 0x0001;

    private int _skillId;
    @SuppressWarnings("unused")
    private int _skillLevel;

    @Override
    protected void readImpl() {
        if (this._buf.remaining() >= 6) {
            this._skillId = this.readD();
            this._skillLevel = this.readH();
        }
    }

    @Override
    protected void runImpl() {
        Player activeChar = this.getClient() == null ? null : ((l2.gameserver.network.l2.GameClient) this.getClient()).getActiveChar();
        if (activeChar == null) {
            return;
        }
        int caps = ((l2.gameserver.network.l2.GameClient) this.getClient()).getFermataCapabilities();
        if ((caps & l2.gameserver.network.l2.fermata.FermataCapabilities.BIT_BUFF_REMOVE_SELF) == 0) {
            return;
        }
        if (activeChar.getEffectList() == null) {
            return;
        }
        activeChar.getEffectList().stopEffect(this._skillId);
    }
}