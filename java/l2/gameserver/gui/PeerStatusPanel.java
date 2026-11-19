package l2.gameserver.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Status do outro servidor (o login visto pelo GameServer, ou o GameServer visto pelo login), sempre visivel
 * no topo da janela: luz verde/amarela/vermelha, texto e botao para ligar o outro quando estao no mesmo PC.
 *
 * <ul>
 * <li>verde: conectado e registrado;</li>
 * <li>amarelo: o processo responde na porta, mas ainda nao registrou (ligando), ou foi ligado por este botao ha pouco;</li>
 * <li>vermelho: desligado. O botao "Ligar" abre o .vbs do outro servidor (so Windows, so no mesmo PC).</li>
 * </ul>
 */
public final class PeerStatusPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PeerStatusPanel.class);
    /** Depois de clicar em Ligar, fica amarelo esse tempo esperando o outro carregar (evita abrir dois). */
    private static final long STARTING_GRACE_MS = 180_000L;

    private final String peerName;
    private final BooleanSupplier registered;
    private final Supplier<String> detail;
    private final Supplier<InetSocketAddress> probe;
    private final File launchDir;
    private final String[] launchers;
    private final StatusLed led = new StatusLed();
    private final JLabel text = new JLabel();
    private final JButton start = new JButton();
    private volatile boolean portOpen;
    private volatile long launchedAt;

    /**
     * @param peerName   nome mostrado ("Login", "GameServer")
     * @param registered true quando o outro esta conectado e registrado
     * @param detail     texto extra quando ligado (pode ser null)
     * @param probe      endereco para testar se o processo ja esta no ar (null = nao testa)
     * @param launchDir  pasta do outro servidor (onde estao os .vbs/.bat)
     * @param launchers  arquivos para ligar, em ordem de preferencia (ex.: o .vbs e depois o .bat)
     */
    public PeerStatusPanel(String peerName, BooleanSupplier registered, Supplier<String> detail, Supplier<InetSocketAddress> probe,
            File launchDir, String... launchers) {
        super(new FlowLayout(FlowLayout.LEFT, 6, 0));
        this.peerName = peerName;
        this.registered = registered;
        this.detail = detail;
        this.probe = probe;
        this.launchDir = launchDir;
        this.launchers = launchers;
        this.setOpaque(false);
        this.text.setForeground(Theme.TEXT);
        this.text.setFont(this.text.getFont().deriveFont(Font.BOLD));
        this.start.setText("Ligar " + peerName);
        this.start.setFocusable(false);
        this.start.addActionListener(e -> this.onStart());
        this.add(this.led);
        this.add(this.text);
        this.add(this.start);
        this.start.setVisible(false);

        if (probe != null) {
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "PeerStatus-" + peerName);
                t.setDaemon(true);
                return t;
            });
            ses.scheduleWithFixedDelay(this::probePort, 0L, 3L, TimeUnit.SECONDS);
        }
    }

    /** Chamado pelo timer da janela (1 s). */
    public void refresh() {
        boolean on;
        try {
            on = this.registered.getAsBoolean();
        } catch (Throwable t) {
            on = false;
        }
        boolean recentlyLaunched = System.currentTimeMillis() - this.launchedAt < STARTING_GRACE_MS;
        if (on) {
            this.launchedAt = 0L;
            this.led.setState(StatusLed.State.ON);
            String d = this.detail == null ? null : this.detail.get();
            this.text.setText(this.peerName + ": ligado" + (d == null || d.isEmpty() ? "" : " (" + d + ")"));
            this.start.setVisible(false);
        } else if (this.portOpen || recentlyLaunched) {
            this.led.setState(StatusLed.State.STARTING);
            this.text.setText(this.peerName + ": " + (this.portOpen ? "no ar, conectando..." : "iniciando..."));
            this.start.setVisible(false);
        } else {
            this.led.setState(StatusLed.State.OFF);
            this.text.setText(this.peerName + ": desligado");
            this.start.setVisible(this.findLauncher() != null);
        }
    }

    /**
     * Testa a porta so quando o outro nao esta registrado (verde ja diz que esta no ar). Cada teste abre e fecha
     * uma conexao, e o outro servidor registra isso no console dele.
     */
    private void probePort() {
        try {
            if (this.registered.getAsBoolean()) {
                this.portOpen = false;
                return;
            }
        } catch (Throwable ignored) {
            // segue testando
        }
        InetSocketAddress addr;
        try {
            addr = this.probe.get();
        } catch (Throwable t) {
            addr = null;
        }
        if (addr == null || !isLocal(addr)) {
            this.portOpen = false;
            return;
        }
        try (Socket s = new Socket()) {
            s.connect(addr, 500);
            this.portOpen = true;
        } catch (IOException e) {
            this.portOpen = false;
        }
    }

    /** So liga pelo botao se o outro estiver neste PC. */
    private static boolean isLocal(InetSocketAddress addr) {
        InetAddress a = addr.getAddress();
        if (a == null) {
            return false;
        }
        if (a.isLoopbackAddress() || a.isAnyLocalAddress()) {
            return true;
        }
        try {
            return NetworkInterface.getByInetAddress(a) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private File findLauncher() {
        if (this.launchDir == null || !this.launchDir.isDirectory()) {
            return null;
        }
        if (this.probe != null) {
            InetSocketAddress addr = this.probe.get();
            if (addr == null || !isLocal(addr)) {
                return null; // o outro servidor esta em outro PC
            }
        }
        for (String name : this.launchers) {
            File f = new File(this.launchDir, name);
            if (f.isFile()) {
                return f;
            }
        }
        return null;
    }

    private void onStart() {
        File f = this.findLauncher();
        if (f == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                "Ligar o " + this.peerName + " neste PC?\n" + f.getAbsolutePath(),
                "Ligar " + this.peerName, JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            launch(f);
            this.launchedAt = System.currentTimeMillis();
            LOG.info("Janela: " + this.peerName + " ligado por " + f.getName() + ".");
            this.refresh();
        } catch (IOException e) {
            LOG.warn("Janela: falha ao ligar o " + this.peerName, e);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Nao foi possivel ligar o " + this.peerName + ":\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Abre o .vbs (console oculto) ou o .bat numa janela propria, com a pasta do outro servidor como diretorio. */
    private static void launch(File f) throws IOException {
        String name = f.getName().toLowerCase(Locale.ROOT);
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            throw new IOException("ligar o outro servidor pelo botao so funciona no Windows");
        }
        ProcessBuilder pb = name.endsWith(".vbs")
                ? new ProcessBuilder("wscript.exe", f.getName())
                : new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", f.getName());
        pb.directory(f.getParentFile());
        pb.start();
    }
}