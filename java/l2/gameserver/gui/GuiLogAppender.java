package l2.gameserver.gui;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recebe as linhas do log (logback) e do System.err para a aba Console da janela.
 * Nao bloqueia quem esta logando: so enfileira; a janela le a fila a cada 200 ms.
 */
public final class GuiLogAppender extends AppenderBase<ILoggingEvent> {
    /** Limite da fila enquanto a janela nao consome (ex.: boot antes da janela aparecer). */
    private static final int MAX_PENDING = 20000;

    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERROR = 2;

    /** Uma linha pronta para exibir. */
    public static final class Line {
        public final int level;
        public final String text;

        Line(int level, String text) {
            this.level = level;
            this.text = text;
        }
    }

    private static final ConcurrentLinkedQueue<Line> QUEUE = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger SIZE = new AtomicInteger();
    private final ThreadLocal<SimpleDateFormat> time = ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss"));

    @Override
    protected void append(ILoggingEvent event) {
        int level = event.getLevel().isGreaterOrEqual(Level.ERROR) ? ERROR
                : event.getLevel().isGreaterOrEqual(Level.WARN) ? WARN : INFO;
        StringBuilder sb = new StringBuilder(128);
        sb.append('[').append(this.time.get().format(new Date(event.getTimeStamp()))).append("] ");
        sb.append(event.getFormattedMessage());
        IThrowableProxy tp = event.getThrowableProxy();
        while (tp != null) {
            sb.append('\n').append(tp.getClassName()).append(": ").append(tp.getMessage());
            StackTraceElementProxy[] st = tp.getStackTraceElementProxyArray();
            int max = Math.min(st == null ? 0 : st.length, 25);
            for (int i = 0; i < max; i++) {
                sb.append("\n    at ").append(st[i].getStackTraceElement());
            }
            tp = tp.getCause();
            if (tp != null) {
                sb.append("\nCaused by:");
            }
        }
        offer(level, sb.toString());
    }

    public static void offer(int level, String text) {
        if (text == null) {
            return;
        }
        QUEUE.offer(new Line(level, text));
        if (SIZE.incrementAndGet() > MAX_PENDING) {
            if (QUEUE.poll() != null) {
                SIZE.decrementAndGet();
            }
        }
    }

    /** Retira da fila ate {@code max} linhas. */
    public static java.util.List<Line> drain(int max) {
        java.util.List<Line> out = new java.util.ArrayList<>(Math.min(max, 512));
        Line l;
        while (out.size() < max && (l = QUEUE.poll()) != null) {
            SIZE.decrementAndGet();
            out.add(l);
        }
        return out;
    }
}
