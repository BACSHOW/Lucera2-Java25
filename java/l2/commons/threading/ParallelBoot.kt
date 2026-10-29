package l2.commons.threading

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

/**
 * Structured-concurrency boot helper para L2JLucera2 (portado do BrProject-Native).
 *
 * Executa um lote de etapas de inicialização independentes concorrentemente com coroutines
 * e aguarda até que todas finalizem (uma barreira segura via [awaitAll]).
 */
object ParallelBoot {
    private val _log: Logger = LoggerFactory.getLogger(ParallelBoot::class.java)

    /** Representa uma etapa de inicialização nomeada. `io = true` roteia para Virtual Threads. */
    class Step @JvmOverloads constructor(
        @JvmField val name: String,
        @JvmField val action: Runnable,
        @JvmField val io: Boolean = false
    )

    @JvmStatic
    fun step(name: String, action: Runnable): Step = Step(name, action, false)

    @JvmStatic
    fun stepIO(name: String, action: Runnable): Step = Step(name, action, true)

    /**
     * Executa [steps] concorrentemente via coroutines, bloqueando até que todos completem.
     * Cada step é medido em tempo; qualquer falha é logada e relançada (fail-fast).
     */
    @JvmStatic
    fun runBatch(label: String, vararg steps: Step) {
        if (steps.isEmpty()) return

        val total = measureTimeMillis {
            runBlocking {
                steps.map { step ->
                    val dispatcher = if (step.io) CoroutinePool.ioDispatcher else CoroutinePool.defaultDispatcher
                    async(dispatcher) {
                        val ms = measureTimeMillis {
                            try {
                                step.action.run()
                            } catch (e: Throwable) {
                                _log.error("Boot step '${step.name}' failed: ${e.message}", e)
                                throw e
                            }
                        }
                        if (ms > 300) {
                            _log.debug("[$label] '${step.name}' finished in ${ms}ms")
                        }
                        step.name to ms
                    }
                }.awaitAll()
            }
        }
        _log.debug("[$label] batch loaded in ${total}ms (${steps.size} coroutine steps).")
    }

    @JvmStatic
    fun shutdown() {
        // Opcional para limpeza após o boot
    }
}
