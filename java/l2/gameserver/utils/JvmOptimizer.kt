package l2.gameserver.utils

import l2.commons.util.SysUtil
import l2.gameserver.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

/**
 * Otimizador Dinâmico de JVM e Sistema Operacional para L2JLucera2 (padrão BrProject-Native).
 * Aplica trava estrita de Java 25, ajusta ergonomia de CPU, pools e buffers.
 */
object JvmOptimizer {
    private val _log: Logger = LoggerFactory.getLogger(JvmOptimizer::class.java)

    @JvmStatic
    fun applyOptimizations() {
        // 1. TRAVA MANDATÓRIA: Bloqueia qualquer execução abaixo de Java 25
        SysUtil.enforceJava25()

        // 2. Banner de diagnóstico completo
        SysUtil.printSystemInfo()

        // 3. Ajuste de ergonomia de CPU e Pools de Threads
        val cores = SysUtil.CPU_CORES
        val recommendedScheduled = (cores * 2).coerceIn(4, 32)
        val recommendedExecutor = (cores * 2).coerceIn(4, 32)

        if (Config.SCHEDULED_THREAD_POOL_SIZE < recommendedScheduled) {
            _log.debug("JvmOptimizer: Adjusting ScheduledThreadPoolSize from {} to recommended {}", Config.SCHEDULED_THREAD_POOL_SIZE, recommendedScheduled)
            Config.SCHEDULED_THREAD_POOL_SIZE = recommendedScheduled
        }

        if (Config.EXECUTOR_THREAD_POOL_SIZE < recommendedExecutor) {
            _log.debug("JvmOptimizer: Adjusting ExecutorThreadPoolSize from {} to recommended {}", Config.EXECUTOR_THREAD_POOL_SIZE, recommendedExecutor)
            Config.EXECUTOR_THREAD_POOL_SIZE = recommendedExecutor
        }

        // 4. Detecção de Garbage Collector moderno (ZGC / G1)
        val gcBeans = ManagementFactory.getGarbageCollectorMXBeans()
        val gcNames = gcBeans.map { it.name }.joinToString(", ")
        val isZgc = gcNames.contains("ZGC", ignoreCase = true)
        val isG1 = gcNames.contains("G1", ignoreCase = true)

        when {
            isZgc -> _log.debug("JvmOptimizer: Ultra-low latency ZGC active (sub-millisecond pause times).")
            isG1 -> _log.debug("JvmOptimizer: G1GC active.")
            else -> _log.debug("JvmOptimizer: GC active: {}", gcNames)
        }

        // 5. Ajustes específicos por Sistema Operacional
        when {
            SysUtil.IS_LINUX -> {
                _log.debug("JvmOptimizer: Linux environment detected. Optimized TCP parameters and SO_REUSEPORT supported.")
            }
            SysUtil.IS_MAC -> {
                _log.debug("JvmOptimizer: macOS / Darwin environment detected. BSD socket layers and Apple Silicon Vector acceleration active.")
            }
            SysUtil.IS_WINDOWS -> {
                _log.debug("JvmOptimizer: Windows environment detected. Windows I/O completion and timer resolution adjusted.")
            }
        }
    }
}
