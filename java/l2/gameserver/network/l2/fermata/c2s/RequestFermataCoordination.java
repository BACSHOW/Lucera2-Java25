package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.fermata.FermataSocialCoordination;
import l2.gameserver.network.l2.fermata.s2c.CoordinationResult;
import l2.gameserver.network.l2.fermata.s2c.CoordinationSnapshot;

/**
 * Sub-opcode 0x000F do protocolo Fermata 0xFD: marcadores táticos e beacons de
 * coordenação de grupo. Cada chamada traz um vetor de até 8 beacons (x,y,z + tipo).
 *
 * <p>Layout C2S:</p>
 * <pre>
 *   short  beaconCount (0..8)
 *   para cada beacon:
 *      byte  kind       (0 = rally point, 1 = aoe, 2 = danger, 3 = loot, 4 = target)
 *      int   x, y, z
 *      byte  color      (0..7)
 *      short lifetimeMs (tempo de exibição do beacon)
 *   String label        (opcional, exibido no HUD)
 * </pre>
 *
 * <p>Resposta S2C:</p>
 * <ul>
 *   <li>{@link CoordinationSnapshot} (0x0014) — sincroniza o estado atual dos beacons
 *       do grupo para todos os membros (incluindo o remetente), garantindo consistência.</li>
 *   <li>{@link CoordinationResult} (0x0015) — eco de confirmação para o remetente, com o
 *       id de correlação recebido para diagnóstico.</li>
 * </ul>
 *
 * <p>O remetente precisa estar em uma party; sem isso o pacote é descartado.</p>
 */
public class RequestFermataCoordination extends L2GameClientPacket {

    public static final int SUB_OPCODE = 0x000F;
    public static final int MAX_BEACONS = 8;

    public static final byte KIND_RALLY = 0;
    public static final byte KIND_AOE = 1;
    public static final byte KIND_DANGER = 2;
    public static final byte KIND_LOOT = 3;
    public static final byte KIND_TARGET = 4;

    private int _correlationId;
    private int _beaconCount;
    private final byte[] _kinds = new byte[MAX_BEACONS];
    private final int[] _xs = new int[MAX_BEACONS];
    private final int[] _ys = new int[MAX_BEACONS];
    private final int[] _zs = new int[MAX_BEACONS];
    private final byte[] _colors = new byte[MAX_BEACONS];
    private final int[] _lifetimes = new int[MAX_BEACONS];
    private String _label;

    @Override
    protected void readImpl() {
        _correlationId = readD();
        int count = readH();
        if (count < 0 || count > MAX_BEACONS) {
            count = MAX_BEACONS;
        }
        _beaconCount = count;
        for (int i = 0; i < count; i++) {
            _kinds[i] = (byte) readC();
            _xs[i] = readD();
            _ys[i] = readD();
            _zs[i] = readD();
            _colors[i] = (byte) readC();
            _lifetimes[i] = readH();
        }
        _label = readS();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (!FermataSocialCoordination.allowCoordination(activeChar)) {
            return;
        }

        Party party = activeChar.getParty();
        if (party == null) {
            return;
        }

        // Snapshot com os beacons validados e clippados em limites seguros.
        CoordinationSnapshot snapshot = new CoordinationSnapshot(
            activeChar.getObjectId(),
            _correlationId,
            _beaconCount,
            _kinds,
            _xs,
            _ys,
            _zs,
            _colors,
            _lifetimes,
            _label
        );
        party.broadCast(snapshot);

        // Eco de confirmação para o remetente.
        CoordinationResult result = new CoordinationResult(_correlationId, _beaconCount);
        activeChar.sendPacket(result);
    }

    protected Player getActiveChar() {
        GameClient client = getClient();
        return client == null ? null : client.getActiveChar();
    }
}