package l2.gameserver.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import l2.gameserver.metrics.ServerMetrics;
import l2.gameserver.metrics.ServerMetrics.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Janela propria do GameServer: aba Console (log com busca e filtro), aba Metricas (numeros ao vivo e
 * graficos da ultima hora) e botoes de administracao.
 *
 * <p>Liga com {@code EnableServerGui = True} em config/server.properties (padrao) e so abre quando ha
 * area de trabalho; num servidor sem tela (VPS Linux) o servidor segue apenas no console, como antes.
 * Pode ser forcada a desligar com {@code -Dl2.gui=false}.</p>
 */
public final class ServerGui {
    private static final Logger LOG = LoggerFactory.getLogger(ServerGui.class);
    private static final int MAX_LINES = 8000;
    private static volatile ServerGui instance;

    private final JFrame frame = new JFrame("Lucera2 - GameServer");
    // console
    private final JTextPane console = new JTextPane();
    private final ArrayDeque<GuiLogAppender.Line> lines = new ArrayDeque<>();
    private final JCheckBox autoScroll = new JCheckBox("Rolagem automática", true);
    private final JCheckBox onlyProblems = new JCheckBox("Somente avisos e erros");
    private final JTextField search = new JTextField(18);
    private final SimpleAttributeSet styleInfo = new SimpleAttributeSet();
    private final SimpleAttributeSet styleWarn = new SimpleAttributeSet();
    private final SimpleAttributeSet styleError = new SimpleAttributeSet();
    private int searchFrom;
    // metricas
    private final JLabel vOnline = value(), vCpu = value(), vHeap = value(), vThreads = value(), vIn = value(),
            vOut = value(), vAi = value(), vPath = value(), vGc = value(), vUptime = value();
    private final JLabel sOnline = sub(), sCpu = sub(), sHeap = sub(), sThreads = sub(), sIn = sub(), sOut = sub(),
            sAi = sub(), sPath = sub(), sGc = sub(), sUptime = sub();
    private final JComboBox<String> range = new JComboBox<>(new String[]{"5 min", "15 min", "30 min", "60 min"});
    private final MetricChart chOnline, chHeap, chPackets, chAi, chPath, chCpu;
    // barra de status / acoes
    private final JLabel status = new JLabel(" ");
    private final JCheckBox telemetry = new JCheckBox("Telemetria de movimento");
    private final JCheckBox npcTrace = new JCheckBox("Trace de NPCs");

    private ServerGui() {
        this.chOnline = new MetricChart("Jogadores", "", 5)
                .add("online", Theme.BLUE, s -> s.playersOnline)
                .add("lojas offline", Theme.GRAY, s -> s.offlineTraders);
        this.chHeap = new MetricChart("Memória (heap)", "MB", 256)
                .add("usada", Theme.GREEN, s -> s.heapUsedMb)
                .add("máxima", Theme.GRAY, s -> s.heapMaxMb);
        this.chPackets = new MetricChart("Pacotes por segundo", "pacotes/s", 10)
                .add("recebidos", Theme.BLUE, s -> s.packetsInPerSec)
                .add("enviados", Theme.ORANGE, s -> s.packetsOutPerSec);
        this.chAi = new MetricChart("IA dos NPCs", "ms", 1)
                .add("média", Theme.GREEN, s -> s.aiAvgMs)
                .add("pior", Theme.ORANGE, s -> s.aiMaxMs);
        this.chPath = new MetricChart("Pathfinding", "ms", 1)
                .add("média", Theme.GREEN, s -> s.pathfindAvgMs)
                .add("pior", Theme.ORANGE, s -> s.pathfindMaxMs);
        this.chCpu = new MetricChart("CPU do processo", "%", 10)
                .add("cpu", Theme.PURPLE, s -> Math.max(0.0, s.processCpuPercent));
    }

    // ------------------------------------------------------------------ inicializacao

