package l2.authserver;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.authserver.database.L2DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protecao do login contra abuso (o "Fail2Ban" do README):
 * <ul>
 *   <li>limite de conexoes por IP (ConnectionFloodMax em ConnectionFloodWindow segundos);</li>
 *   <li>ban progressivo para reincidentes (IpBanSteps), voltando ao primeiro nivel depois de
 *       IpBanResetHours sem novo ban;</li>
 *   <li>bans salvos no banco (tabela login_ip_bans), valendo depois de reiniciar;</li>
 *   <li>WhiteIpList, 127.x e (com TrustLocalNetwork) a rede local nunca sao banidos.</li>
 * </ul>
 * Os bans por senha errada continuam vindo do IpBanManager (LoginTryBeforeBan), agora progressivos.
 */
public final class LoginGuard {
    private static final Logger LOG = LoggerFactory.getLogger(LoginGuard.class);
    private static final LoginGuard INSTANCE = new LoginGuard();

    /** Um IP banido (ou que ja foi, para o ban progressivo). */
    public static final class Ban {
        public final String ip;
        public final int level;
        public final long expire;
        public final long lastBan;
        public final String reason;

        Ban(String ip, int level, long expire, long lastBan, String reason) {
            this.ip = ip;
            this.level = level;
            this.expire = expire;
            this.lastBan = lastBan;
            this.reason = reason;
        }

        public boolean isActive(long now) {
            return this.expire > now;
        }
    }

    private static final class Window {
        long start;
        int count;
    }

    private final Map<String, Ban> bans = new ConcurrentHashMap<>();
    private final Map<String, Window> connections = new ConcurrentHashMap<>();
    private volatile boolean dbReady;

    private LoginGuard() {
    }

    public static LoginGuard getInstance() {
        return INSTANCE;
    }

