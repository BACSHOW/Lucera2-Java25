package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.fermata.FermataSocialCoordination;
import l2.gameserver.network.l2.fermata.s2c.PartyPing;

/**
 * Sub-opcode 0x0010 do protocolo Fermata 0xFD: ping de party, seja em um alvo
 * (objectId) ou em coordenadas absolutas no chão.
 *
 * <p>Layout C2S:</p>
 * <pre>
 *   byte  pingKind      (0 = alvo, 1 = coordenadas)
 *   int   targetObjectId (se pingKind == 0)
 *   int   x              (se pingKind == 1)
 *   int   y
 *   int   z
 *   short durationMs    (tempo que o cliente deve mostrar o ping)
 *   byte  color         (0..7; mapeado no cliente)
 * </pre>
 *
 * <p>Broadcast é restrito a membros da party do jogador, e rate-limitado a 6 pings / 3s.</p>
 */
public class RequestFermataPartyPing extends L2GameClientPacket {

    public static final int SUB_OPCODE = 0x0010;
    public static final byte PING_KIND_TARGET = 0;
    public static final byte PING_KIND_COORDS = 1;

    private byte _pingKind;
    private int _targetObjectId;
    private int _x;
    private int _y;
    private int _z;
    private int _durationMs;
    private byte _color;

    @Override
    protected void readImpl() {
        _pingKind = (byte) readC();
        _targetObjectId = readD();
        _x = readD();
        _y = readD();
        _z = readD();
        _durationMs = readH();
        _color = (byte) readC();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (!FermataSocialCoordination.allowPartyPing(activeChar)) {
            return;
        }

        Party party = activeChar.getParty();
        if (party == null) {
            // Sem party: o ping seria inútil — descartar.
            return;
        }

        int duration = Math.max(500, Math.min(30_000, _durationMs));
        int color = _color & 0x07;

        PartyPing ping = new PartyPing(
            activeChar.getObjectId(),
            _pingKind == PING_KIND_TARGET,
            _targetObjectId,
            _x,
            _y,
            _z,
            duration,
            color
        );
        party.broadcastToPartyMembers(activeChar, ping);
    }

    protected Player getActiveChar() {
        GameClient client = getClient();
        return client == null ? null : client.getActiveChar();
    }
}