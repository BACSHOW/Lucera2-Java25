package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.fermata.FermataCapabilities;

/**
 * Handshake inicial do protocolo Fermata: o cliente envia a revisao do
 * protocolo que entende e a mascara de capabilities QoL que implementou.
 *
 * O servidor persiste ambas no {@code GameClient} para que os pacotes
 * S2C da Phase 2 (Healing, MonsterDebuffSnapshot, ArmorSetAura) sejam
 * gateados por capacidade.
 */
public class RequestFermataClientCapabilities extends L2GameClientPacket {
    private static final org.slf4j.Logger _log = org.slf4j.LoggerFactory.getLogger(RequestFermataClientCapabilities.class);

    private int _protocolRevision;
    private int _capabilities;

    @Override
    protected void readImpl() {
        if (this._buf.remaining() >= 8) {
            this._protocolRevision = this.readD();
            this._capabilities = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        l2.gameserver.network.l2.GameClient client = (l2.gameserver.network.l2.GameClient) this.getClient();
        if (client == null) {
            return;
        }
        int normalized = this._capabilities & FermataCapabilities.allKnown();
        client.setFermataCapabilities(normalized);
        if (_log.isDebugEnabled()) {
            _log.debug("Fermata handshake: proto={} caps=0x{}", this._protocolRevision, Integer.toHexString(normalized));
        }
    }
}