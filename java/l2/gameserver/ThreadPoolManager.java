package l2.gameserver;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.CoroutinePool;
import l2.commons.threading.RunnableStatsWrapper;
import l2.gameserver.Config;

/**
 * ThreadPoolManager — Façade de alto desempenho delegando para {@link CoroutinePool}.
 * Toda a execução de threads, timers e I/O agora roda sobre Coroutines e Virtual Threads do Java 25.
 */
public class ThreadPoolManager {
    private static final ThreadPoolManager _instance = new ThreadPoolManager();
    private volatile boolean _shutdown = false;

    public static ThreadPoolManager getInstance() {
        return _instance;
    }

    private ThreadPoolManager() {
        // Inicializa o CoroutinePool com base nos parâmetros configurados e otimizados pelo JvmOptimizer
        CoroutinePool.init(Config.SCHEDULED_THREAD_POOL_SIZE, Config.EXECUTOR_THREAD_POOL_SIZE);

        // Purge periódico para manter a memória limpa
        this.scheduleAtFixedRate(CoroutinePool::purge, 300000L, 300000L);
    }

    public boolean isShutdown() {
        return this._shutdown;
    }

    public Runnable wrap(Runnable runnable) {
        return Config.ENABLE_RUNNABLE_STATS ? RunnableStatsWrapper.wrap(runnable) : runnable;
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long delay) {
        return CoroutinePool.schedule(this.wrap(runnable), delay);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period) {
        return CoroutinePool.scheduleAtFixedRate(this.wrap(runnable), initialDelay, period);
    }

    public ScheduledFuture<?> scheduleAtFixedDelay(Runnable runnable, long initialDelay, long delay) {
        return CoroutinePool.scheduleAtFixedDelay(this.wrap(runnable), initialDelay, delay);
    }

    public void execute(Runnable runnable) {
        CoroutinePool.execute(this.wrap(runnable));
    }

    /** Executa tarefas bloqueantes de I/O (disco/banco) diretamente nas Virtual Threads do Java 25. */
    public void executeIO(Runnable runnable) {
        CoroutinePool.executeIO(this.wrap(runnable));
    }

    public void shutdown() throws InterruptedException {
        this._shutdown = true;
        CoroutinePool.shutdown();
    }

    public List<Runnable> shutdownNow() throws InterruptedException {
        this._shutdown = true;
        return CoroutinePool.shutdownNow();
    }

    public CharSequence getStats() {
        return CoroutinePool.getStats();
    }
}
