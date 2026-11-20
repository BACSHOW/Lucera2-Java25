package l2.gameserver.network.l2;

import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.utils.ServiceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aviso de conexao ruim (ConnectionWarning): a cada 2 s olha as respostas de ping e a fila de envio de cada jogador.
 * Quando a conexao fica ruim, mostra uma tarja na tela; quando volta ao normal, mostra outra.
 *
 * <ul>
 * <li>ruim: 2 respostas de ping seguidas >= ConnectionWarningPing, ou fila de envio acima de
 * NettySlowClientShedBytes (ou cheia) em 2 verificacoes seguidas. Ping que ainda nao voltou nao conta: cliente
 * minimizado ou carregando para de responder sem a rede estar ruim;</li>
 * <li>normal de novo: ping abaixo de 75% do limite e fila em dia por 6 s seguidos;</li>
 * <li>no maximo um aviso de "ruim" a cada ConnectionWarningCooldown segundos por jogador.</li>
 * </ul>
 *
 * <p>O aviso vai pela mesma conexao, entao chega com o atraso que ela tiver; com NettySlowClientShedding ligado
 * a fila fica curta e isso e coisa de poucos segundos. O pacote do aviso nunca e pulado.</p>
 */
public final class ConnectionQualityMonitor {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionQualityMonitor.class);
    private static final long CHECK_MS = 2000L;
    private static final int BAD_CHECKS_TO_WARN = 2;
    /** Respostas de ping seguidas acima de ConnectionWarningPing para avisar. */
    private static final int HIGH_PINGS_TO_WARN = 2;
    private static final long GOOD_MS_TO_RECOVER = 6000L;
    private static final int WARN_SCREEN_MS = 6000;
    private static final int RECOVER_SCREEN_MS = 4000;

    /** Estado por cliente (guardado no GameClient, so este monitor mexe). */
    public static final class State {
        boolean warned;
        int badChecks;
        long goodSince;
        long lastWarnAt;
        /** Ultima resposta de ping ja contada e quantas respostas seguidas vieram com ping alto. */
        long lastAnswerSeen;
        int highPings;
    }

    public enum Quality {
        GOOD("boa"),
        UNSTABLE("instavel"),
        BAD("ruim");

        private final String label;

        Quality(String label) {
            this.label = label;
        }

        public String label() {
            return this.label;
        }
    }

    private static boolean started;

    private ConnectionQualityMonitor() {
    }

    public static synchronized void start() {
        if (started || !Config.CONNECTION_WARNING) {
            return;
        }
        started = true;
        ThreadPoolManager.getInstance().scheduleAtFixedRate(ConnectionQualityMonitor::checkAll, CHECK_MS, CHECK_MS);
        ServiceLog.section("AVISO DE CONEXÃO")
                .add("Tarja de conexão ruim: ligada (" + (Config.CONNECTION_WARNING_INTERFACE ? "pela interface" : "mensagem do cliente") + ")")
                .add("Dispara com ping >= " + Config.CONNECTION_WARNING_PING_MS + " ms ou fila de envio > "
                        + Config.NETTY_SLOW_CLIENT_SHED_BYTES / 1024 + " KB | no máximo 1 aviso a cada "
                        + Config.CONNECTION_WARNING_COOLDOWN_MS / 1000 + " s")
                .print();
    }

    /** Qualidade atual, usada tambem pelo .ping. */
    public static Quality quality(GameClient client) {
        int ping = client.getLivePing();
        if (lagging(client) || ping >= Config.CONNECTION_WARNING_PING_MS) {
            return Quality.BAD;
        }
        if (ping >= Config.CONNECTION_WARNING_PING_MS / 2) {
            return Quality.UNSTABLE;
        }
        return Quality.GOOD;
    }

    static boolean lagging(GameClient client) {
        NettyGameConnection conn = client.getNettyConnection();
        return conn != null && conn.isLagging();
    }

    private static void checkAll() {
        long now = System.currentTimeMillis();
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player.isInOfflineMode()) {
                continue;
            }
            GameClient client = player.getNetConnection();
            if (client == null || !client.isConnected() || !client.isStateIs(GameClient.GameClientState.IN_GAME)) {
                continue;
            }
            try {
                check(player, client, now);
            } catch (RuntimeException e) {
                LOG.warn("ConnectionWarning: erro ao verificar " + player.getName(), e);
            }
        }
    }

    private static void check(Player player, GameClient client, long now) {
        State st = client.getConnectionState();
        // Ping: conta so respostas que chegaram, e so avisa com 2 seguidas acima do limite. Uma resposta
        // atrasada sozinha costuma ser o cliente parado (minimizado, tela de carregamento), nao a rede.
        long answeredAt = client.getPingAnsweredAt();
        if (answeredAt != 0L && answeredAt != st.lastAnswerSeen) {
            st.lastAnswerSeen = answeredAt;
            st.highPings = client.getPing() >= Config.CONNECTION_WARNING_PING_MS ? st.highPings + 1 : 0;
        }
        boolean lagging = lagging(client);
        boolean bad = lagging || st.highPings >= HIGH_PINGS_TO_WARN;
        boolean good = !lagging && st.highPings == 0 && answeredAt != 0L
                && client.getPing() < Config.CONNECTION_WARNING_PING_MS * 3 / 4;

        if (!st.warned) {
            st.badChecks = bad ? st.badChecks + 1 : 0;
            if (st.badChecks >= BAD_CHECKS_TO_WARN && now - st.lastWarnAt >= Config.CONNECTION_WARNING_COOLDOWN_MS) {
                st.warned = true;
                st.lastWarnAt = now;
                st.goodSince = 0L;
                st.badChecks = 0;
                LOG.info("ConnectionWarning: " + player.getName() + " conexao ruim (" + reason(client, lagging, st) + ").");
                show(player, true);
            }
            return;
        }
        if (!good) {
            st.goodSince = 0L;
            return;
        }
        if (st.goodSince == 0L) {
            st.goodSince = now;
        } else if (now - st.goodSince >= GOOD_MS_TO_RECOVER) {
            st.warned = false;
            st.goodSince = 0L;
            LOG.info("ConnectionWarning: " + player.getName() + " conexao normal de novo (ping " + client.getPing() + " ms, ruim por "
                    + (now - st.lastWarnAt) / 1000L + " s).");
            show(player, false);
        }
    }

    /** Motivo do aviso, para o log: ajuda a separar rede ruim de cliente parado (minimizado, carregando). */
    private static String reason(GameClient client, boolean lagging, State st) {
        StringBuilder sb = new StringBuilder();
        if (st.highPings >= HIGH_PINGS_TO_WARN) {
            sb.append(st.highPings).append(" pings altos seguidos, ultimo ").append(client.getPing()).append(" ms");
        }
        if (lagging) {
            NettyGameConnection conn = client.getNettyConnection();
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append("fila de envio ").append(conn == null ? 0L : conn.pendingWriteBytes() / 1024L).append(" KB");
        }
        long age = client.getPingAgeMs();
        sb.append("; ultima resposta de ping ha ").append(age < 0L ? "-" : String.valueOf(age / 1000L)).append(" s");
        return sb.toString();
    }

    /**
     * Mostra a tarja. Com ConnectionWarningInterface = True vai para a interface personalizada (TutorialViewerWnd
     * repassa "NET_TOAST|STATE=BAD|TEXT=...|" para a OnScreenMessageWnd9, vermelha ou verde); senao usa a
     * mensagem de tela do proprio cliente, que funciona em qualquer interface.
     */
    private static void show(Player player, boolean bad) {
        String text = bad ? Config.CONNECTION_WARNING_BAD_TEXT : Config.CONNECTION_WARNING_GOOD_TEXT;
        if (text == null || text.isEmpty()) {
            return;
        }
        if (Config.CONNECTION_WARNING_INTERFACE) {
            player.sendPacket(new TutorialShowHtml("NET_TOAST|STATE=" + (bad ? "BAD" : "GOOD") + "|TIME=" + (bad ? WARN_SCREEN_MS : RECOVER_SCREEN_MS)
                    + "|TEXT=" + text.replace('|', '/') + "|"));
            return;
        }
        player.sendPacket(new ExShowScreenMessage(text, bad ? WARN_SCREEN_MS : RECOVER_SCREEN_MS, 0, ScreenMessageAlign.TOP_CENTER, true,
                ExShowScreenMessage.STRING_TYPE, -1, false));
    }
}