package l2.commons.threading

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.LinkedList
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext

/**
 * Backbone de Structured Concurrency para L2JLucera2 (padrão BrProject-Native).
 * Integra Coroutines Kotlin, Virtual Threads (Java 25) e Executors estruturados.
 */
object CoroutinePool {
    private val _log: Logger = LoggerFactory.getLogger(CoroutinePool::class.java)

    private val backgroundScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var virtualExecutor: ExecutorService? = null
    private var scheduledPool: ScheduledThreadPoolExecutor? = null
    private var instantPool: ThreadPoolExecutor? = null

    private val tasksSubmitted = AtomicLong()
    private val tasksCompleted = AtomicLong()

    private val MAX_DELAY: Long = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2L

    /** Dispatcher para tarefas ligadas à CPU (parsers XML, IA de combate). */
    @JvmStatic
    val defaultDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    /** Dispatcher para I/O bloqueante (arquivos, banco de dados) via Virtual Threads do Java 25. */
    @JvmStatic
    val ioDispatcher: CoroutineDispatcher
        get() = virtualExecutor?.asCoroutineDispatcher() ?: Dispatchers.IO

    init {
        try {
            // Java 25 Virtual Threads executor para I/O leve e concorrente
            virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()
            _log.info("CoroutinePool: Java 25 Virtual-thread I/O executor initialized.")
        } catch (e: Throwable) {
            _log.warn("CoroutinePool: Could not initialize VirtualThread executor, falling back to Dispatchers.IO: {}", e.message)
        }
    }

    @JvmStatic
    fun init(scheduledThreads: Int, instantThreads: Int) {
        scheduledPool = ScheduledThreadPoolExecutor(
            scheduledThreads,
            PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY),
            LoggingRejectedExecutionHandler()
        ).apply {
            removeOnCancelPolicy = true
            prestartAllCoreThreads()
        }

        instantPool = ThreadPoolExecutor(
            instantThreads, Int.MAX_VALUE, 60L, TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            PriorityThreadFactory("InstantThreadPool", Thread.NORM_PRIORITY),
            LoggingRejectedExecutionHandler()
        ).apply {
            prestartAllCoreThreads()
        }

        _log.info("CoroutinePool: Structured pools active (Scheduled: {} threads, Instant: {} threads, Virtual Threads: ACTIVE)", scheduledThreads, instantThreads)
    }

    private fun validate(delay: Long): Long = delay.coerceIn(0L, MAX_DELAY)

    @JvmStatic
    fun schedule(r: Runnable, delay: Long): ScheduledFuture<*>? {
        val pool = scheduledPool ?: run { r.run(); return null }
        return try {
            tasksSubmitted.incrementAndGet()
            pool.schedule(wrap(r), validate(delay), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            _log.error("CoroutinePool#schedule failed: {}", e.message, e)
            null
        }
    }

    @JvmStatic
    fun scheduleAtFixedRate(r: Runnable, initialDelay: Long, period: Long): ScheduledFuture<*>? {
        val pool = scheduledPool ?: return null
        return try {
            tasksSubmitted.incrementAndGet()
            pool.scheduleAtFixedRate(wrap(r), validate(initialDelay), validate(period), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            _log.error("CoroutinePool#scheduleAtFixedRate failed: {}", e.message, e)
            null
        }
    }

    @JvmStatic
    fun scheduleAtFixedDelay(r: Runnable, initialDelay: Long, delay: Long): ScheduledFuture<*>? {
        val pool = scheduledPool ?: return null
        return try {
            tasksSubmitted.incrementAndGet()
            pool.scheduleWithFixedDelay(wrap(r), validate(initialDelay), validate(delay), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            _log.error("CoroutinePool#scheduleAtFixedDelay failed: {}", e.message, e)
            null
        }
    }

    @JvmStatic
    fun execute(r: Runnable) {
        val pool = instantPool ?: run { r.run(); return }
        try {
            tasksSubmitted.incrementAndGet()
            pool.execute(wrap(r))
        } catch (e: Exception) {
            _log.error("CoroutinePool#execute failed: {}", e.message, e)
        }
    }

    /** Executa uma tarefa bloqueante no pool de virtual threads de I/O (Java 25). */
    @JvmStatic
    fun executeIO(r: Runnable) {
        val exec = virtualExecutor ?: run { execute(r); return }
        exec.execute(wrap(r))
    }

    /** Dispara uma coroutine em background sob SupervisorJob. */
    @JvmStatic
    fun launchBackground(context: CoroutineContext = Dispatchers.Default, block: suspend () -> Unit): Job =
        backgroundScope.launch(context) {
            try {
                block()
            } catch (e: Exception) {
                _log.error("Background coroutine failed: {}", e.message, e)
            }
        }

    @JvmStatic
    fun purge() {
        scheduledPool?.purge()
        instantPool?.purge()
    }

    @JvmStatic
    fun shutdown() {
        try {
            backgroundScope.cancel()
            scheduledPool?.shutdown()
            instantPool?.shutdown()
            virtualExecutor?.shutdownNow()
            _log.info("CoroutinePool: Shutdown complete.")
        } catch (t: Throwable) {
            _log.warn("CoroutinePool: Problem at shutdown: {}", t.message)
        }
    }

    @JvmStatic
    fun shutdownNow(): List<Runnable> {
        val list = LinkedList<Runnable>()
        try {
            backgroundScope.cancel()
            scheduledPool?.shutdownNow()?.let { list.addAll(it) }
            instantPool?.shutdownNow()?.let { list.addAll(it) }
            virtualExecutor?.shutdownNow()?.let { list.addAll(it) }
        } catch (t: Throwable) {
            _log.warn("CoroutinePool: Problem at shutdownNow: {}", t.message)
        }
        return list
    }

    @JvmStatic
    fun getStats(): CharSequence {
        val sb = StringBuilder()
        sb.append("CoroutinePool / Virtual Threads Metrics (Java 25)\n")
        sb.append("=================================================\n")
        sb.append("\tTasks Submitted: ....... ").append(tasksSubmitted.get()).append("\n")
        sb.append("\tTasks Completed: ....... ").append(tasksCompleted.get()).append("\n")
        sb.append("\tScheduled Active: ...... ").append(scheduledPool?.activeCount ?: 0).append("\n")
        sb.append("\tScheduled Pool Size: ... ").append(scheduledPool?.poolSize ?: 0).append("\n")
        sb.append("\tScheduled Queue: ....... ").append(scheduledPool?.queue?.size ?: 0).append("\n")
        sb.append("\tInstant Active: ........ ").append(instantPool?.activeCount ?: 0).append("\n")
        sb.append("\tInstant Pool Size: ..... ").append(instantPool?.poolSize ?: 0).append("\n")
        sb.append("\tInstant Queue: ......... ").append(instantPool?.queue?.size ?: 0).append("\n")
        return sb
    }

    private fun wrap(r: Runnable): Runnable = Runnable {
        try {
            r.run()
        } finally {
            tasksCompleted.incrementAndGet()
        }
    }
}
