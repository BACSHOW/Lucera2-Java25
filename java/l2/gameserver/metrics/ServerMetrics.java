package l2.gameserver.metrics;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import l2.gameserver.model.GameObjectsStorage;

/**
 * Metricas do GameServer, coletadas a cada segundo com custo desprezivel (contadores LongAdder).
 *
 * <p>Pontos de coleta: pacotes recebidos (NettyGameDispatcher), pacotes enviados (L2PacketEncoder),
 * tempo de pensamento da IA dos NPCs (DefaultAI.onEvtThink) e tempo de busca de caminho
 * (GeoMove.findMovePath). O resto vem da JVM.</p>
 *
 * <p>Guarda a ultima hora em memoria (para a janela do servidor) e sabe se escrever no formato texto do
 * Prometheus ({@link #toPrometheus()}), para quando o Grafana for ligado.</p>
 */
public final class ServerMetrics {
    /** Segundos de historico mantidos em memoria. */
    public static final int HISTORY_SECONDS = 3600;

    private static final LongAdder PACKETS_IN = new LongAdder();
    private static final LongAdder BYTES_IN = new LongAdder();
    private static final LongAdder PACKETS_OUT = new LongAdder();
    private static final LongAdder BYTES_OUT = new LongAdder();

    /** Eventos das correcoes de movimento, geodata e AutoFarm (contados por segundo e por minuto). */
    public enum Counter {
        VP_GREEN("ValidatePosition: zona verde (posicao do cliente aceita)"),
        VP_YELLOW("ValidatePosition: zona amarela (correcao suave)"),
        VP_RED("ValidatePosition: zona vermelha (salto)"),
        VP_RED_LAG("Zona vermelha com a conexao atrasada (nao conta para a cidade)"),
        ARRIVED_BLOCKED("Bloqueio ao andar (parede/corpos)"),
        CLIENT_SYNC_WAIT("AutoFarm: espera pelo cliente antes do golpe"),
        CLIENT_SYNC_TIMEOUT("AutoFarm: espera esgotada (golpe sem o cliente)"),
        PATH_NBA_SMOOTH("Caminho NBA* suavizado"),
        PATH_NBA_RAW("Caminho NBA* bruto (fallback 1)"),
        PATH_CLASSIC("Caminho classico (fallback 2)"),
        PATH_NONE("Sem caminho possivel"),
        SMOOTH_CORNERS("Caminhos de NPC com quinas suavizadas"),
        NPC_RETURN_HOME("Monstros que voltaram ao spawn"),
        NPC_RETURN_HOME_PATHFAIL("Voltaram ao spawn por falha de caminho"),
        PATH_BUFFERS_EXHAUSTED("Buffers do pathfinding esgotados"),
        NPC_INFO_DROPPED("NPCs nao enviados ao cliente (filtro de raio)"),
        NET_UNWRITABLE("Conexoes que ficaram cheias (fila de envio acima do limite)"),
        NET_SHED("Pacotes visuais pulados para clientes com a conexao cheia"),
        NET_RESYNC("Ressincronizacoes depois de a conexao esvaziar");

        public final String label;

        Counter(String label) {
            this.label = label;
        }
    }

    private static final Counter[] COUNTERS = Counter.values();
    private static final LongAdder[] COUNTS = new LongAdder[COUNTERS.length];
    private static final long[] COUNTS_PREV = new long[COUNTERS.length];
    /** Ultimos 60 segundos de cada contador, para a soma "por minuto". */
    private static final long[][] MINUTE_RING = new long[60][COUNTERS.length];
    private static int minuteNext;

    static {
        for (int i = 0; i < COUNTS.length; i++) {
            COUNTS[i] = new LongAdder();
        }
    }

    public static void count(Counter c) {
        COUNTS[c.ordinal()].increment();
    }

    /** Tempo de pensamento da IA dos NPCs. */
    public static final Timing AI_THINK = new Timing();
    /** Tempo de cada busca de caminho (geodata). */
    public static final Timing PATHFIND = new Timing();

    private static final List<Consumer<Snapshot>> LISTENERS = new CopyOnWriteArrayList<>();
    private static final Object LOCK = new Object();
    private static volatile ScheduledExecutorService sampler;
    private static ScheduledFuture<?> task;

    // historico circular (um ponto por segundo)
    private static final Snapshot[] HISTORY = new Snapshot[HISTORY_SECONDS];
    private static int historyNext;
    private static int historySize;
    private static volatile Snapshot last;

