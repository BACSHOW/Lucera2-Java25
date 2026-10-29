package l2.gameserver.network.l2.fermata;

public final class FermataCombatQuantizer {

    private static final int SHIFT = 20;
    private static final double SCALE = (double) (1L << SHIFT);

    private FermataCombatQuantizer() {
    }

    public static long quantizeHp(double hp) {
        if (Double.isNaN(hp) || Double.isInfinite(hp)) {
            return 0L;
        }
        return Math.round(hp * SCALE);
    }

    public static double unquantizeHp(long quantized) {
        return (double) quantized / SCALE;
    }
}
