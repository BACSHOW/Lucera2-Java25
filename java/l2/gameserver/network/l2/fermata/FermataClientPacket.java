/*
 * L2JLucera2 — Base class for Fermata C2S packets.
 *
 * Multiplexed under opcode 0xFD, every Fermata inbound packet shares a
 * two-byte subOpcode. This base class assumes the dispatcher has already
 * consumed those two bytes; subclasses read the rest from {@link #getFermataBuf}.
 *
 * The {@link FermataContext} is exposed via {@link #context()} so handlers
 * can branch on negotiated capability bits without re-reading them.
 */
package l2.gameserver.network.l2.fermata;

import java.nio.ByteBuffer;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;

/**
 * Base class for Fermata C2S packets (opcode 0xFD + subOpcode).
 *
 * <p>Subclasses must NOT call {@code readC()} on the parent class to re-read
 * the subOpcode; the dispatcher already consumed it. Use {@link #readD()},
 * {@link #readH()}, etc. directly to consume the payload.
 */
public abstract class FermataClientPacket extends L2GameClientPacket {

    private FermataContext _fermata;

    /** Returns the per-client Fermata context (never {@code null}). */
    public final FermataContext context() {
        return this._fermata;
    }

    /**
     * Wires the {@link FermataContext} carried by the active {@code GameClient}.
     * Called by the packet handler before {@link #readImpl()}.
     */
    public final void attachContext(FermataContext context) {
        this._fermata = context;
    }

    /** Exposes the underlying ByteBuffer (LE) to subclasses without re-imports. */
    protected final ByteBuffer getFermataBuf() {
        return this._buf;
    }

    /**
     * Returns the number of payload bytes remaining (after the 0xFD + subOpcode
     * have already been consumed by the dispatcher).
     */
    protected final int remaining() {
        return this._buf != null ? this._buf.remaining() : 0;
    }
}