    private static long prevPacketsIn, prevBytesIn, prevPacketsOut, prevBytesOut, prevGcCount, prevGcTime;
    private static long prevSampleNanos;
    private static long totalPacketsIn, totalPacketsOut;

    private ServerMetrics() {
    }

    // ------------------------------------------------------------------ coleta (chamadas no codigo do jogo)

    public static void packetIn(int bytes) {
        PACKETS_IN.increment();
        BYTES_IN.add(bytes);
    }

    public static void packetOut(int bytes) {
        PACKETS_OUT.increment();
        BYTES_OUT.add(bytes);
    }

    /** Duracao de um trecho: soma, quantidade e pior caso do ultimo segundo. */
    public static final class Timing {
        private final LongAdder count = new LongAdder();
        private final LongAdder totalNanos = new LongAdder();
        private final AtomicLong maxNanos = new AtomicLong();

        public void record(long nanos) {
            if (nanos < 0L) {
                return;
            }
            this.count.increment();
            this.totalNanos.add(nanos);
            long max = this.maxNanos.get();
            while (nanos > max && !this.maxNanos.compareAndSet(max, nanos)) {
                max = this.maxNanos.get();
            }
        }

        /** Le e zera: {quantidade, total ns, max ns}. */
        long[] drain() {
            return new long[]{this.count.sumThenReset(), this.totalNanos.sumThenReset(), this.maxNanos.getAndSet(0L)};
        }
    }

    // ------------------------------------------------------------------ amostra

    /** Um segundo de metricas. Taxas sao por segundo; tempos em milissegundos. */
    public static final class Snapshot {
        public final long timeMillis;
        public final long uptimeMillis;
        public final int playersOnline;
        public final int offlineTraders;
        public final double packetsInPerSec;
        public final double packetsOutPerSec;
        public final double kbInPerSec;
        public final double kbOutPerSec;
        public final long totalPacketsIn;
        public final long totalPacketsOut;
        public final long aiThinksPerSec;
        public final double aiAvgMs;
        public final double aiMaxMs;
        public final long pathfindsPerSec;
        public final double pathfindAvgMs;
        public final double pathfindMaxMs;
        public final long heapUsedMb;
        public final long heapMaxMb;
        public final long nonHeapUsedMb;
        public final double processCpuPercent;
        public final int threads;
        public final int deadlockedThreads;
        public final long gcPerSec;
        public final long gcMsPerSec;
        /** Eventos de cada {@link Counter} no ultimo segundo, nos ultimos 60 s e desde o boot. */
        public final long[] countPerSec;
        public final long[] countPerMin;
        public final long[] countTotal;
        /** Fila de envio: conexoes cheias agora, maior fila (KB) e conexoes com //netslow. */
        public final int netFullNow;
        public final long netMaxPendingKb;
        public final int netSlowedNow;

        public long perMin(Counter c) {
            return this.countPerMin[c.ordinal()];
        }

        public long total(Counter c) {
            return this.countTotal[c.ordinal()];
        }

        Snapshot(long timeMillis, long uptimeMillis, int playersOnline, int offlineTraders, double packetsInPerSec,
                double packetsOutPerSec, double kbInPerSec, double kbOutPerSec, long totalPacketsIn, long totalPacketsOut,
                long aiThinksPerSec, double aiAvgMs, double aiMaxMs, long pathfindsPerSec, double pathfindAvgMs,
                double pathfindMaxMs, long heapUsedMb, long heapMaxMb, long nonHeapUsedMb, double processCpuPercent,
                int threads, int deadlockedThreads, long gcPerSec, long gcMsPerSec, long[] countPerSec,
                long[] countPerMin, long[] countTotal, long[] net) {
            this.netFullNow = (int) net[0];
            this.netMaxPendingKb = net[1] / 1024L;
            this.netSlowedNow = (int) net[2];
            this.countPerSec = countPerSec;
            this.countPerMin = countPerMin;
            this.countTotal = countTotal;
            this.timeMillis = timeMillis;
            this.uptimeMillis = uptimeMillis;
            this.playersOnline = playersOnline;
            this.offlineTraders = offlineTraders;
            this.packetsInPerSec = packetsInPerSec;
            this.packetsOutPerSec = packetsOutPerSec;
            this.kbInPerSec = kbInPerSec;
            this.kbOutPerSec = kbOutPerSec;
            this.totalPacketsIn = totalPacketsIn;
            this.totalPacketsOut = totalPacketsOut;
            this.aiThinksPerSec = aiThinksPerSec;
            this.aiAvgMs = aiAvgMs;
            this.aiMaxMs = aiMaxMs;
            this.pathfindsPerSec = pathfindsPerSec;
            this.pathfindAvgMs = pathfindAvgMs;
            this.pathfindMaxMs = pathfindMaxMs;
            this.heapUsedMb = heapUsedMb;
            this.heapMaxMb = heapMaxMb;
            this.nonHeapUsedMb = nonHeapUsedMb;
            this.processCpuPercent = processCpuPercent;
            this.threads = threads;
            this.deadlockedThreads = deadlockedThreads;
            this.gcPerSec = gcPerSec;
            this.gcMsPerSec = gcMsPerSec;
        }
    }

