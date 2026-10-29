package l2.gameserver.network.l2.netty.disruptor.topology;

import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Objects;

/**
 * Representação imutável da alocação de núcleos físicos e lógicos calculada
 * para eliminar jitter de escalonamento, falsos compartilhamentos (false sharing)
 * e contenção no motor determinístico de 120 Hz.
 *
 * @param houseKeepingCoreId ID do núcleo físico/lógico dedicado para SO, JIT Compiler e Garbage Collector.
 * @param nettyIoCoreId ID do núcleo físico/lógico dedicado para I/O de rede (Netty Epoll/KQueue EventLoop).
 * @param workerCount Quantidade de workers (sempre garantida como potência de 2, ex: 1, 2, 4, 8, 16, 32).
 * @param workerBitMask Máscara binária para indexação lock-free rápida (workerCount - 1).
 * @param workerCoreIds IDs dos núcleos atribuídos exclusivamente para os workers do pipeline de pacotes.
 * @param architectureSummary Resumo textual do processador, arquitetura detectada, frequência e status de SMT.
 */
public record CpuTopologyConfig(
    int houseKeepingCoreId,
    int nettyIoCoreId,
    int workerCount,
    int workerBitMask,
    int[] workerCoreIds,
    String architectureSummary
) {
    /**
     * Construtor compacto para validações e aplicação estrita de cópia defensiva (Immutability).
     */
    public CpuTopologyConfig {
        Objects.requireNonNull(workerCoreIds, "workerCoreIds must not be null");
        Objects.requireNonNull(architectureSummary, "architectureSummary must not be null");

        if (workerCount <= 0 || (workerCount & (workerCount - 1)) != 0) {
            throw new IllegalArgumentException("workerCount must be a positive power of two: " + workerCount);
        }

        if (workerBitMask != (workerCount - 1)) {
            throw new IllegalArgumentException(
                "workerBitMask must be (workerCount - 1). Expected: " + (workerCount - 1) + ", got: " + workerBitMask
            );
        }

        if (workerCoreIds.length != workerCount) {
            throw new IllegalArgumentException(
                "workerCoreIds length (" + workerCoreIds.length + ") does not match workerCount (" + workerCount + ")"
            );
        }

        // Cópia defensiva para garantir imutabilidade estrita
        workerCoreIds = workerCoreIds.clone();
    }

    @Override
    public int[] workerCoreIds() {
        return workerCoreIds.clone();
    }

    /**
     * Retorna o ID do núcleo correspondente para uma chave de partição usando operação bitwise rápida.
     *
     * @param partitionKey Chave do jogador/sessão ou sequência.
     * @return ID do núcleo atribuído.
     */
    public int getWorkerCoreIdForPartition(int partitionKey) {
        int workerIndex = partitionKey & workerBitMask;
        return workerCoreIds[workerIndex];
    }

    /**
     * Exibe o relatório de topologia de hardware e alocação de núcleos no console/logger.
     */
    public void printReport(Logger log) {
        String border = "================================================================================";
        log.info(border);
        log.info("           L2JLucera2 Hardware Mechanical Sympathy & CPU Topology               ");
        log.info(border);
        log.info("  Host Architecture    : {}", architectureSummary);
        log.info("  Housekeeping Core    : Core #{} (OS, HotSpot JIT & GC Threads)", houseKeepingCoreId);
        log.info("  Netty I/O Core       : Core #{} (Network Epoll/KQueue EventLoop)", nettyIoCoreId);
        log.info("  Worker Ring Pool     : {} threads (BitMask: 0x{} / & {})",
            workerCount, Integer.toHexString(workerBitMask), workerBitMask);
        log.info("  Allocated Worker Cores: {}", Arrays.toString(workerCoreIds));
        log.info("  Optimization Target  : 120 Hz deterministic tick (~8.33ms budget, Zero Jitter)");
        log.info(border);
    }
}
