package l2.gameserver.network.l2.netty.disruptor.topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.LogicalProcessor;
import oshi.hardware.CentralProcessor.PhysicalProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor de autotuning e inspeção de topologia de hardware (Mechanical Sympathy).
 * <p>
 * Projetado para eliminar contenção de locks, falsos compartilhamentos e jitter
 * de escalonamento do sistema operacional sob um orçamento de 8.33ms (120 Hz).
 * <p>
 * Regras implementadas:
 * 1. Eliminação de SMT/Hyper-Threading (apenas núcleos físicos reais).
 * 2. Segregação de Arquiteturas Heterogêneas (Performance Cores vs Efficiency Cores).
 * 3. Reserva estratégica de Housekeeping (SO/GC/JIT) e Netty I/O.
 * 4. Truncamento da quantidade de workers para a maior potência de 2 elegível.
 * 5. Fallback seguro para ambientes virtuais restritos (1 ou 2 vCPUs).
 */
public final class CoreAutoTuner {
    private static final Logger _log = LoggerFactory.getLogger(CoreAutoTuner.class);

    // Cache thread-safe da configuração resolvida
    private static volatile CpuTopologyConfig CACHED_CONFIG = null;

    private CoreAutoTuner() {
        // Utilitário estático
    }

    /**
     * Resolve ou recupera a configuração de topologia de CPU do sistema host.
     * Método thread-safe com double-checked locking.
     */
    public static CpuTopologyConfig resolve() {
        CpuTopologyConfig config = CACHED_CONFIG;
        if (config == null) {
            synchronized (CoreAutoTuner.class) {
                config = CACHED_CONFIG;
                if (config == null) {
                    config = inspectAndTune();
                    CACHED_CONFIG = config;
                }
            }
        }
        return config;
    }

    /**
     * Força a reavaliação da topologia (útil para testes unitários ou recálculo dinâmico).
     */
    public static synchronized CpuTopologyConfig reevaluate() {
        CACHED_CONFIG = inspectAndTune();
        return CACHED_CONFIG;
    }

