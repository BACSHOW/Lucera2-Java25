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
import java.net.InetSocketAddress;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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

import l2.gameserver.Config;
import l2.gameserver.Shutdown;
import l2.gameserver.metrics.ServerMetrics;
import l2.gameserver.metrics.ServerMetrics.Snapshot;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import org.slf4j.ILoggerFactory;
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
            vOut = value(), vAi = value(), vPath = value(), vGc = value(), vUptime = value(), vNetQueue = value(), vNetFull = value();
    private final JLabel sOnline = sub(), sCpu = sub(), sHeap = sub(), sThreads = sub(), sIn = sub(), sOut = sub(),
            sAi = sub(), sPath = sub(), sGc = sub(), sUptime = sub(), sNetQueue = sub(), sNetFull = sub();
    private final JComboBox<String> range = new JComboBox<>(new String[]{"5 min", "15 min", "30 min", "60 min"});
    private final MetricChart chOnline, chHeap, chPackets, chAi, chPath, chCpu, chNet;
    // movimento (contadores por minuto)
    private final EnumMap<ServerMetrics.Counter, JLabel[]> moveCards = new EnumMap<>(ServerMetrics.Counter.class);
    private final MetricChart chZones, chPaths, chNpcs, chFarm;
    // barra de status / acoes
    private final JLabel status = new JLabel(" ");
    private final JCheckBox telemetry = new JCheckBox("Telemetria de movimento");
    private final JCheckBox npcTrace = new JCheckBox("Trace de NPCs");
    // faixa fixa no topo: status do login e rates
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel rates = new JLabel(" ");
    private PeerStatusPanel loginStatus;
    private ConfigEditorPanel configEditor;

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
        this.chNet = new MetricChart("Fila de envio (maior)", "KB", 64)
                .add("KB", Theme.ORANGE, s -> s.netMaxPendingKb);
        this.chZones = new MetricChart("Correções de posição do jogador", "por minuto", 5)
                .add("amarela (suave)", Theme.ORANGE, s -> s.perMin(ServerMetrics.Counter.VP_YELLOW))
                .add("vermelha (salto)", Theme.ERROR, s -> s.perMin(ServerMetrics.Counter.VP_RED))
                .add("bloqueios", Theme.GRAY, s -> s.perMin(ServerMetrics.Counter.ARRIVED_BLOCKED));
        this.chPaths = new MetricChart("Como os caminhos foram achados", "por minuto", 10)
                .add("NBA* suavizado", Theme.GREEN, s -> s.perMin(ServerMetrics.Counter.PATH_NBA_SMOOTH))
                .add("NBA* bruto", Theme.BLUE, s -> s.perMin(ServerMetrics.Counter.PATH_NBA_RAW))
                .add("clássico", Theme.ORANGE, s -> s.perMin(ServerMetrics.Counter.PATH_CLASSIC))
                .add("sem caminho", Theme.ERROR, s -> s.perMin(ServerMetrics.Counter.PATH_NONE));
        this.chNpcs = new MetricChart("Monstros", "por minuto", 5)
                .add("quinas suavizadas", Theme.GREEN, s -> s.perMin(ServerMetrics.Counter.SMOOTH_CORNERS))
                .add("voltaram ao spawn", Theme.BLUE, s -> s.perMin(ServerMetrics.Counter.NPC_RETURN_HOME))
                .add("por falha de caminho", Theme.ERROR, s -> s.perMin(ServerMetrics.Counter.NPC_RETURN_HOME_PATHFAIL));
        this.chFarm = new MetricChart("AutoFarm: espera pelo cliente", "por minuto", 5)
                .add("esperas", Theme.PURPLE, s -> s.perMin(ServerMetrics.Counter.CLIENT_SYNC_WAIT))
                .add("esgotadas", Theme.ORANGE, s -> s.perMin(ServerMetrics.Counter.CLIENT_SYNC_TIMEOUT));
    }

    // ------------------------------------------------------------------ inicializacao

    /** Chamado no main(), logo depois de configurar o log. */
    public static void startIfEnabled() {
        ServerMetrics.start();
        if (!isEnabled()) {
            abortIfHidden("a janela esta desligada (EnableServerGui = False, -Dl2.gui=false ou sem area de trabalho)");
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
            LOG.warn("Janela do servidor nao pode ser aberta, seguindo so no console: " + t, t);
            abortIfHidden("a janela nao pode ser aberta (" + t + ")");
        }
    }

    /**
     * Iniciado com o console oculto (StartGameServerGui.vbs), a janela e o unico jeito de ver e desligar o
     * servidor. Sem ela o servidor rodaria invisivel, entao encerra antes de carregar qualquer coisa
     * (codigo 1: o GameServerGui.bat mostra o aviso).
     */
    private static void abortIfHidden(String reason) {
        if (!Boolean.getBoolean("l2.hiddenConsole")) {
            return;
        }
        LOG.error("Servidor iniciado sem console, mas " + reason + ". Encerrando para nao rodar invisivel. "
                + "Use StartGameServer.bat ou ligue EnableServerGui = True no config/server.properties.");
        System.exit(1);
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

    /** Liga o log (logback) e o System.err a janela. Tambem usado pela janela do login. */
    public static void captureLogs() {
        attachLogAppender();
        teeSystemErr();
    }

    private static void attachLogAppender() {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof ch.qos.logback.classic.LoggerContext context) {
            GuiLogAppender appender = new GuiLogAppender();
            appender.setContext(context);
            appender.setName("GUI");
            appender.start();
            context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
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

        this.configEditor = new ConfigEditorPanel(new File(Config.CONFIGURATION_FILE).getAbsoluteFile().getParentFile(),
                Config::load, "Opções marcadas com (reiniciar) só valem depois de reiniciar o servidor.", ServerGui::restartOnly);
        this.tabs.addTab("Console", this.buildConsole());
        this.tabs.addTab("Métricas", this.buildMetrics());
        this.tabs.addTab("Movimento", this.buildMovement());
        this.tabs.addTab("Configurações", this.configEditor);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(Theme.BG);
        north.add(this.buildToolbar());
        north.add(this.buildStatusStrip());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(north, BorderLayout.NORTH);
        root.add(this.tabs, BorderLayout.CENTER);
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

    /** Faixa sempre visivel (qualquer aba): status do login e rates do servidor. */
    private JComponent buildStatusStrip() {
        this.loginStatus = new PeerStatusPanel("Login",
                () -> AuthServerCommunication.getInstance().isRegistered(),
                null,
                () -> new InetSocketAddress(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT),
                new File("../login"), "StartAuthServerGui.vbs", "StartAuthServer.bat");
        this.rates.setForeground(Theme.TEXT);
        this.rates.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 8));
        JButton editRates = button("Editar rates", e -> {
            this.tabs.setSelectedComponent(this.configEditor);
            this.configEditor.showFile(new File(Config.CONFIGURATION_FILE).getName(), "Rate");
        });
        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        east.setOpaque(false);
        east.add(editRates);

        JPanel strip = new JPanel(new BorderLayout());
        strip.setBackground(Theme.PANEL);
        strip.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        strip.add(this.loginStatus, BorderLayout.WEST);
        strip.add(this.rates, BorderLayout.CENTER);
        strip.add(east, BorderLayout.EAST);
        return strip;
    }

    private void refreshStatusStrip() {
        this.loginStatus.refresh();
        this.rates.setText("Rates   XP x" + rate(Config.RATE_XP) + "   SP x" + rate(Config.RATE_SP)
                + "   Adena x" + rate(Config.RATE_DROP_ADENA) + "   Drop x" + rate(Config.RATE_DROP_ITEMS)
                + "   Spoil x" + rate(Config.RATE_DROP_SPOIL) + "   Quest x" + rate(Config.RATE_QUESTS_REWARD));
    }

    /**
     * Opcoes lidas so ao ligar o servidor (rede, banco, carga do geodata, threads): o editor marca "(reiniciar)".
     * Lista conservadora: o que nao esta aqui e recarregado pelo Config.load().
     */
    private static boolean restartOnly(String file, String key) {
        String f = file.toLowerCase(Locale.ROOT);
        if (f.equals("telnet.properties") || key.equals("EnableServerGui") || key.matches("(?i).*(Threads?|ThreadPool.*)")) {
            return true;
        }
        if (f.equals("server.properties")) {
            return key.matches("Gameserver(Hostname|Port)|(External|Internal)Hostname|Login(Port|Host|UseCrypt)|RequestServerID"
                    + "|AcceptAlternateID|Database.*|HAProxy.*|UseNetty|Netty.*|UseDisruptor|Disruptor.*|EnableDeterministic120HzPipeline"
                    + "|Deterministic120HzPort|EnableNetworkJitWarmup|EnableZstdGeodataCache");
        }
        if (f.equals("geodata.properties")) {
            return key.matches("Geo(First|Last)[XY]|GeoFilesPattern|AllowGeodata|CompactGeoData|PathFindBuffers");
        }
        return false;
    }

    private static String rate(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v).replace('.', ',');
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
        JPanel cards = new JPanel(new GridLayout(2, 6, 8, 8));
        cards.setBackground(Theme.BG);
        cards.add(card("Jogadores online", this.vOnline, this.sOnline));
        cards.add(card("CPU do processo", this.vCpu, this.sCpu));
        cards.add(card("Memória (heap)", this.vHeap, this.sHeap));
        cards.add(card("Threads", this.vThreads, this.sThreads));
        cards.add(card("Tempo ligado", this.vUptime, this.sUptime));
        cards.add(card("Fila de envio (maior)", this.vNetQueue, this.sNetQueue));
        cards.add(card("Pacotes recebidos/s", this.vIn, this.sIn));
        cards.add(card("Pacotes enviados/s", this.vOut, this.sOut));
        cards.add(card("IA dos NPCs", this.vAi, this.sAi));
        cards.add(card("Pathfinding", this.vPath, this.sPath));
        cards.add(card("Coleta de lixo (GC)", this.vGc, this.sGc));
        cards.add(card("Conexões cheias", this.vNetFull, this.sNetFull));

        JPanel charts = new JPanel(new GridLayout(2, 4, 8, 8));
        charts.setBackground(Theme.BG);
        for (MetricChart c : new MetricChart[]{this.chOnline, this.chHeap, this.chCpu, this.chNet, this.chPackets, this.chAi, this.chPath}) {
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

    private JComponent buildMovement() {
        ServerMetrics.Counter[] shown = {
                ServerMetrics.Counter.VP_GREEN, ServerMetrics.Counter.VP_YELLOW, ServerMetrics.Counter.VP_RED,
                ServerMetrics.Counter.ARRIVED_BLOCKED, ServerMetrics.Counter.CLIENT_SYNC_WAIT, ServerMetrics.Counter.CLIENT_SYNC_TIMEOUT,
                ServerMetrics.Counter.PATH_NBA_SMOOTH, ServerMetrics.Counter.PATH_NBA_RAW, ServerMetrics.Counter.PATH_CLASSIC,
                ServerMetrics.Counter.PATH_NONE, ServerMetrics.Counter.SMOOTH_CORNERS, ServerMetrics.Counter.NPC_RETURN_HOME,
                ServerMetrics.Counter.NPC_RETURN_HOME_PATHFAIL, ServerMetrics.Counter.PATH_BUFFERS_EXHAUSTED, ServerMetrics.Counter.NPC_INFO_DROPPED};
        String[] titles = {"Zona verde (aceita)", "Zona amarela (suave)", "Zona vermelha (salto)",
                "Bloqueios ao andar", "AutoFarm: esperas", "AutoFarm: esgotadas",
                "Caminho NBA* suavizado", "Caminho NBA* bruto", "Caminho clássico",
                "Sem caminho", "Quinas suavizadas", "Voltas ao spawn",
                "Voltas por falha de caminho", "Buffers esgotados", "NPCs não enviados"};
        JPanel cards = new JPanel(new GridLayout(3, 5, 8, 8));
        cards.setBackground(Theme.BG);
        for (int i = 0; i < shown.length; i++) {
            JLabel v = value();
            JLabel sub = sub();
            this.moveCards.put(shown[i], new JLabel[]{v, sub});
            JComponent c = card(titles[i], v, sub);
            c.setToolTipText(shown[i].label);
            cards.add(c);
        }
        JLabel hint = new JLabel("Valores do último minuto; abaixo, o total desde que o servidor ligou. "
                + "Vermelho em \"Buffers esgotados\" ou \"NPCs não enviados\" indica problema.");
        hint.setForeground(Theme.MUTED);

        JPanel charts = new JPanel(new GridLayout(2, 2, 8, 8));
        charts.setBackground(Theme.BG);
        for (MetricChart c : new MetricChart[]{this.chZones, this.chPaths, this.chNpcs, this.chFarm}) {
            c.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
            charts.add(c);
        }
        JPanel north = new JPanel(new BorderLayout(0, 6));
        north.setBackground(Theme.BG);
        north.add(cards, BorderLayout.CENTER);
        north.add(hint, BorderLayout.SOUTH);

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Theme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(north, BorderLayout.NORTH);
        p.add(charts, BorderLayout.CENTER);
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
        this.refreshStatusStrip();
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
        this.vNetQueue.setText(s.netMaxPendingKb + " KB");
        long shed = s.perMin(ServerMetrics.Counter.NET_SHED);
        long resync = s.perMin(ServerMetrics.Counter.NET_RESYNC);
        this.sNetQueue.setText((s.netSlowedNow > 0 ? s.netSlowedNow + " com //netslow | " : "")
                + (shed > 0 || resync > 0 ? shed + " pulados, " + resync + " resync/min" : "esperando para sair"));
        this.vNetQueue.setForeground(s.netMaxPendingKb >= 512 ? Theme.WARN : Theme.TEXT);
        this.vNetFull.setText(String.valueOf(s.netFullNow));
        this.sNetFull.setText(s.perMin(ServerMetrics.Counter.NET_UNWRITABLE) + " no último minuto | total "
                + s.total(ServerMetrics.Counter.NET_UNWRITABLE));
        this.vNetFull.setForeground(s.netFullNow > 0 ? Theme.ERROR : Theme.TEXT);

        for (Map.Entry<ServerMetrics.Counter, JLabel[]> e : this.moveCards.entrySet()) {
            ServerMetrics.Counter c = e.getKey();
            long perMin = s.perMin(c);
            e.getValue()[0].setText(String.valueOf(perMin));
            e.getValue()[1].setText("total " + s.total(c));
            boolean alarm = (c == ServerMetrics.Counter.PATH_BUFFERS_EXHAUSTED || c == ServerMetrics.Counter.NPC_INFO_DROPPED) && perMin > 0;
            boolean warn = c == ServerMetrics.Counter.VP_RED && perMin > 0;
            e.getValue()[0].setForeground(alarm ? Theme.ERROR : warn ? Theme.WARN : Theme.TEXT);
        }
        if (this.chZones.isShowing()) {
            int secs = this.rangeSeconds();
            Snapshot[] data = ServerMetrics.history(secs);
            for (MetricChart c : new MetricChart[]{this.chZones, this.chPaths, this.chNpcs, this.chFarm}) {
                c.setData(data, secs);
            }
        }

        if (this.chOnline.isShowing()) {
            int secs = this.rangeSeconds();
            Snapshot[] data = ServerMetrics.history(secs);
            for (MetricChart c : new MetricChart[]{this.chOnline, this.chHeap, this.chPackets, this.chAi, this.chPath, this.chCpu, this.chNet}) {
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
            int secs = Shutdown.getInstance().getSeconds();
            int mode = Shutdown.getInstance().getMode();
            if (secs >= 0 && mode != Shutdown.NONE) {
                sb.append("   |   ").append(mode == Shutdown.RESTART ? "Reinício" : "Desligamento")
                        .append(" em ").append(duration(secs * 1000L));
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