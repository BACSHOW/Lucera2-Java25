package l2.authserver.gui;

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
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import l2.authserver.Config;
import l2.authserver.GameServerManager;
import l2.authserver.LoginGuard;
import l2.authserver.LoginMetrics;
import l2.authserver.LoginMetrics.Snapshot;
import l2.authserver.network.gamecomm.GameServer;
import l2.gameserver.gui.ConfigEditorPanel;
import l2.gameserver.gui.GuiLogAppender;
import l2.gameserver.gui.PeerStatusPanel;
import l2.gameserver.gui.ServerGui;
import l2.gameserver.gui.Theme;
import l2.gameserver.gui.TimeSeriesChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Janela propria do login: Console, Visao geral (numeros e graficos), Bans (lista e desbanir) e
 * GameServers. Liga com EnableServerGui = True no config/authserver.properties (padrao) e so abre com
 * area de trabalho. Iniciado sem console (-Dl2.hiddenConsole=true), encerra se a janela nao abrir.
 */
public final class LoginGui {
    private static final Logger LOG = LoggerFactory.getLogger(LoginGui.class);
    private static final int MAX_LINES = 8000;

    private final JFrame frame = new JFrame("Lucera2 - Login Server");
    private final JTextPane console = new JTextPane();
    private final ArrayDeque<GuiLogAppender.Line> lines = new ArrayDeque<>();
    private final JCheckBox autoScroll = new JCheckBox("Rolagem automática", true);
    private final JCheckBox onlyProblems = new JCheckBox("Somente avisos e erros");
    private final JTextField search = new JTextField(18);
    private final SimpleAttributeSet styleInfo = new SimpleAttributeSet();
    private final SimpleAttributeSet styleWarn = new SimpleAttributeSet();
    private final SimpleAttributeSet styleError = new SimpleAttributeSet();
    private int searchFrom;

    private final JLabel vOk = value(), vFail = value(), vCreated = value(), vConn = value(), vBans = value(), vGs = value(),
            vPlayers = value(), vUptime = value();
    private final JLabel sOk = sub(), sFail = sub(), sCreated = sub(), sConn = sub(), sBans = sub(), sGs = sub(),
            sPlayers = sub(), sUptime = sub();
    private final JComboBox<String> range = new JComboBox<>(new String[]{"5 min", "15 min", "30 min", "60 min"});
    private final TimeSeriesChart<Snapshot> chLogins = new TimeSeriesChart<Snapshot>("Logins por minuto", "por minuto", 5, s -> s.timeMillis)
            .add("aceitos", Theme.GREEN, s -> s.okPerMin)
            .add("recusados", Theme.ORANGE, s -> s.failedPerMin)
            .add("contas novas", Theme.BLUE, s -> s.createdPerMin);
    private final TimeSeriesChart<Snapshot> chConn = new TimeSeriesChart<Snapshot>("Conexões por minuto", "por minuto", 5, s -> s.timeMillis)
            .add("aceitas", Theme.BLUE, s -> s.acceptedPerMin)
            .add("recusadas", Theme.ORANGE, s -> s.refusedPerMin)
            .add("bans", Theme.PURPLE, s -> s.bansPerMin);

    private final BanModel banModel = new BanModel();
    private final JTable banTable = new JTable(this.banModel);
    private final GsModel gsModel = new GsModel();
    private final JLabel status = new JLabel(" ");
    private PeerStatusPanel gameStatus;

    // ------------------------------------------------------------------ inicializacao

    /** Chamado no inicio do main() do AuthServer. */
    public static void startIfEnabled() {
        LoginMetrics.start();
        if (Boolean.getBoolean("l2.hiddenConsole")) {
            detachConsoleAppender();
        }
        if (!isEnabled()) {
            abortIfHidden("a janela esta desligada (EnableServerGui = False, -Dl2.gui=false ou sem area de trabalho)");
            return;
        }
        ServerGui.captureLogs();
        try {
            SwingUtilities.invokeAndWait(() -> new LoginGui().build());
        } catch (Throwable t) {
            LOG.warn("Janela do login nao pode ser aberta, seguindo so no console: " + t, t);
            abortIfHidden("a janela nao pode ser aberta (" + t + ")");
        }
    }