    /** Liga a amostragem de 1 segundo (idempotente). */
    public static void start() {
        synchronized (LOCK) {
            if (sampler != null) {
                return;
            }
            sampler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "ServerMetrics");
                t.setDaemon(true);
                return t;
            });
            prevSampleNanos = System.nanoTime();
            task = sampler.scheduleAtFixedRate(ServerMetrics::sampleSafe, 1L, 1L, TimeUnit.SECONDS);
        }
    }

    public static void stop() {
        synchronized (LOCK) {
            if (task != null) {
                task.cancel(false);
            }
            if (sampler != null) {
                sampler.shutdownNow();
            }
            sampler = null;
            task = null;
        }
    }

    public static void addListener(Consumer<Snapshot> listener) {
        LISTENERS.add(listener);
    }

    public static Snapshot last() {
        return last;
    }

    /** Copia do historico, do mais antigo ao mais novo, limitada aos ultimos {@code seconds}. */
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
            for (Consumer<Snapshot> l : LISTENERS) {
                try {
                    l.accept(s);
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
            // metrica nunca derruba o servidor
        }
    }

    private static Snapshot sample() {
        long now = System.nanoTime();
        double secs = Math.max(0.001, (now - prevSampleNanos) / 1.0E9);
        prevSampleNanos = now;

        long pin = PACKETS_IN.sum(), bin = BYTES_IN.sum(), pout = PACKETS_OUT.sum(), bout = BYTES_OUT.sum();
        double pinRate = (pin - prevPacketsIn) / secs, poutRate = (pout - prevPacketsOut) / secs;
        double kbin = (bin - prevBytesIn) / secs / 1024.0, kbout = (bout - prevBytesOut) / secs / 1024.0;
        prevPacketsIn = pin;
        prevBytesIn = bin;
        prevPacketsOut = pout;
        prevBytesOut = bout;
        totalPacketsIn = pin;
        totalPacketsOut = pout;

        long[] perSec = new long[COUNTERS.length];
        long[] total = new long[COUNTERS.length];
        long[] perMin = new long[COUNTERS.length];
        for (int i = 0; i < COUNTERS.length; i++) {
            total[i] = COUNTS[i].sum();
            perSec[i] = total[i] - COUNTS_PREV[i];
            COUNTS_PREV[i] = total[i];
        }
        MINUTE_RING[minuteNext] = perSec;
        minuteNext = (minuteNext + 1) % MINUTE_RING.length;
        for (long[] sec : MINUTE_RING) {
            for (int i = 0; i < perMin.length; i++) {
                perMin[i] += sec[i];
            }
        }

        long[] ai = AI_THINK.drain();
        long[] pf = PATHFIND.drain();

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        long heapMax = heap.getMax() > 0 ? heap.getMax() : heap.getCommitted();

        double cpu = -1.0;
        java.lang.management.OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            double v = sunOs.getProcessCpuLoad();
            cpu = v < 0 ? -1.0 : v * 100.0;
        }

        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        long[] dead = threads.findDeadlockedThreads();

        long gcCount = 0L, gcTime = 0L;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcCount += Math.max(0L, gc.getCollectionCount());
            gcTime += Math.max(0L, gc.getCollectionTime());
        }
        long gcDelta = prevGcCount == 0L && prevGcTime == 0L ? 0L : gcCount - prevGcCount;
        long gcMs = prevGcCount == 0L && prevGcTime == 0L ? 0L : gcTime - prevGcTime;
        prevGcCount = gcCount;
        prevGcTime = gcTime;

        int all = 0, offline = 0;
        try {
            all = GameObjectsStorage.getAllPlayersCount();
            offline = GameObjectsStorage.getAllOfflineCount();
        } catch (Throwable ignored) {
            // durante o boot o storage ainda pode nao existir
        }

        return new Snapshot(System.currentTimeMillis(), ManagementFactory.getRuntimeMXBean().getUptime(),
                Math.max(0, all - offline), offline, pinRate, poutRate, kbin, kbout, totalPacketsIn, totalPacketsOut,
                ai[0], ai[0] == 0 ? 0.0 : ai[1] / (double) ai[0] / 1.0E6, ai[2] / 1.0E6,
                pf[0], pf[0] == 0 ? 0.0 : pf[1] / (double) pf[0] / 1.0E6, pf[2] / 1.0E6,
                heap.getUsed() >> 20, heapMax >> 20, nonHeap.getUsed() >> 20, cpu,
                threads.getThreadCount(), dead == null ? 0 : dead.length, gcDelta, gcMs, perSec, perMin, total, netQueue());
    }

    /** Quem informa a fila de envio (o NettyGameServer registra ao ligar); sem Netty fica zerado. */
    private static volatile Supplier<long[]> netQueueProbe;

    public static void setNetQueueProbe(Supplier<long[]> probe) {
        netQueueProbe = probe;
    }

    private static long[] netQueue() {
        Supplier<long[]> p = netQueueProbe;
        try {
            return p == null ? new long[3] : p.get();
        } catch (Throwable t) {
            return new long[3];
        }
    }

    // ------------------------------------------------------------------ Prometheus

    /** Ultima amostra no formato texto do Prometheus (para um futuro endpoint HTTP / Grafana). */
    public static String toPrometheus() {
        Snapshot s = last;
        StringBuilder sb = new StringBuilder(2048);
        if (s == null) {
            return "";
        }
        gauge(sb, "l2_players_online", "Jogadores conectados", s.playersOnline);
        gauge(sb, "l2_offline_traders", "Lojas offline", s.offlineTraders);
        counter(sb, "l2_packets_in_total", "Pacotes recebidos", s.totalPacketsIn);
        counter(sb, "l2_packets_out_total", "Pacotes enviados", s.totalPacketsOut);
        gauge(sb, "l2_packets_in_per_second", "Pacotes recebidos por segundo", s.packetsInPerSec);
        gauge(sb, "l2_packets_out_per_second", "Pacotes enviados por segundo", s.packetsOutPerSec);
        gauge(sb, "l2_ai_think_avg_ms", "Tempo medio de pensamento da IA", s.aiAvgMs);
        gauge(sb, "l2_ai_think_max_ms", "Pior tempo de pensamento da IA no ultimo segundo", s.aiMaxMs);
        gauge(sb, "l2_pathfind_avg_ms", "Tempo medio de busca de caminho", s.pathfindAvgMs);
        gauge(sb, "l2_pathfind_max_ms", "Pior busca de caminho no ultimo segundo", s.pathfindMaxMs);
        gauge(sb, "l2_jvm_heap_used_mb", "Heap usado", s.heapUsedMb);
        gauge(sb, "l2_jvm_heap_max_mb", "Heap maximo", s.heapMaxMb);
        gauge(sb, "l2_process_cpu_percent", "CPU do processo", s.processCpuPercent);
        gauge(sb, "l2_jvm_threads", "Threads", s.threads);
        gauge(sb, "l2_jvm_deadlocked_threads", "Threads em deadlock", s.deadlockedThreads);
        gauge(sb, "l2_uptime_seconds", "Tempo ligado", s.uptimeMillis / 1000L);
        gauge(sb, "l2_net_full_connections", "Conexoes com a fila de envio cheia agora", s.netFullNow);
        gauge(sb, "l2_net_max_pending_kb", "Maior fila de envio de um cliente (KB)", s.netMaxPendingKb);
        for (Counter c : COUNTERS) {
            counter(sb, "l2_" + c.name().toLowerCase(java.util.Locale.ROOT) + "_total", c.label, s.total(c));
        }
        return sb.toString();
    }

    private static void gauge(StringBuilder sb, String name, String help, double value) {
        sb.append("# HELP ").append(name).append(' ').append(help).append('\n');
        sb.append("# TYPE ").append(name).append(" gauge\n");
        sb.append(name).append(' ').append(value).append('\n');
    }

    private static void counter(StringBuilder sb, String name, String help, long value) {
        sb.append("# HELP ").append(name).append(' ').append(help).append('\n');
        sb.append("# TYPE ").append(name).append(" counter\n");
        sb.append(name).append(' ').append(value).append('\n');
    }
}
