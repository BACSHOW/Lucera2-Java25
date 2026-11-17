package l2.gameserver.gui;

import java.awt.Color;
import java.util.function.ToDoubleFunction;
import l2.gameserver.metrics.ServerMetrics.Snapshot;

/** Grafico da janela do GameServer: o TimeSeriesChart aplicado as amostras do ServerMetrics. */
final class MetricChart extends TimeSeriesChart<Snapshot> {
    private static final long serialVersionUID = 1L;

    MetricChart(String title, String unit, double minMax) {
        super(title, unit, minMax, s -> s.timeMillis);
    }

    @Override
    public MetricChart add(String name, Color color, ToDoubleFunction<Snapshot> value) {
        super.add(name, color, value);
        return this;
    }
}
