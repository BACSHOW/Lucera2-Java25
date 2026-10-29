/*
 * L2JLucera2 — Fermata RequestFermataClientCapabilities C2S handler.
 *
 * SubOpcode 0x0000 inside the 0xFD envelope. Body: LE int revision + LE int
 * client capability mask. The server validates, applies the negotiated mask
 * to the per-client context, and replies with a ServerCapabilities envelope.
 */
package l2.gameserver.network.l2.fermata.c2s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.network.l2.fermata.FermataCapabilities;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.FermataContext;
import l2.gameserver.network.l2.fermata.s2c.ServerCapabilities;

/**
 * Fermata client capability handshake.
 *
 * <p>Layout consumed from the buffer:
 * <pre>
 *   int32 revision;
 *   int32 clientMask;
 * </pre>
 */
public final class RequestFermataClientCapabilities extends FermataClientPacket {

    private static final Logger _log = LoggerFactory.getLogger(RequestFermataClientCapabilities.class);

    private int _revision;
    private int _clientMask;

    @Override
    protected void readImpl() {
        // Defensive: 0xFD + 2-byte subOpcode have already been consumed by the
        // dispatcher, so we expect exactly 8 bytes (int32 revision + int32 mask).
        if (this.remaining() < 8) {
            _log.debug("Fermata handshake truncated: {} bytes remaining", this.remaining());
            return;
        }
        this._revision = this.readD();
        this._clientMask = this.readD();
    }

    @Override
    protected void runImpl() {
        FermataContext ctx = this.context();
        if (ctx == null) {
            _log.warn("Fermata handshake arrived without a context holder");
            return;
        }

        if (this._revision < FermataCapabilities.MIN_REVISION
                || this._revision > FermataCapabilities.MAX_REVISION) {
            _log.info("Fermata handshake rejected: revision {} out of range [{}..{}]",
                    this._revision,
                    FermataCapabilities.MIN_REVISION,
                    FermataCapabilities.MAX_REVISION);
            ctx.reset();
            return;
        }
        if (!FermataCapabilities.isClientMaskValid(this._clientMask)) {
            _log.info("Fermata handshake rejected: invalid client mask 0x{}", Integer.toHexString(this._clientMask));
            ctx.reset();
            return;
        }

        int serverMask = FermataCapabilities.allKnownBits();
        int negotiated = FermataCapabilities.negotiate(this._clientMask, serverMask);
        ctx.applyHandshake(this._revision, this._clientMask, serverMask, negotiated);

        this.sendPacket(new ServerCapabilities(FermataCapabilities.SUPPORTED_REVISION, serverMask));

        if (_log.isDebugEnabled()) {
            _log.debug("Fermata handshake complete: revision={} clientMask=0x{} negotiated=0x{}",
                    this._revision,
                    Integer.toHexString(this._clientMask),
                    Integer.toHexString(negotiated));
        }
    }
}