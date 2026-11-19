package l2.gameserver.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.Timer;

/** Luz de status: verde (ligado, pulsa devagar), amarela (iniciando, pisca) ou vermelha (desligado). */
public final class StatusLed extends JComponent {
    private static final long serialVersionUID = 1L;

    public enum State {
        ON(Theme.OK),
        STARTING(Theme.WARN),
        OFF(Theme.ERROR);

        final Color color;

        State(Color color) {
            this.color = color;
        }
    }

    private State state = State.OFF;
    private float phase;

    public StatusLed() {
        this.setPreferredSize(new Dimension(18, 18));
        this.setOpaque(false);
        new Timer(60, e -> {
            this.phase += 0.06f;
            if (this.phase > 1f) {
                this.phase -= 1f;
            }
            if (this.isShowing()) {
                this.repaint();
            }
        }).start();
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            this.repaint();
        }
    }

    public State getState() {
        return this.state;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = Math.min(this.getWidth(), this.getHeight());
        float cx = this.getWidth() / 2f;
        float cy = this.getHeight() / 2f;
        double wave = 0.5 + 0.5 * Math.sin(this.phase * 2 * Math.PI);
        // brilho: verde respira devagar, amarelo pisca, vermelho fixo
        float glow = switch (this.state) {
            case ON -> (float) (0.25 + 0.35 * wave);
            case STARTING -> wave > 0.5 ? 0.6f : 0.1f;
            case OFF -> 0.15f;
        };
        Color c = this.state.color;
        float r = size / 2f;
        g.setPaint(new RadialGradientPaint(cx, cy, r, new float[]{0f, 1f},
                new Color[]{new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (glow * 255)), new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)}));
        g.fillOval((int) (cx - r), (int) (cy - r), size, size);
        float core = size * 0.32f;
        Color fill = this.state == State.STARTING && wave <= 0.5 ? c.darker() : c;
        g.setColor(fill);
        g.fillOval((int) (cx - core), (int) (cy - core), (int) (core * 2), (int) (core * 2));
        g.setColor(new Color(255, 255, 255, 90));
        g.fillOval((int) (cx - core * 0.5f), (int) (cy - core * 0.7f), (int) core, (int) (core * 0.7f));
        g.dispose();
    }
}