package l2.authserver;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import l2.authserver.network.gamecomm.GameServer;

/**
 * Metricas do login, amostradas a cada segundo (contadores LongAdder, custo desprezivel).
 * Guarda a ultima hora para a janela; as taxas "por minuto" sao a soma dos ultimos 60 segundos.
 */
public final class LoginMetrics {
    public static final int HISTORY_SECONDS = 3600;

    public static final LongAdder CONNECTIONS_ACCEPTED = new LongAdder();
    public static final LongAdder CONNECTIONS_REFUSED = new LongAdder();
    public static final LongAdder LOGINS_OK = new LongAdder();
    public static final LongAdder LOGINS_FAILED = new LongAdder();
    public static final LongAdder ACCOUNTS_CREATED = new LongAdder();
    public static final LongAdder BANS_ISSUED = new LongAdder();

    /** Um segundo de metricas. "PerMin" = soma dos ultimos 60 s. */
    public static final class Snapshot {
        public final long timeMillis;
        public final long uptimeMillis;
        public final long accepted, refused, ok, failed, created, bans;          // neste segundo
        public final long acceptedPerMin, refusedPerMin, okPerMin, failedPerMin, createdPerMin, bansPerMin;
        public final long totalOk, totalFailed, totalCreated, totalBans, totalRefused;
        public final int activeBans;
        public final int gameServersOnline;
        public final int gameServersTotal;
        public final int playersOnline;
        public final long heapUsedMb, heapMaxMb;
        public final double processCpuPercent;

        Snapshot(long timeMillis, long uptimeMillis, long[] sec, long[] perMin, long[] totals, int activeBans,
                int gsOnline, int gsTotal, int players, long heapUsedMb, long heapMaxMb, double cpu) {
            this.timeMillis = timeMillis;
            this.uptimeMillis = uptimeMillis;
            this.accepted = sec[0];
            this.refused = sec[1];
            this.ok = sec[2];
            this.failed = sec[3];
            this.created = sec[4];
            this.bans = sec[5];
            this.acceptedPerMin = perMin[0];
            this.refusedPerMin = perMin[1];
            this.okPerMin = perMin[2];
            this.failedPerMin = perMin[3];
            this.createdPerMin = perMin[4];
            this.bansPerMin = perMin[5];
            this.totalRefused = totals[1];
            this.totalOk = totals[2];
            this.totalFailed = totals[3];
            this.totalCreated = totals[4];
            this.totalBans = totals[5];
            this.activeBans = activeBans;
            this.gameServersOnline = gsOnline;
            this.gameServersTotal = gsTotal;
            this.playersOnline = players;
            this.heapUsedMb = heapUsedMb;
            this.heapMaxMb = heapMaxMb;
            this.processCpuPercent = cpu;
        }
    }

    private static final LongAdder[] COUNTERS = {CONNECTIONS_ACCEPTED, CONNECTIONS_REFUSED, LOGINS_OK, LOGINS_FAILED, ACCOUNTS_CREATED, BANS_ISSUED};
    private static final long[] PREV = new long[COUNTERS.length];
    private static final long[][] LAST_MINUTE = new long[60][COUNTERS.length];
    private static int minuteIdx;
    private static final Snapshot[] HISTORY = new Snapshot[HISTORY_SECONDS];
    private static int historyNext;
    private static int historySize;
    private static volatile Snapshot last;
    private static ScheduledExecutorService sampler;
    /**
     * So depois do Config e do banco: a janela abre antes, e consultar o GameServerManager cedo demais
     * o criaria sem configuracao (lista de servidores vazia).
     */
    private static volatile boolean serverReady;

    private LoginMetrics() {
    }

    public static synchronized void start() {
        if (sampler != null) {
            return;
        }
        sampler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "LoginMetrics");
            t.setDaemon(true);
            return t;
        });
        sampler.scheduleAtFixedRate(LoginMetrics::sampleSafe, 1L, 1L, TimeUnit.SECONDS);
    }

    public static void setServerReady() {
        serverReady = true;
    }

    public static boolean isServerReady() {
        return serverReady;
    }

    public static Snapshot last() {
        return last;
    }

    public static Snapshot[] history(int seconds) {
        synchronized (HISTORY) {
            int n = Math.min(Math.max(0, seconds), historySize);
            Snapshot[] out = new Snapshot[n];
            int start = (historyNext - n + HISTORY_SECONDS) % HISTORY_SECONDS;
            for (int i = 0; i < n; i++) {
                out[i] = HISTORY[(start + i) % HISTORY_SECONDS];
            }
            return out;
        }
    }

    private static void sampleSafe() {
        try {
            Snapshot s = sample();
            last = s;
            synchronized (HISTORY) {
                HISTORY[historyNext] = s;
                historyNext = (historyNext + 1) % HISTORY_SECONDS;
                historySize = Math.min(HISTORY_SECONDS, historySize + 1);
            }
        } catch (Throwable ignored) {
            // metrica nunca derruba o login
        }
    }

    private static Snapshot sample() {
        long[] sec = new long[COUNTERS.length];
        long[] totals = new long[COUNTERS.length];
        long[] perMin = new long[COUNTERS.length];
        for (int i = 0; i < COUNTERS.length; i++) {
            long v = COUNTERS[i].sum();
            totals[i] = v;
            sec[i] = v - PREV[i];
            PREV[i] = v;
        }
        LAST_MINUTE[minuteIdx] = sec;
        minuteIdx = (minuteIdx + 1) % 60;
        for (long[] row : LAST_MINUTE) {
            for (int i = 0; i < perMin.length; i++) {
                perMin[i] += row[i];
            }
        }

        int gsOnline = 0, gsTotal = 0, players = 0;
        try {
            for (GameServer gs : serverReady ? GameServerManager.getInstance().getGameServers() : new GameServer[0]) {
                gsTotal++;
                if (gs.isAuthed()) {
                    gsOnline++;
                    players += gs.getOnline();
                }
            }
        } catch (Throwable ignored) {
            // durante o boot o gerenciador ainda pode nao existir
        }

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long heapMax = heap.getMax() > 0 ? heap.getMax() : heap.getCommitted();
        double cpu = -1.0;
        java.lang.management.OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            double v = sunOs.getProcessCpuLoad();
            cpu = v < 0 ? -1.0 : v * 100.0;
        }
        return new Snapshot(System.currentTimeMillis(), ManagementFactory.getRuntimeMXBean().getUptime(), sec, perMin, totals,
                LoginGuard.getInstance().getActiveBanCount(), gsOnline, gsTotal, players, heap.getUsed() >> 20, heapMax >> 20, cpu);
    }
}
