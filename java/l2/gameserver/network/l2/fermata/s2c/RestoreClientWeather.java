/*
 * L2JLucera2 — Fermata RestoreClientWeather envelope.
 *
 * Resets the client's weather state to the server's authoritative copy.
 * Used when reconnecting mid-zone or after a teleport.
 */
package l2.gameserver.network.l2.fermata.s2c;

/**
 * Tells the client to restore its weather state from the server.
 *
 * <p>Wire layout: 0xFD + LE short 0x0002 + LE int zoneId (0 = world default).
 */
public final class RestoreClientWeather extends ExFermataEnvelope {

    private final int _zoneId;

    public RestoreClientWeather(int zoneId) {
        super(SubOpcodes.RESTORE_CLIENT_WEATHER);
        this._zoneId = zoneId;
    }

    public RestoreClientWeather() {
        this(0);
    }

    @Override
    protected void writePayload() {
        this.writeD(this._zoneId);
    }

    public int zoneId() {
        return this._zoneId;
    }
}