package l2.gameserver.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import javax.swing.JPanel;
import l2.gameserver.metrics.ServerMetrics.Snapshot;

/** Grafico de linhas simples (sem bibliotecas externas) para a aba Metricas. */
final class MetricChart extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final class Series {
        final String name;
        final Color color;
        final ToDoubleFunction<Snapshot> value;

        Series(String name, Color color, ToDoubleFunction<Snapshot> value) {
            this.name = name;
            this.color = color;
            this.value = value;
        }
    }

    private final String title;
    private final String unit;
    private final double minMax;
    private final List<Series> series = new ArrayList<>();
    private Snapshot[] data = new Snapshot[0];
    private int windowSeconds = 900;

    /**
     * @param minMax menor topo do eixo Y (evita que ruido pequeno pareca um pico).
     */
    MetricChart(String title, String unit, double minMax) {
        this.title = title;
        this.unit = unit;
        this.minMax = minMax;
        this.setBackground(Theme.PANEL);
        this.setPreferredSize(new Dimension(360, 180));
    }

    MetricChart add(String name, Color color, ToDoubleFunction<Snapshot> value) {
        this.series.add(new Series(name, color, value));
        return this;
    }

    void setData(Snapshot[] data, int windowSeconds) {
        this.data = data;
        this.windowSeconds = windowSeconds;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = this.getWidth(), h = this.getHeight();
            g.setColor(Theme.PANEL);
            g.fillRect(0, 0, w, h);

            Font titleFont = this.getFont().deriveFont(Font.BOLD, 12f);
            Font small = this.getFont().deriveFont(10.5f);
            g.setFont(titleFont);
            g.setColor(Theme.TEXT);
            g.drawString(this.title, 10, 18);

            // legenda (direita do titulo)
            g.setFont(small);
            FontMetrics fm = g.getFontMetrics();
            int lx = w - 10;
            for (int i = this.series.size() - 1; i >= 0; i--) {
                Series s = this.series.get(i);
                int tw = fm.stringWidth(s.name);
                lx -= tw;
                g.setColor(Theme.MUTED);
                g.drawString(s.name, lx, 18);
                lx -= 14;
                g.setColor(s.color);
                g.fillRoundRect(lx, 10, 10, 4, 2, 2);
                lx -= 10;
            }

            int left = 46, right = w - 12, top = 30, bottom = h - 22;
            if (right - left < 20 || bottom - top < 20) {
                return;
            }

            double max = this.minMax;
            for (Snapshot s : this.data) {
                for (Series se : this.series) {
                    max = Math.max(max, se.value.applyAsDouble(s));
                }
            }
            double step = niceStep(max / 4.0);
            double top_ = Math.ceil(max / step) * step;

            // grade e eixo Y
            g.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                double v = top_ * i / 4.0;
                int y = bottom - (int) Math.round((bottom - top) * v / top_);
                g.setColor(Theme.GRID);
                g.drawLine(left, y, right, y);
                g.setColor(Theme.MUTED);
                String label = format(v);
                g.drawString(label, left - 6 - fm.stringWidth(label), y + 4);
            }
            g.setColor(Theme.MUTED);
            g.drawString("-" + (this.windowSeconds / 60) + " min", left, h - 6);
            String now = "agora" + (this.unit.isEmpty() ? "" : "  (" + this.unit + ")");
            g.drawString(now, right - fm.stringWidth(now), h - 6);

            if (this.data.length < 2) {
                g.drawString("Coletando dados...", left + 8, top + 18);
                return;
            }
            long newest = this.data[this.data.length - 1].timeMillis;
            long oldest = newest - this.windowSeconds * 1000L;
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (Series se : this.series) {
                Path2D path = new Path2D.Double();
                boolean first = true;
                for (Snapshot s : this.data) {
                    double x = left + (right - left) * (double) (s.timeMillis - oldest) / (newest - oldest);
                    double y = bottom - (bottom - top) * Math.max(0.0, se.value.applyAsDouble(s)) / top_;
                    if (first) {
                        path.moveTo(x, y);
                        first = false;
                    } else {
                        path.lineTo(x, y);
                    }
                }
                g.setColor(se.color);
                g.draw(path);
            }
        } finally {
            g.dispose();
        }
    }

    private static double niceStep(double raw) {
        if (raw <= 0) {
            return 1;
        }
        double exp = Math.pow(10, Math.floor(Math.log10(raw)));
        double f = raw / exp;
        double nice = f <= 1 ? 1 : f <= 2 ? 2 : f <= 2.5 ? 2.5 : f <= 5 ? 5 : 10;
        return nice * exp;
    }

    static String format(double v) {
        if (v >= 1000) {
            return String.format("%,.0f", v).replace(',', '.');
        }
        if (v >= 10 || v == Math.rint(v)) {
            return String.format("%.0f", v);
        }
        return String.format("%.1f", v).replace('.', ',');
    }
}