    /** Chamado no main(), logo depois de configurar o log. */
    public static void startIfEnabled() {
        ServerMetrics.start();
        if (!isEnabled()) {
            return;
        }
        attachLogAppender();
        teeSystemErr();
        try {
            SwingUtilities.invokeAndWait(() -> {
                ServerGui gui = new ServerGui();
                gui.build();
                instance = gui;
            });
        } catch (Throwable t) {
            LOG.warn("Janela do servidor nao pode ser aberta, seguindo so no console: " + t);
        }
    }

    public static boolean isOpen() {
        return instance != null;
    }

    private static boolean isEnabled() {
        if ("false".equalsIgnoreCase(System.getProperty("l2.gui", ""))) {
            return false;
        }
        if (GraphicsEnvironment.isHeadless()) {
            return false;
        }
        Properties p = new Properties();
        File f = new File(l2.gameserver.Config.CONFIGURATION_FILE);
        if (f.exists()) {
            try (InputStream in = new FileInputStream(f)) {
                p.load(in);
            } catch (Exception ignored) {
            }
        }
        return Boolean.parseBoolean(p.getProperty("EnableServerGui", "true").trim());
    }

    private static void attachLogAppender() {
        org.slf4j.ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof ch.qos.logback.classic.LoggerContext context) {
            GuiLogAppender appender = new GuiLogAppender();
            appender.setContext(context);
            appender.setName("GUI");
            appender.start();
            context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).addAppender(appender);
        }
    }

    /** Excecoes impressas direto no System.err (printStackTrace) tambem aparecem na janela. */
    private static void teeSystemErr() {
        final PrintStream original = System.err;
        System.setErr(new PrintStream(original, true) {
            private final StringBuilder buf = new StringBuilder();

            @Override
            public synchronized void write(byte[] b, int off, int len) {
                super.write(b, off, len);
                this.buf.append(new String(b, off, len, StandardCharsets.UTF_8));
                int nl;
                while ((nl = this.buf.indexOf("\n")) >= 0) {
                    String line = this.buf.substring(0, nl).replace("\r", "");
                    this.buf.delete(0, nl + 1);
                    if (!line.isEmpty()) {
                        GuiLogAppender.offer(GuiLogAppender.ERROR, line);
                    }
                }
            }
        });
    }

    // ------------------------------------------------------------------ montagem

    private void build() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        StyleConstants.setForeground(this.styleInfo, Theme.LOG);
        StyleConstants.setForeground(this.styleWarn, Theme.WARN);
        StyleConstants.setForeground(this.styleError, Theme.ERROR);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Console", this.buildConsole());
        tabs.addTab("Métricas", this.buildMetrics());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(this.buildToolbar(), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        this.status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        this.status.setForeground(Theme.MUTED);
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Theme.BG);
        statusBar.add(this.status, BorderLayout.CENTER);
        root.add(statusBar, BorderLayout.SOUTH);

        this.frame.setContentPane(root);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerGui.this.onClose();
            }
        });
        this.frame.setSize(1180, 760);
        this.frame.setMinimumSize(new Dimension(820, 520));
        this.frame.setLocationByPlatform(true);
        this.frame.setVisible(true);

        new Timer(200, e -> this.pumpLog()).start();
        new Timer(1000, e -> this.refreshMetrics()).start();
    }

    private JComponent buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        bar.setBackground(Theme.BG);
        bar.add(button("Recarregar configs", e -> this.reloadConfigs()));
        bar.add(button("Anunciar...", e -> this.announce()));
        bar.add(button("Reiniciar...", e -> this.scheduleShutdown(true)));
        bar.add(button("Desligar...", e -> this.scheduleShutdown(false)));
        bar.add(button("Cancelar desligamento", e -> this.cancelShutdown()));
        bar.add(button("Salvar thread dump", e -> this.threadDump()));
        for (JCheckBox c : new JCheckBox[]{this.telemetry, this.npcTrace}) {
            c.setOpaque(false);
            c.setForeground(Theme.TEXT);
            bar.add(c);
        }
        this.telemetry.setToolTipText("Grava log/movement_debug.txt (mesmo que //track_npc telemetry on)");
        this.npcTrace.setToolTipText("Grava log/npc_movement_debug.txt (mesmo que //track_npc global on)");
        this.telemetry.addActionListener(e -> l2.gameserver.utils.MovementTelemetryLogger.ENABLED = this.telemetry.isSelected());
        this.npcTrace.addActionListener(e -> l2.gameserver.ai.debug.NpcMovementDebugTracer.GLOBAL_TRACE = this.npcTrace.isSelected());
        return bar;
    }

    private JComponent buildConsole() {
        this.console.setEditable(false);
        this.console.setBackground(Theme.BG);
        this.console.setForeground(Theme.LOG);
        this.console.setCaretColor(Theme.TEXT);
        this.console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        this.console.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(this.console);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.BG);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        top.setBackground(Theme.BG);
        JLabel l = new JLabel("Buscar:");
        l.setForeground(Theme.TEXT);
        top.add(l);
        top.add(this.search);
        this.search.addActionListener(e -> this.findNext());
        top.add(button("Próximo", e -> this.findNext()));
        top.add(button("Limpar tela", e -> {
            this.lines.clear();
            this.console.setText("");
        }));
        for (JCheckBox c : new JCheckBox[]{this.autoScroll, this.onlyProblems}) {
            c.setOpaque(false);
            c.setForeground(Theme.TEXT);
            top.add(c);
        }
        this.onlyProblems.addActionListener(e -> this.rerender());

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG);
        p.add(top, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildMetrics() {
        JPanel cards = new JPanel(new GridLayout(2, 5, 8, 8));
        cards.setBackground(Theme.BG);
        cards.add(card("Jogadores online", this.vOnline, this.sOnline));
        cards.add(card("CPU do processo", this.vCpu, this.sCpu));
        cards.add(card("Memória (heap)", this.vHeap, this.sHeap));
        cards.add(card("Threads", this.vThreads, this.sThreads));
        cards.add(card("Tempo ligado", this.vUptime, this.sUptime));
        cards.add(card("Pacotes recebidos/s", this.vIn, this.sIn));
        cards.add(card("Pacotes enviados/s", this.vOut, this.sOut));
        cards.add(card("IA dos NPCs", this.vAi, this.sAi));
        cards.add(card("Pathfinding", this.vPath, this.sPath));
        cards.add(card("Coleta de lixo (GC)", this.vGc, this.sGc));

        JPanel charts = new JPanel(new GridLayout(2, 3, 8, 8));
        charts.setBackground(Theme.BG);
        for (MetricChart c : new MetricChart[]{this.chOnline, this.chHeap, this.chCpu, this.chPackets, this.chAi, this.chPath}) {
            c.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
            charts.add(c);
        }

        JPanel rangeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rangeBar.setBackground(Theme.BG);
        JLabel rl = new JLabel("Período dos gráficos:");
        rl.setForeground(Theme.MUTED);
        rangeBar.add(rl);
        this.range.setSelectedIndex(1);
        this.range.addActionListener(e -> this.refreshMetrics());
        rangeBar.add(this.range);

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setBackground(Theme.BG);
        center.add(rangeBar, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Theme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(cards, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private static JComponent card(String title, JLabel value, JLabel sub) {
        JPanel c = new JPanel(new BorderLayout(0, 2));
        c.setBackground(Theme.PANEL);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JLabel t = new JLabel(title);
        t.setForeground(Theme.MUTED);
        t.setFont(t.getFont().deriveFont(11.5f));
        c.add(t, BorderLayout.NORTH);
        c.add(value, BorderLayout.CENTER);
        c.add(sub, BorderLayout.SOUTH);
        return c;
    }

    private static JLabel value() {
        JLabel l = new JLabel("-");
        l.setForeground(Theme.TEXT);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 20f));
        return l;
    }

    private static JLabel sub() {
        JLabel l = new JLabel(" ");
        l.setForeground(Theme.MUTED);
        l.setFont(l.getFont().deriveFont(11f));
        return l;
    }

    private static JButton button(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.addActionListener(action);
        return b;
    }

    // ------------------------------------------------------------------ console

    private void pumpLog() {
        List<GuiLogAppender.Line> batch = GuiLogAppender.drain(2000);
        if (batch.isEmpty()) {
            return;
        }
        Document doc = this.console.getDocument();
        boolean filter = this.onlyProblems.isSelected();
        for (GuiLogAppender.Line line : batch) {
            this.lines.addLast(line);
            if (!filter || line.level != GuiLogAppender.INFO) {
                this.insert(doc, line);
            }
        }
        boolean trimmed = false;
        while (this.lines.size() > MAX_LINES) {
            this.lines.removeFirst();
            trimmed = true;
        }
        if (trimmed) {
            this.trimDocument(doc);
        }
        if (this.autoScroll.isSelected()) {
            this.console.setCaretPosition(doc.getLength());
        }
    }

    private void insert(Document doc, GuiLogAppender.Line line) {
        SimpleAttributeSet style = line.level == GuiLogAppender.ERROR ? this.styleError
                : line.level == GuiLogAppender.WARN ? this.styleWarn : this.styleInfo;
        try {
            doc.insertString(doc.getLength(), line.text + "\n", style);
        } catch (BadLocationException ignored) {
        }
    }

    /** Mantem o documento perto do limite de linhas, cortando do inicio. */
    private void trimDocument(Document doc) {
        javax.swing.text.Element root = doc.getDefaultRootElement();
        int excess = root.getElementCount() - MAX_LINES;
        if (excess <= 0) {
            return;
        }
        int end = root.getElement(Math.min(excess, root.getElementCount() - 1)).getStartOffset();
        try {
            doc.remove(0, end);
        } catch (BadLocationException ignored) {
        }
        this.searchFrom = 0;
    }

    private void rerender() {
        Document doc = this.console.getDocument();
        this.console.setText("");
        boolean filter = this.onlyProblems.isSelected();
        for (GuiLogAppender.Line line : this.lines) {
            if (!filter || line.level != GuiLogAppender.INFO) {
                this.insert(doc, line);
            }
        }
        this.searchFrom = 0;
        this.console.setCaretPosition(doc.getLength());
    }

    private void findNext() {
        String q = this.search.getText();
        if (q == null || q.isEmpty()) {
            return;
        }
        try {
            Document doc = this.console.getDocument();
            String text = doc.getText(0, doc.getLength()).toLowerCase(Locale.ROOT);
            String needle = q.toLowerCase(Locale.ROOT);
            int idx = text.indexOf(needle, this.searchFrom);
            if (idx < 0) {
                idx = text.indexOf(needle);
            }
            this.console.getHighlighter().removeAllHighlights();
            if (idx < 0) {
                this.status.setText("Busca: \"" + q + "\" não encontrado.");
                return;
            }
            this.autoScroll.setSelected(false);
            this.console.getHighlighter().addHighlight(idx, idx + needle.length(),
                    new DefaultHighlighter.DefaultHighlightPainter(Theme.HIGHLIGHT));
            this.console.setCaretPosition(idx);
            this.console.moveCaretPosition(idx + needle.length());
            this.searchFrom = idx + needle.length();
        } catch (BadLocationException ignored) {
        }
    }

    // ------------------------------------------------------------------ metricas

    private int rangeSeconds() {
        switch (this.range.getSelectedIndex()) {
            case 0:
                return 300;
            case 2:
                return 1800;
            case 3:
                return 3600;
            default:
                return 900;
        }
    }

    private void refreshMetrics() {
        Snapshot s = ServerMetrics.last();
        // os toggles podem ter sido mudados por //track_npc
        this.telemetry.setSelected(l2.gameserver.utils.MovementTelemetryLogger.ENABLED);
        this.npcTrace.setSelected(l2.gameserver.ai.debug.NpcMovementDebugTracer.GLOBAL_TRACE);
        this.updateStatus(s);
        if (s == null) {
            return;
        }
        this.vOnline.setText(String.valueOf(s.playersOnline));
        this.sOnline.setText(s.offlineTraders + " lojas offline");
        this.vCpu.setText(s.processCpuPercent < 0 ? "-" : String.format("%.0f %%", s.processCpuPercent));
        this.sCpu.setText(Runtime.getRuntime().availableProcessors() + " núcleos");
        double heapPct = s.heapMaxMb <= 0 ? 0 : 100.0 * s.heapUsedMb / s.heapMaxMb;
        this.vHeap.setText(s.heapUsedMb + " MB");
        this.sHeap.setText(String.format("de %d MB (%.0f%%)", s.heapMaxMb, heapPct));
        this.vHeap.setForeground(heapPct > 90 ? Theme.ERROR : heapPct > 75 ? Theme.WARN : Theme.TEXT);
        this.vThreads.setText(String.valueOf(s.threads));
        this.sThreads.setText(s.deadlockedThreads == 0 ? "nenhuma travada" : s.deadlockedThreads + " em deadlock!");
        this.sThreads.setForeground(s.deadlockedThreads == 0 ? Theme.MUTED : Theme.ERROR);
        this.vIn.setText(MetricChart.format(s.packetsInPerSec));
        this.sIn.setText(String.format("%.1f KB/s", s.kbInPerSec).replace('.', ','));
        this.vOut.setText(MetricChart.format(s.packetsOutPerSec));
        this.sOut.setText(String.format("%.1f KB/s", s.kbOutPerSec).replace('.', ','));
        this.vAi.setText(ms(s.aiAvgMs));
        this.sAi.setText("pior " + ms(s.aiMaxMs) + " | " + s.aiThinksPerSec + "/s");
        this.vAi.setForeground(s.aiMaxMs > 50 ? Theme.WARN : Theme.TEXT);
        this.vPath.setText(ms(s.pathfindAvgMs));
        this.sPath.setText("pior " + ms(s.pathfindMaxMs) + " | " + s.pathfindsPerSec + "/s");
        this.vPath.setForeground(s.pathfindMaxMs > 50 ? Theme.WARN : Theme.TEXT);
        this.vGc.setText(s.gcMsPerSec + " ms/s");
        this.sGc.setText(s.gcPerSec + " coletas no último segundo");
        this.vUptime.setText(duration(s.uptimeMillis));
        this.sUptime.setText("desde " + new SimpleDateFormat("dd/MM HH:mm").format(new Date(s.timeMillis - s.uptimeMillis)));

        if (this.chOnline.isShowing()) {
            int secs = this.rangeSeconds();
            Snapshot[] data = ServerMetrics.history(secs);
            for (MetricChart c : new MetricChart[]{this.chOnline, this.chHeap, this.chPackets, this.chAi, this.chPath, this.chCpu}) {
                c.setData(data, secs);
            }
        }
    }

    private void updateStatus(Snapshot s) {
        StringBuilder sb = new StringBuilder();
        if (s != null) {
            sb.append("Online: ").append(s.playersOnline).append("   |   Ligado há ").append(duration(s.uptimeMillis));
        }
        try {
            int secs = l2.gameserver.Shutdown.getInstance().getSeconds();
            int mode = l2.gameserver.Shutdown.getInstance().getMode();
            if (secs >= 0 && mode != l2.gameserver.Shutdown.NONE) {
                sb.append("   |   ").append(mode == l2.gameserver.Shutdown.RESTART ? "Reinício" : "Desligamento")
                        .append(" em ").append(secs).append(" s");
                this.status.setForeground(Theme.WARN);
            } else {
                this.status.setForeground(Theme.MUTED);
            }
        } catch (Throwable ignored) {
            // Shutdown ainda nao carregado durante o boot
        }
        if (sb.length() > 0) {
            this.status.setText(sb.toString());
        }
    }

    private static String ms(double v) {
        return (v < 10 ? String.format("%.2f", v) : String.format("%.0f", v)).replace('.', ',') + " ms";
    }

    private static String duration(long millis) {
        long s = millis / 1000L;
        long d = s / 86400L, h = s / 3600L % 24L, m = s / 60L % 60L;
        return d > 0 ? String.format("%dd %02dh %02dm", d, h, m) : String.format("%02d:%02d:%02d", h, m, s % 60L);
    }

    // ------------------------------------------------------------------ acoes

    private void runInBackground(String what, Runnable action) {
        Thread t = new Thread(() -> {
            try {
                action.run();
            } catch (Throwable e) {
                LOG.warn("Janela: falha ao " + what, e);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this.frame,
                        "Falha ao " + what + ":\n" + e, "Erro", JOptionPane.ERROR_MESSAGE));
            }
        }, "ServerGui-action");
        t.setDaemon(true);
        t.start();
    }

    private void reloadConfigs() {
        if (JOptionPane.showConfirmDialog(this.frame, "Recarregar todos os arquivos de config/*.properties?",
                "Recarregar configs", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        this.runInBackground("recarregar configs", () -> {
            l2.gameserver.Config.load();
            LOG.info("Configs recarregadas pela janela do servidor.");
        });
    }

    private void announce() {
        String text = JOptionPane.showInputDialog(this.frame, "Mensagem para todos os jogadores:", "Anunciar",
                JOptionPane.PLAIN_MESSAGE);
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        this.runInBackground("anunciar", () -> {
            l2.gameserver.Announcements.getInstance().announceToAll(text.trim());
            LOG.info("Anuncio enviado pela janela: " + text.trim());
        });
    }

    private void scheduleShutdown(boolean restart) {
        JSpinner seconds = new JSpinner(new SpinnerNumberModel(60, 0, 3600, 10));
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.add(new JLabel((restart ? "Reiniciar" : "Desligar") + " o servidor em quantos segundos?"), BorderLayout.NORTH);
        p.add(seconds, BorderLayout.CENTER);
        p.add(new JLabel("Os jogadores são avisados e tudo é salvo antes de fechar.", SwingConstants.LEFT), BorderLayout.SOUTH);
        if (JOptionPane.showConfirmDialog(this.frame, p, restart ? "Reiniciar" : "Desligar",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        int secs = (Integer) seconds.getValue();
        this.runInBackground(restart ? "agendar reinicio" : "agendar desligamento", () ->
                l2.gameserver.Shutdown.getInstance().schedule(secs, restart ? l2.gameserver.Shutdown.RESTART : l2.gameserver.Shutdown.SHUTDOWN));
    }

    private void cancelShutdown() {
        this.runInBackground("cancelar desligamento", () -> {
            l2.gameserver.Shutdown.getInstance().cancel();
            LOG.info("Desligamento/reinicio cancelado pela janela do servidor.");
        });
    }

    private void threadDump() {
        this.runInBackground("salvar thread dump", () -> {
            File dir = new File("log");
            dir.mkdirs();
            File out = new File(dir, "threads-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".txt");
            try (PrintWriter w = new PrintWriter(out, StandardCharsets.UTF_8)) {
                this.writeDump(w);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
            LOG.info("Thread dump salvo em " + out.getPath());
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this.frame, "Salvo em " + out.getAbsolutePath()));
        });
    }

    private void writeDump(PrintWriter w) {
        {
                long[] dead = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
                w.println("Thread dump " + new Date() + (dead == null ? "" : "  -- DEADLOCK em " + dead.length + " threads"));
                for (ThreadInfo info : ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)) {
                    w.println();
                    w.println("\"" + info.getThreadName() + "\" id=" + info.getThreadId() + " " + info.getThreadState()
                            + (info.getLockName() == null ? "" : " em " + info.getLockName())
                            + (info.getLockOwnerName() == null ? "" : " (dono: " + info.getLockOwnerName() + ")"));
                    for (StackTraceElement e : info.getStackTrace()) {
                        w.println("    at " + e);
                    }
                }
        }
    }

    private void onClose() {
        Object[] options = {"Desligar (salvando tudo)", "Cancelar"};
        int r = JOptionPane.showOptionDialog(this.frame,
                "Fechar a janela desliga o GameServer.\nOs jogadores são desconectados e tudo é salvo antes.",
                "Desligar o servidor?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        if (r == 0) {
            this.runInBackground("desligar", () -> l2.gameserver.Shutdown.getInstance().schedule(0, l2.gameserver.Shutdown.SHUTDOWN));
        }
    }
}