    private static boolean isEnabled() {
        if ("false".equalsIgnoreCase(System.getProperty("l2.gui", "")) || GraphicsEnvironment.isHeadless()) {
            return false;
        }
        Properties p = new Properties();
        File f = new File(Config.LOGIN_CONFIGURATION_FILE);
        if (f.exists()) {
            try (InputStream in = new FileInputStream(f)) {
                p.load(in);
            } catch (Exception ignored) {
            }
        }
        return Boolean.parseBoolean(p.getProperty("EnableServerGui", "true").trim());
    }

    /** Console oculto: a saida vai para log/stdout.log e o log completo ja esta no log/java.log; nao duplica. */
    private static void detachConsoleAppender() {
        org.slf4j.ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof ch.qos.logback.classic.LoggerContext context) {
            context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).detachAppender("CONSOLE");
        }
    }

    private static void abortIfHidden(String reason) {
        if (!Boolean.getBoolean("l2.hiddenConsole")) {
            return;
        }
        LOG.error("Login iniciado sem console, mas " + reason + ". Encerrando para nao rodar invisivel. "
                + "Use StartAuthServer.bat ou ligue EnableServerGui = True no config/authserver.properties.");
        System.exit(1);
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
        tabs.addTab("Visão geral", this.buildOverview());
        tabs.addTab("Bans", this.buildBans());
        tabs.addTab("GameServers", this.buildGameServers());
        tabs.addTab("Configurações", new ConfigEditorPanel(new File(Config.LOGIN_CONFIGURATION_FILE).getAbsoluteFile().getParentFile(),
                Config::reloadSecuritySettings,
                "Proteção, contas e mensagens valem na hora; portas, banco e chaves só depois de reiniciar o login."));

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setBackground(Theme.BG);
        north.add(this.buildToolbar());
        north.add(this.buildStatusStrip());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(north, BorderLayout.NORTH);
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
                LoginGui.this.onClose();
            }
        });
        this.frame.setSize(1100, 700);
        this.frame.setMinimumSize(new Dimension(800, 500));
        this.frame.setLocationByPlatform(true);
        this.frame.setVisible(true);

        new Timer(200, e -> this.pumpLog()).start();
        new Timer(1000, e -> this.refresh()).start();
    }

    private JComponent buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        bar.setBackground(Theme.BG);
        bar.add(button("Recarregar proteção", e -> this.reloadSecurity()));
        bar.add(button("Banir IP...", e -> this.banIp()));
        bar.add(button("Reiniciar...", e -> this.exit(true)));
        bar.add(button("Desligar...", e -> this.exit(false)));
        bar.add(button("Salvar thread dump", e -> this.threadDump()));
        return bar;
    }

    /** Faixa sempre visivel (qualquer aba): status do GameServer. */
    private JComponent buildStatusStrip() {
        this.gameStatus = new PeerStatusPanel("GameServer",
                () -> {
                    Snapshot s = LoginMetrics.last();
                    return s != null && s.gameServersOnline > 0;
                },
                () -> {
                    Snapshot s = LoginMetrics.last();
                    return s == null ? null : s.playersOnline + " jogadores";
                },
                LoginGui::gameServerAddress,
                new File("../game"), "StartGameServerGui.vbs", "StartGameServer.bat");
        JPanel strip = new JPanel(new BorderLayout());
        strip.setBackground(Theme.PANEL);
        strip.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        strip.add(this.gameStatus, BorderLayout.WEST);
        return strip;
    }

    /** Porta do GameServer lida do config dele (../game/config/server.properties), para saber se ja esta no ar. */
    private static InetSocketAddress gameServerAddress() {
        File f = new File("../game/config/server.properties");
        if (!f.isFile()) {
            return null;
        }
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(f)) {
            p.load(in);
        } catch (Exception e) {
            return null;
        }
        String port = p.getProperty("GameserverPort", "7777").trim().split("[,;\\s]+")[0];
        try {
            return new InetSocketAddress("127.0.0.1", Integer.parseInt(port));
        } catch (NumberFormatException e) {
            return null;
        }
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
        top.add(label("Buscar:", Theme.TEXT));
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

    private JComponent buildOverview() {
        JPanel cards = new JPanel(new GridLayout(2, 4, 8, 8));
        cards.setBackground(Theme.BG);
        cards.add(card("Logins aceitos (min)", this.vOk, this.sOk));
        cards.add(card("Logins recusados (min)", this.vFail, this.sFail));
        cards.add(card("Contas criadas (min)", this.vCreated, this.sCreated));
        cards.add(card("Conexões (min)", this.vConn, this.sConn));
        cards.add(card("IPs banidos agora", this.vBans, this.sBans));
        cards.add(card("GameServers", this.vGs, this.sGs));
        cards.add(card("Jogadores nos GameServers", this.vPlayers, this.sPlayers));
        cards.add(card("Tempo ligado", this.vUptime, this.sUptime));

        JPanel charts = new JPanel(new GridLayout(1, 2, 8, 8));
        charts.setBackground(Theme.BG);
        for (TimeSeriesChart<Snapshot> c : List.of(this.chLogins, this.chConn)) {
            c.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
            charts.add(c);
        }
        JPanel rangeBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rangeBar.setBackground(Theme.BG);
        rangeBar.add(label("Período dos gráficos:", Theme.MUTED));
        this.range.setSelectedIndex(1);
        this.range.addActionListener(e -> this.refresh());
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

    private JComponent buildBans() {
        this.banTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(this.banTable);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        top.setBackground(Theme.BG);
        top.add(button("Desbanir selecionado", e -> this.unbanSelected()));
        top.add(button("Banir IP...", e -> this.banIp()));
        top.add(label("  Bans por senha errada, conta inexistente ou excesso de conexões. Reincidentes ficam mais tempo.", Theme.MUTED));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG);
        p.add(top, BorderLayout.NORTH);
        p.add(scroll(this.banTable), BorderLayout.CENTER);
        return p;
    }

    private JComponent buildGameServers() {
        JTable t = new JTable(this.gsModel);
        styleTable(t);
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG);
        p.add(scroll(t), BorderLayout.CENTER);
        return p;
    }

    private static void styleTable(JTable t) {
        t.setBackground(Theme.PANEL);
        t.setForeground(Theme.TEXT);
        t.setGridColor(Theme.GRID);
        t.setSelectionBackground(Theme.HIGHLIGHT);
        t.setSelectionForeground(Theme.TEXT);
        t.setRowHeight(24);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
    }

    private static JScrollPane scroll(JComponent c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        s.getViewport().setBackground(Theme.PANEL);
        return s;
    }

    private static JComponent card(String title, JLabel value, JLabel sub) {
        JPanel c = new JPanel(new BorderLayout(0, 2));
        c.setBackground(Theme.PANEL);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JLabel t = label(title, Theme.MUTED);
        t.setFont(t.getFont().deriveFont(11.5f));
        c.add(t, BorderLayout.NORTH);
        c.add(value, BorderLayout.CENTER);
        c.add(sub, BorderLayout.SOUTH);
        return c;
    }

    private static JLabel label(String text, java.awt.Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        return l;
    }

    private static JLabel value() {
        JLabel l = label("-", Theme.TEXT);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 20f));
        return l;
    }

    private static JLabel sub() {
        JLabel l = label(" ", Theme.MUTED);
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
        while (this.lines.size() > MAX_LINES) {
            this.lines.removeFirst();
        }
        javax.swing.text.Element root = doc.getDefaultRootElement();
        int excess = root.getElementCount() - MAX_LINES;
        if (excess > 0) {
            try {
                doc.remove(0, root.getElement(excess).getStartOffset());
            } catch (BadLocationException ignored) {
            }
            this.searchFrom = 0;
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

    // ------------------------------------------------------------------ atualizacao

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

    private void refresh() {
        this.gameStatus.refresh();
        Snapshot s = LoginMetrics.last();
        this.banModel.reload();
        this.gsModel.reload();
        if (s == null) {
            return;
        }
        this.vOk.setText(String.valueOf(s.okPerMin));
        this.sOk.setText(s.totalOk + " desde que ligou");
        this.vFail.setText(String.valueOf(s.failedPerMin));
        this.sFail.setText(s.totalFailed + " desde que ligou");
        this.vFail.setForeground(s.failedPerMin >= 20 ? Theme.WARN : Theme.TEXT);
        this.vCreated.setText(String.valueOf(s.createdPerMin));
        this.sCreated.setText(s.totalCreated + " desde que ligou" + (Config.AUTO_CREATE_ACCOUNTS ? "" : " (criação automática desligada)"));
        this.vConn.setText(String.valueOf(s.acceptedPerMin));
        this.sConn.setText(s.refusedPerMin + " recusadas no último minuto");
        this.vBans.setText(String.valueOf(s.activeBans));
        this.sBans.setText(s.totalBans + " bans desde que ligou");
        this.vBans.setForeground(s.activeBans > 0 ? Theme.WARN : Theme.TEXT);
        this.vGs.setText(s.gameServersOnline + " / " + s.gameServersTotal);
        this.sGs.setText(s.gameServersOnline == 0 ? "nenhum conectado" : "conectados / registrados");
        this.vGs.setForeground(s.gameServersOnline == 0 ? Theme.ERROR : Theme.TEXT);
        this.vPlayers.setText(String.valueOf(s.playersOnline));
        this.sPlayers.setText(String.format("memória %d/%d MB%s", s.heapUsedMb, s.heapMaxMb,
                s.processCpuPercent < 0 ? "" : String.format(" | CPU %.0f%%", s.processCpuPercent)));
        this.vUptime.setText(duration(s.uptimeMillis));
        this.sUptime.setText("desde " + new SimpleDateFormat("dd/MM HH:mm").format(new Date(s.timeMillis - s.uptimeMillis)));
        if (this.chLogins.isShowing()) {
            int secs = this.rangeSeconds();
            Snapshot[] data = LoginMetrics.history(secs);
            this.chLogins.setData(data, secs);
            this.chConn.setData(data, secs);
        }
        this.status.setText("GameServers: " + s.gameServersOnline + "/" + s.gameServersTotal + "   |   Jogadores: " + s.playersOnline
                + "   |   IPs banidos: " + s.activeBans + "   |   Ligado há " + duration(s.uptimeMillis));
    }

    private static String duration(long millis) {
        long s = millis / 1000L;
        long d = s / 86400L, h = s / 3600L % 24L, m = s / 60L % 60L;
        return d > 0 ? String.format("%dd %02dh %02dm", d, h, m) : String.format("%02d:%02d:%02d", h, m, s % 60L);
    }

    // ------------------------------------------------------------------ tabelas

    private static final class BanModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private static final String[] COLS = {"IP", "Motivo", "Nível", "Termina às", "Restante"};
        private List<LoginGuard.Ban> rows = new ArrayList<>();

        void reload() {
            List<LoginGuard.Ban> next = LoginGuard.getInstance().getActiveBans();
            boolean same = next.size() == this.rows.size();
            for (int i = 0; same && i < next.size(); i++) {
                same = next.get(i).ip.equals(this.rows.get(i).ip) && next.get(i).expire == this.rows.get(i).expire;
            }
            this.rows = next;
            if (same) {
                if (!this.rows.isEmpty()) {
                    this.fireTableRowsUpdated(0, this.rows.size() - 1); // so a coluna "Restante" muda
                }
            } else {
                this.fireTableDataChanged();
            }
        }

        LoginGuard.Ban get(int row) {
            return row >= 0 && row < this.rows.size() ? this.rows.get(row) : null;
        }

        @Override
        public int getRowCount() {
            return this.rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLS.length;
        }

        @Override
        public String getColumnName(int c) {
            return COLS[c];
        }

        @Override
        public Object getValueAt(int r, int c) {
            LoginGuard.Ban b = this.rows.get(r);
            switch (c) {
                case 0:
                    return b.ip;
                case 1:
                    return b.reason;
                case 2:
                    return b.level + 1;
                case 3:
                    return new SimpleDateFormat("dd/MM HH:mm:ss").format(new Date(b.expire));
                default:
                    return LoginGuard.formatDuration(Math.max(0L, b.expire - System.currentTimeMillis()));
            }
        }
    }

    private static final class GsModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private static final String[] COLS = {"ID", "Nome", "Situação", "Jogadores", "Máximo", "Endereço"};
        private Object[][] rows = new Object[0][];

        void reload() {
            List<Object[]> out = new ArrayList<>();
            if (!LoginMetrics.isServerReady()) {
                return;
            }
            try {
                for (GameServer gs : GameServerManager.getInstance().getGameServers()) {
                    String addr;
                    try {
                        addr = gs.getExternalHost().getHostAddress() + ":" + gs.getPort();
                    } catch (Exception e) {
                        addr = "-";
                    }
                    out.add(new Object[]{gs.getId(), gs.getName(), gs.isAuthed() ? "Conectado" : "Desconectado",
                            gs.isAuthed() ? gs.getOnline() : 0, gs.getMaxPlayers(), gs.isAuthed() ? addr : "-"});
                }
            } catch (Throwable ignored) {
                // gerenciador ainda carregando
            }
            Object[][] next = out.toArray(new Object[0][]);
            boolean sameSize = next.length == this.rows.length;
            this.rows = next;
            if (sameSize && next.length > 0) {
                this.fireTableRowsUpdated(0, next.length - 1);
            } else if (!sameSize) {
                this.fireTableDataChanged();
            }
        }

        @Override
        public int getRowCount() {
            return this.rows.length;
        }

        @Override
        public int getColumnCount() {
            return COLS.length;
        }

        @Override
        public String getColumnName(int c) {
            return COLS[c];
        }

        @Override
        public Object getValueAt(int r, int c) {
            return this.rows[r][c];
        }
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
        }, "LoginGui-action");
        t.setDaemon(true);
        t.start();
    }

    private void reloadSecurity() {
        this.runInBackground("recarregar a proteção", () -> {
            Config.reloadSecuritySettings();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this.frame,
                    "Recarregado: contas (AutoCreateAccounts, modelos de nome/senha), tentativas e bans,\n"
                            + "limite de conexões, WhiteIpList e BlackIpList.\n"
                            + "Portas, banco e chaves só mudam reiniciando o login."));
        });
    }

    private void banIp() {
        String ip = JOptionPane.showInputDialog(this.frame, "IP para banir (pelo próximo nível de IpBanSteps):", "Banir IP",
                JOptionPane.PLAIN_MESSAGE);
        if (ip == null || ip.trim().isEmpty()) {
            return;
        }
        String clean = ip.trim();
        if (LoginGuard.getInstance().isTrusted(clean)) {
            JOptionPane.showMessageDialog(this.frame, clean + " é confiável (WhiteIpList, local ou rede interna) e não pode ser banido.");
            return;
        }
        long ms = LoginGuard.getInstance().ban(clean, "manual (janela)");
        this.banModel.reload();
        JOptionPane.showMessageDialog(this.frame, clean + " banido por " + LoginGuard.formatDuration(ms) + ".");
    }

    private void unbanSelected() {
        LoginGuard.Ban b = this.banModel.get(this.banTable.getSelectedRow());
        if (b == null) {
            JOptionPane.showMessageDialog(this.frame, "Selecione um IP na lista.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this.frame, "Desbanir " + b.ip + " e zerar o histórico dele?", "Desbanir",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            LoginGuard.getInstance().unban(b.ip);
            this.banModel.reload();
        }
    }

    private void exit(boolean restart) {
        String msg = restart
                ? "Reiniciar o login agora?\nQuem estiver logando precisa tentar de novo; quem já está no jogo não é afetado."
                : "Desligar o login agora?\nNinguém consegue entrar até ele voltar; quem já está no jogo não é afetado.";
        if (JOptionPane.showConfirmDialog(this.frame, msg, restart ? "Reiniciar" : "Desligar",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
            LOG.info("Login " + (restart ? "reiniciando" : "desligando") + " pela janela.");
            this.runInBackground(restart ? "reiniciar" : "desligar", () -> Runtime.getRuntime().exit(restart ? 2 : 0));
        }
    }

    private void onClose() {
        Object[] options = {"Desligar o login", "Cancelar"};
        int r = JOptionPane.showOptionDialog(this.frame,
                "Fechar a janela desliga o Login Server.\nQuem já está no jogo não é afetado, mas ninguém novo consegue entrar.",
                "Desligar o login?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        if (r == 0) {
            this.runInBackground("desligar", () -> Runtime.getRuntime().exit(0));
        }
    }

    private void threadDump() {
        this.runInBackground("salvar thread dump", () -> {
            File dir = new File("log");
            dir.mkdirs();
            File out = new File(dir, "threads-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".txt");
            try (PrintWriter w = new PrintWriter(out, StandardCharsets.UTF_8)) {
                long[] dead = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
                w.println("Thread dump " + new Date() + (dead == null ? "" : "  -- DEADLOCK em " + dead.length + " threads"));
                for (ThreadInfo info : ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)) {
                    w.println();
                    w.println("\"" + info.getThreadName() + "\" id=" + info.getThreadId() + " " + info.getThreadState());
                    for (StackTraceElement e : info.getStackTrace()) {
                        w.println("    at " + e);
                    }
                }
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
            LOG.info("Thread dump salvo em " + out.getPath());
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this.frame, "Salvo em " + out.getAbsolutePath()));
        });
    }
}