    private static CpuTopologyConfig inspectAndTune() {
        // 1. Em macOS / Darwin, detecção nativa direta e limpa via sysctl sem ruído de JNA
        if (l2.commons.util.SysUtil.IS_MAC) {
            CpuTopologyConfig macConfig = tryMacSysctlTopology();
            if (macConfig != null) {
                return macConfig;
            }
        }
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            return calculateTopology(processor);
        } catch (Throwable t) {
            CpuTopologyConfig nativeConfig = tryPlatformNativeFallback();
            if (nativeConfig != null) {
                return nativeConfig;
            }
            return buildFallbackConfig("Fallback JVM (Falha: " + t.getClass().getSimpleName() + ")");
        }
    }

    private static CpuTopologyConfig tryPlatformNativeFallback() {
        try {
            // 1. Detecção especializada para macOS / Apple Silicon via sysctl
            if (l2.commons.util.SysUtil.IS_MAC) {
                CpuTopologyConfig macConfig = tryMacSysctlTopology();
                if (macConfig != null) {
                    return macConfig;
                }
            }

            // 2. Detecção deterministica multiplataforma via Runtime JVM
            int available = Runtime.getRuntime().availableProcessors();
            if (available > 2) {
                int housekeepingCoreId = 0;
                int nettyIoCoreId = 1;
                int eligible = available - 2;
                int workerCount = Integer.highestOneBit(eligible);
                if (workerCount <= 0) workerCount = 1;
                int workerBitMask = workerCount - 1;
                int[] workerCoreIds = new int[workerCount];
                for (int i = 0; i < workerCount; i++) {
                    workerCoreIds[i] = i + 2;
                }
                String summary = String.format("Multiplatform JVM Fallback | Lógicos: %d | Housekeeping: #%d, Netty: #%d | Workers: %d (Mask: 0x%X)",
                    available, housekeepingCoreId, nettyIoCoreId, workerCount, workerBitMask);
                return new CpuTopologyConfig(housekeepingCoreId, nettyIoCoreId, workerCount, workerBitMask, workerCoreIds, summary);
            }
        } catch (Throwable t) {
            _log.debug("[CoreAutoTuner] Falha no tryPlatformNativeFallback: {}", t.getMessage());
        }
        return null;
    }

    private static CpuTopologyConfig tryMacSysctlTopology() {
        try {
            Process process = new ProcessBuilder("sysctl", "-n",
                "hw.perflevel0.physicalcpu",
                "hw.perflevel1.physicalcpu",
                "hw.physicalcpu",
                "hw.logicalcpu"
            ).start();

            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String lineP = reader.readLine();
                String lineE = reader.readLine();
                String linePhys = reader.readLine();
                String lineLog = reader.readLine();

                if (lineP != null && lineE != null) {
                    int pCores = Integer.parseInt(lineP.trim());
                    int eCores = Integer.parseInt(lineE.trim());
                    int physTotal = linePhys != null ? Integer.parseInt(linePhys.trim()) : (pCores + eCores);
                    int logTotal = lineLog != null ? Integer.parseInt(lineLog.trim()) : physTotal;

                    // E-Cores recebem Housekeeping e Netty
                    int housekeepingCoreId = pCores;
                    int nettyIoCoreId = eCores > 1 ? (pCores + 1) : housekeepingCoreId;

                    // P-Cores exclusivos para os Workers (potencia de 2)
                    int workerCount = Integer.highestOneBit(pCores);
                    if (workerCount <= 0) workerCount = 1;
                    int workerBitMask = workerCount - 1;
                    int[] workerCoreIds = new int[workerCount];
                    for (int i = 0; i < workerCount; i++) {
                        workerCoreIds[i] = i;
                    }

                    String summary = String.format("Apple Silicon (Darwin via sysctl) | Físicos: %d, Lógicos: %d | SMT: Inativo | Heterogêneo: SIM (P:%d, E:%d)",
                        physTotal, logTotal, pCores, eCores);

                    return new CpuTopologyConfig(housekeepingCoreId, nettyIoCoreId, workerCount, workerBitMask, workerCoreIds, summary);
                }
            }
        } catch (Throwable t) {
            _log.debug("[CoreAutoTuner] sysctl fallback falhou: {}", t.getMessage());
        }
        return null;
    }

    /**
     * Executa o cálculo determinístico da alocação de núcleos com base nos dados do OSHI.
     */
    static CpuTopologyConfig calculateTopology(CentralProcessor processor) {
        String vendor = processor.getProcessorIdentifier().getVendor();
        String modelName = processor.getProcessorIdentifier().getName();
        long maxFreqHz = processor.getMaxFreq();
        double maxFreqGhz = maxFreqHz > 0 ? maxFreqHz / 1_000_000_000.0 : 0.0;

        int logicalProcessorCount = processor.getLogicalProcessorCount();
        int physicalProcessorCount = processor.getPhysicalProcessorCount();
        boolean hasSmt = logicalProcessorCount > physicalProcessorCount;

        List<PhysicalProcessor> physicalProcessors = processor.getPhysicalProcessors();
        List<LogicalProcessor> logicalProcessors = processor.getLogicalProcessors();

        // Edge case: ambiente extremamente restrito (1 ou 2 processadores lógicos)
        if (logicalProcessorCount <= 2 || physicalProcessorCount <= 1 || physicalProcessors == null || physicalProcessors.isEmpty()) {
            String summary = String.format("%s %s (%.2f GHz) [Restrito: %dL/%dP Cores, SMT=%b]",
                vendor, modelName, maxFreqGhz, logicalProcessorCount, physicalProcessorCount, hasSmt);
            return buildFallbackConfig(summary);
        }

        // 1. Eliminação de SMT/Hyper-Threading:
        // Mapeia para cada physicalProcessorNumber o primeiro logicalProcessorId do sistema operacional
        Map<Integer, Integer> physicalToPrimaryLogicalMap = new HashMap<>();
        for (LogicalProcessor lp : logicalProcessors) {
            int physId = lp.getPhysicalProcessorNumber();
            physicalToPrimaryLogicalMap.putIfAbsent(physId, lp.getProcessorNumber());
        }

        // 2. Análise de Arquiteturas Heterogêneas (big.LITTLE / Apple Silicon / Intel Hybrid)
        int maxEfficiency = Integer.MIN_VALUE;
        int minEfficiency = Integer.MAX_VALUE;
        for (PhysicalProcessor pp : physicalProcessors) {
            int eff = pp.getEfficiency();
            if (eff > maxEfficiency) maxEfficiency = eff;
            if (eff < minEfficiency) minEfficiency = eff;
        }

        boolean isHeterogeneous = maxEfficiency > minEfficiency;
        List<Integer> performanceCoreIds = new ArrayList<>();
        List<Integer> efficiencyCoreIds = new ArrayList<>();

        for (PhysicalProcessor pp : physicalProcessors) {
            int physNum = pp.getPhysicalProcessorNumber();
            Integer primaryLogicalId = physicalToPrimaryLogicalMap.get(physNum);
            if (primaryLogicalId == null) {
                primaryLogicalId = physNum; // Fallback se não mapeado
            }

            if (isHeterogeneous) {
                if (pp.getEfficiency() == maxEfficiency) {
                    performanceCoreIds.add(primaryLogicalId);
                } else {
                    efficiencyCoreIds.add(primaryLogicalId);
                }
            } else {
                performanceCoreIds.add(primaryLogicalId);
            }
        }

        int housekeepingCoreId;
        int nettyIoCoreId;
        List<Integer> candidateWorkerCoreIds = new ArrayList<>();

        if (isHeterogeneous && !efficiencyCoreIds.isEmpty()) {
            // Arquitetura Heterogênea (ex: Apple M-Series ou Intel Alder/Raptor/Arrow Lake):
            // E-Cores recebem o Sistema Operacional, Housekeeping e Netty I/O.
            // Os P-Cores ficam 100% livres e exclusivos para o pipeline de simulação e ticks de 120 Hz.
            if (efficiencyCoreIds.size() == 1) {
                housekeepingCoreId = efficiencyCoreIds.get(0);
                nettyIoCoreId = housekeepingCoreId;
            } else {                housekeepingCoreId = efficiencyCoreIds.get(0);
                nettyIoCoreId = efficiencyCoreIds.get(1);
            }

            // Todos os Performance Cores físicos são candidatos a workers
            candidateWorkerCoreIds.addAll(performanceCoreIds);
        } else {            // Arquitetura Homogênea (ex: AMD Ryzen 5000/7000/9000, Intel Xeon clássico):
            // Reserva os primeiros núcleos físicos para Housekeeping e Netty I/O
            housekeepingCoreId = performanceCoreIds.get(0);
            nettyIoCoreId = performanceCoreIds.size() > 1 ? performanceCoreIds.get(1) : housekeepingCoreId;

            // Os núcleos físicos restantes tornam-se candidatos para Workers
            for (int i = 2; i < performanceCoreIds.size(); i++) {                candidateWorkerCoreIds.add(performanceCoreIds.get(i));
            }
        }

        // Se após a reserva não restarem candidatos suficientes, recorrer ao fallback seguro
        if (candidateWorkerCoreIds.isEmpty()) {            String summary = String.format("%s %s [Fallback por insuficiência de P-Cores pós-reserva]", vendor, modelName);
            return buildFallbackConfig(summary);
        }

        // 3. Regra da Potência de 2 para Mascaramento Binário:
        // workerCount = Integer.highestOneBit(candidateWorkerCoreIds.size())
        int eligibleCount = candidateWorkerCoreIds.size();
        int workerCount = Integer.highestOneBit(eligibleCount);
        if (workerCount <= 0) {            workerCount = 1;
        }

        int workerBitMask = workerCount - 1;
        int[] workerCoreIds = new int[workerCount];
        for (int i = 0; i < workerCount; i++) {            workerCoreIds[i] = candidateWorkerCoreIds.get(i);
        }

        String archDescription = String.format("%s %s (%.2f GHz) | Físicos: %d, Lógicos: %d | SMT: %s | Heterogêneo: %s (P:%d, E:%d)",
            vendor,
            modelName,
            maxFreqGhz,
            physicalProcessorCount,
            logicalProcessorCount,
            hasSmt ? "ATIVO (Filtrado)" : "Inativo",
            isHeterogeneous ? "SIM" : "NÃO",
            performanceCoreIds.size(),
            efficiencyCoreIds.size()
        );

        return new CpuTopologyConfig(
            housekeepingCoreId,
            nettyIoCoreId,
            workerCount,
            workerBitMask,
            workerCoreIds,
            archDescription
        );
    }

    /**
     * Constrói configuração de fallback segura quando o host possui recursos limitados (1 ou 2 vCPUs).
     */
    private static CpuTopologyConfig buildFallbackConfig(String summary) {
        return new CpuTopologyConfig(
            0,                     // Housekeeping colapsado no Core 0
            0,                     // Netty I/O colapsado no Core 0
            1,                     // 1 Worker
            0,                     // Máscara 0 (1 - 1)
            new int[] { 0 },       // Core 0
            summary
        );
    }
}