    /** Cria a tabela (se preciso), carrega os bans salvos e agenda a limpeza. Chamado no inicio do AuthServer. */
    public void init() {
        if (Config.PERSIST_IP_BANS) {
            try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                 Statement st = con.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS `login_ip_bans` ("
                        + "`ip` VARCHAR(45) NOT NULL, `ban_level` INT NOT NULL DEFAULT 0, "
                        + "`ban_expire` BIGINT NOT NULL DEFAULT 0, `last_ban` BIGINT NOT NULL DEFAULT 0, "
                        + "`reason` VARCHAR(64) NOT NULL DEFAULT '', PRIMARY KEY (`ip`))");
                this.dbReady = true;
                long now = System.currentTimeMillis();
                int active = 0;
                try (ResultSet rs = st.executeQuery("SELECT ip, ban_level, ban_expire, last_ban, reason FROM login_ip_bans")) {
                    while (rs.next()) {
                        Ban b = new Ban(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getLong(4), rs.getString(5));
                        this.bans.put(b.ip, b);
                        if (b.isActive(now)) {
                            active++;
                        }
                    }
                }
                LOG.info("LoginGuard: " + active + " IPs banidos carregados do banco (" + this.bans.size() + " no historico).");
            } catch (Exception e) {
                LOG.warn("LoginGuard: nao foi possivel usar a tabela login_ip_bans; bans so na memoria. " + e);
            }
        }
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this::cleanup, 60000L, 60000L);
    }

    // ------------------------------------------------------------------ consultas

    /** WhiteIpList, loopback e (com TrustLocalNetwork) rede local: nunca banidos nem limitados. */
    public boolean isTrusted(String ip) {
        if (ip == null) {
            return false;
        }
        if (Config.WHITE_IPS.contains(ip)) {
            return true;
        }
        try {
            InetAddress a = InetAddress.getByName(ip);
            if (a.isLoopbackAddress()) {
                return true;
            }
            return Config.TRUST_LOCAL_NETWORK && (a.isSiteLocalAddress() || a.isLinkLocalAddress());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBanned(String ip) {
        if (ip == null) {
            return false;
        }
        if (Config.BLACK_IPS.contains(ip)) {
            return true;
        }
        if (this.isTrusted(ip)) {
            return false;
        }
        Ban b = this.bans.get(ip);
        return b != null && b.isActive(System.currentTimeMillis());
    }

    /** Bans ativos, do que termina antes ao que termina depois. */
    public List<Ban> getActiveBans() {
        long now = System.currentTimeMillis();
        List<Ban> out = new ArrayList<>();
        for (Ban b : this.bans.values()) {
            if (b.isActive(now)) {
                out.add(b);
            }
        }
        out.sort(Comparator.comparingLong(b -> b.expire));
        return out;
    }

    public int getActiveBanCount() {
        long now = System.currentTimeMillis();
        int n = 0;
        for (Ban b : this.bans.values()) {
            if (b.isActive(now)) {
                n++;
            }
        }
        return n;
    }

    // ------------------------------------------------------------------ eventos

    /**
     * Nova conexao. Retorna false (e bane) quando o IP passa do limite de conexoes.
     * Chamado na thread de aceite: so mexe em memoria.
     */
    public boolean onConnection(String ip) {
        if (ip == null || this.isTrusted(ip) || Config.CONNECTION_FLOOD_MAX <= 0) {
            return true;
        }
        long now = System.currentTimeMillis();
        Window w = this.connections.computeIfAbsent(ip, k -> new Window());
        boolean flood;
        synchronized (w) {
            if (now - w.start > Config.CONNECTION_FLOOD_WINDOW) {
                w.start = now;
                w.count = 0;
            }
            w.count++;
            flood = w.count > Config.CONNECTION_FLOOD_MAX;
            if (flood) {
                w.count = 0;
            }
        }
        if (flood) {
            this.ban(ip, "excesso de conexoes");
            return false;
        }
        return true;
    }

    /**
     * Bane o IP pelo proximo nivel (IpBanSteps). Retorna a duracao em ms (0 se o IP e confiavel).
     */
    public long ban(String ip, String reason) {
        if (ip == null || this.isTrusted(ip)) {
            return 0L;
        }
        long now = System.currentTimeMillis();
        long[] steps = Config.IP_BAN_STEPS;
        Ban prev = this.bans.get(ip);
        if (prev != null && prev.isActive(now)) {
            return prev.expire - now; // ja banido
        }
        int level = prev != null && now - prev.lastBan < Config.IP_BAN_RESET ? Math.min(prev.level + 1, steps.length - 1) : 0;
        long duration = steps[Math.min(level, steps.length - 1)];
        Ban b = new Ban(ip, level, now + duration, now, reason);
        this.bans.put(ip, b);
        LoginMetrics.BANS_ISSUED.increment();
        LOG.warn("LoginGuard: " + ip + " banido por " + formatDuration(duration) + " (" + reason + ", nivel " + (level + 1) + ").");
        this.saveAsync(b);
        return duration;
    }

    /** Remove o ban (pela janela). O historico do nivel tambem e apagado. */
    public boolean unban(String ip) {
        Ban b = this.bans.remove(ip);
        IpBanManager.getInstance().reset(ip);
        this.connections.remove(ip);
        if (b != null) {
            LOG.info("LoginGuard: " + ip + " desbanido manualmente.");
            this.deleteAsync(ip);
        }
        return b != null;
    }

    // ------------------------------------------------------------------ persistencia e limpeza

    private void saveAsync(Ban b) {
        if (!this.dbReady) {
            return;
        }
        ThreadPoolManager.getInstance().execute(() -> {
            try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("REPLACE INTO login_ip_bans (ip, ban_level, ban_expire, last_ban, reason) VALUES (?,?,?,?,?)")) {
                ps.setString(1, b.ip);
                ps.setInt(2, b.level);
                ps.setLong(3, b.expire);
                ps.setLong(4, b.lastBan);
                ps.setString(5, b.reason.length() > 64 ? b.reason.substring(0, 64) : b.reason);
                ps.executeUpdate();
            } catch (Exception e) {
                LOG.warn("LoginGuard: falha ao salvar ban de " + b.ip + ": " + e);
            }
        });
    }

    private void deleteAsync(String ip) {
        if (!this.dbReady) {
            return;
        }
        ThreadPoolManager.getInstance().execute(() -> {
            try (Connection con = L2DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM login_ip_bans WHERE ip=?")) {
                ps.setString(1, ip);
                ps.executeUpdate();
            } catch (Exception e) {
                LOG.warn("LoginGuard: falha ao remover ban de " + ip + ": " + e);
            }
        });
    }

    /** Esquece contadores velhos e o historico de quem nao e banido ha mais de IpBanResetHours. */
    private void cleanup() {
        long now = System.currentTimeMillis();
        this.connections.entrySet().removeIf(e -> now - e.getValue().start > Config.CONNECTION_FLOOD_WINDOW * 2);
        List<String> forget = new ArrayList<>();
        for (Ban b : this.bans.values()) {
            if (!b.isActive(now) && now - b.lastBan > Config.IP_BAN_RESET) {
                forget.add(b.ip);
            }
        }
        for (String ip : forget) {
            this.bans.remove(ip);
            this.deleteAsync(ip);
        }
    }

    public static String formatDuration(long ms) {
        long s = ms / 1000L;
        if (s >= 86400L) {
            return (s / 86400L) + "d" + (s % 86400L >= 3600L ? " " + (s % 86400L / 3600L) + "h" : "");
        }
        if (s >= 3600L) {
            return (s / 3600L) + "h" + (s % 3600L >= 60L ? " " + (s % 3600L / 60L) + "min" : "");
        }
        if (s >= 60L) {
            return (s / 60L) + " min";
        }
        return s + " s";
    }
}
