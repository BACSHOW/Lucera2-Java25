/*
 * L2JLucera2 — Fermata ServerCapabilities envelope.
 *
 * Sent in response to {@code RequestFermataClientCapabilities}. The payload
 * is the server revision + the server-side capability mask (NEVER the
 * negotiated intersection; that lives client-side and the server does not
 * need to echo it).
 */
package l2.gameserver.network.l2.fermata.s2c;

import l2.gameserver.network.l2.fermata.FermataCapabilities;

/**
 * S2C envelope broadcasting the server's Fermata revision + capability mask.
 *
 * <p>Wire layout: 0xFD + LE short 0x0013 + LE int revision + LE int mask.
 */
public final class ServerCapabilities extends ExFermataEnvelope {

    private final int _revision;
    private final int _serverMask;

    public ServerCapabilities(int revision, int serverMask) {
        super(SubOpcodes.SERVER_CAPABILITIES);
        this._revision = revision;
        this._serverMask = serverMask & ~FermataCapabilities.MASK_RESERVED_HIGH;
    }

    @Override
    public int requiredCapability() {
        return 0; // always sent during handshake
    }

    @Override
    protected void writePayload() {
        this.writeD(this._revision);
        this.writeD(this._serverMask);
    }

    /** Exposed for diagnostics/tests. */
    public int revision() {
        return this._revision;
    }

    /** Exposed for diagnostics/tests. */
    public int serverMask() {
        return this._serverMask;
    }
}