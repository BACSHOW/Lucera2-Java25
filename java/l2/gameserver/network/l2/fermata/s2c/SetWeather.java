/*
 * L2JLucera2 — Fermata SetWeather envelope.
 *
 * Multiplexed under 0xFD; broadcasts a weather state update to a single
 * client. Only sent when the client has negotiated the linked-item/weather
 * capability bit (or always when no capability is required - that's the
 * trade-off a subclass can override).
 */
package l2.gameserver.network.l2.fermata.s2c;

import l2.gameserver.network.l2.fermata.FermataCapabilities;

/**
 * Sets the in-world weather state on the client.
 *
 * <p>Wire layout: 0xFD + LE short 0x0001 + LE int weatherId + LE int intensity.
 */
public final class SetWeather extends ExFermataEnvelope {

    private final int _weatherId;
    private final int _intensity;

    public SetWeather(int weatherId, int intensity) {
        super(SubOpcodes.SET_WEATHER);
        this._weatherId = weatherId;
        this._intensity = intensity;
    }

    @Override
    public int requiredCapability() {
        // Weather is treated as a free extension - any negotiated client
        // accepts it. Custom sub-classes can override for stricter gating.
        return 0;
    }

    @Override
    protected void writePayload() {
        this.writeD(this._weatherId);
        this.writeD(this._intensity);
    }

    /** Used by tests/diagnostics; reflects the pre-mask value the client receives. */
    public int weatherId() {
        return this._weatherId;
    }

    /** Used by tests/diagnostics. */
    public int intensity() {
        return this._intensity;
    }

    /**
     * Returns the bitmask used to gate this envelope. Exposed so the broadcaster
     * can be unit-tested without instantiating the full registry chain.
     */
    public static int gateBit() {
        return FermataCapabilities.EMPTY_MASK;
    }
}