/*
 * L2JLucera2 — Per-client Fermata session context.
 *
 * Attached to a GameClient after the 0xFD handshake; carries the negotiated
 * revision, capability mask, and module flags. Always read-only after the
 * handshake completes; if you need to reset, call {@link #reset()} on state
 * transitions (e.g. IN_GAME re-entry).
 */
package l2.gameserver.network.l2.fermata;

import l2.gameserver.Config;

/**
 * Holds the negotiated Fermata state for a single client session.
 *
 * <p>The {@code _active} flag gates every Fermata-aware send path; calling
 * {@link #reset()} clears the state but keeps the holder reference alive.
 */
public final class FermataContext {

    private volatile boolean _active;
    private volatile int _revision;
    private volatile int _clientMask;
    private volatile int _serverMask;
    private volatile int _negotiatedMask;
    private volatile boolean _wideEconomy;
    private volatile long _handshakeNanos;

    public FermataContext() {
        this._revision = 0;
        this._clientMask = FermataCapabilities.EMPTY_MASK;
        this._serverMask = FermataCapabilities.EMPTY_MASK;
        this._negotiatedMask = FermataCapabilities.EMPTY_MASK;
        this._wideEconomy = false;
        this._handshakeNanos = 0L;
        this._active = false;
    }

    /**
     * Records the negotiated handshake. Idempotent: re-applying the same
     * parameters is a no-op.
     *
     * @param revision     client revision (must be in [MIN_REVISION, MAX_REVISION])
     * @param clientMask   raw capability mask sent by the client
     * @param serverMask   raw capability mask supported by the server
     * @param negotiatedMask bits both sides agreed on
     */
    public void applyHandshake(int revision, int clientMask, int serverMask, int negotiatedMask) {
        this._revision = revision;
        this._clientMask = clientMask;
        this._serverMask = serverMask;
        this._negotiatedMask = negotiatedMask;
        this._wideEconomy = (negotiatedMask & FermataCapabilities.BIT_WIDE_ECONOMY) != 0;
        this._handshakeNanos = System.nanoTime();
        this._active = true;
    }

    /** Clears the context, leaving only the holder reference alive. */
    public void reset() {
        this._revision = 0;
        this._clientMask = FermataCapabilities.EMPTY_MASK;
        this._serverMask = FermataCapabilities.EMPTY_MASK;
        this._negotiatedMask = FermataCapabilities.EMPTY_MASK;
        this._wideEconomy = false;
        this._handshakeNanos = 0L;
        this._active = false;
    }

    /** Whether the handshake has completed. */
    public boolean isActive() {
        return this._active;
    }

    /** Negotiated revision (server-supported subset of the client's revision). */
    public int getRevision() {
        return this._revision;
    }

    /** Raw capability mask sent by the client (pre-negotiation). */
    public int getClientMask() {
        return this._clientMask;
    }

    /** Server-side capability mask advertised to the client. */
    public int getServerMask() {
        return this._serverMask;
    }

    /** Intersection of client and server capability masks. */
    public int getNegotiatedMask() {
        return this._negotiatedMask;
    }

    /** Convenience: agreed-upon wide private store economy block. */
    public boolean isWideEconomy() {
        return this._wideEconomy;
    }

    /**
     * Returns {@code true} when {@link #isActive()} is true AND
     * {@link Config#ENABLE_FERMATA_EXTENSIONS} is enabled at runtime.
     * Callers should use this guard before sending any Fermata envelope.
     */
    public boolean isBroadcastEligible() {
        return this._active && Config.ENABLE_FERMATA_EXTENSIONS;
    }

    /** True when {@code bit} is set in the negotiated mask. */
    public boolean has(int bit) {
        return (this._negotiatedMask & bit) == bit;
    }

    public boolean hasNegotiatedCapability(int bit) {
        return has(bit);
    }

    /** When the handshake completed (System.nanoTime), 0 if never. */
    public long getHandshakeNanos() {
        return this._handshakeNanos;
    }
}