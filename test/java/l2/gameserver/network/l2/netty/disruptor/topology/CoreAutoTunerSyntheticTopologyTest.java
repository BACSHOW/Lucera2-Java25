package l2.gameserver.network.l2.netty.disruptor.topology;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.LogicalProcessor;
import oshi.hardware.CentralProcessor.PhysicalProcessor;
import oshi.hardware.CentralProcessor.ProcessorCache;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CoreAutoTunerSyntheticTopologyTest {

    /**
     * Fake estático puro de CentralProcessor compatível com Java 25 (Zero dependência de ByteBuddy/Mockito).
     */
    private static CentralProcessor createFakeProcessor(
        String vendor,
        String name,
        long maxFreqHz,
        List<PhysicalProcessor> physList,
        List<LogicalProcessor> logList
    ) {
        ProcessorIdentifier id = new ProcessorIdentifier(
            vendor,
            name,
            "0", "0", "0", "0",
            true,
            maxFreqHz
        );

        return new CentralProcessor() {
            @Override public ProcessorIdentifier getProcessorIdentifier() { return id; }
            @Override public long getMaxFreq() { return maxFreqHz; }
            @Override public long[] getCurrentFreq() { return new long[0]; }
            @Override public List<LogicalProcessor> getLogicalProcessors() { return logList; }
            @Override public List<PhysicalProcessor> getPhysicalProcessors() { return physList; }
            @Override public List<ProcessorCache> getProcessorCaches() { return List.of(); }
            @Override public List<String> getFeatureFlags() { return List.of(); }
            @Override public double getSystemCpuLoadBetweenTicks(long[] longs) { return 0; }
            @Override public long[] getSystemCpuLoadTicks() { return new long[0]; }
            @Override public double[] getSystemLoadAverage(int i) { return new double[0]; }
            @Override public double[] getProcessorCpuLoadBetweenTicks(long[][] longs) { return new double[0]; }
            @Override public long[][] getProcessorCpuLoadTicks() { return new long[0][0]; }
            @Override public int getLogicalProcessorCount() { return logList.size(); }
            @Override public int getPhysicalProcessorCount() { return physList.size(); }
            @Override public int getPhysicalPackageCount() { return 1; }
            @Override public long getContextSwitches() { return 0; }
            @Override public long getInterrupts() { return 0; }
        };
    }

    @Test
    @DisplayName("Cenário 1: AMD Ryzen 5 5600X (6 Núcleos Físicos, 12 Threads com SMT ativo)")
    void testAmdRyzenWithSmt() {
        List<PhysicalProcessor> physList = new ArrayList<>();
        List<LogicalProcessor> logList = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            physList.add(new PhysicalProcessor(0, i, 0, "core-" + i));
            logList.add(new LogicalProcessor(i * 2, i, 0, 0));
            logList.add(new LogicalProcessor(i * 2 + 1, i, 0, 0)); // SMT irmã
        }

        CentralProcessor proc = createFakeProcessor("AuthenticAMD", "AMD Ryzen 5 5600X 6-Core Processor", 4_600_000_000L, physList, logList);

        CpuTopologyConfig config = CoreAutoTuner.calculateTopology(proc);

        // Validações:
        // 6 físicos homogêneos: Core 0 (SO), Core 1 (Netty I/O) -> Restam 4 elegíveis
        // Potência de 2 = 4 workers (máscara 3)
        assertThat(config.workerCount()).isEqualTo(4);
        assertThat(config.workerBitMask()).isEqualTo(3);
        assertThat(config.houseKeepingCoreId()).isEqualTo(0);
        assertThat(config.nettyIoCoreId()).isEqualTo(2);
        assertThat(config.workerCoreIds()).containsExactly(4, 6, 8, 10);
        assertThat(config.architectureSummary()).contains("SMT: ATIVO (Filtrado)");
        assertThat(config.architectureSummary()).contains("Heterogêneo: NÃO");
    }

    @Test
    @DisplayName("Cenário 2: Intel Core i9-13900K (8 P-Cores com Hyper-Threading + 16 E-Cores sem HT)")
    void testIntelHybridArchitecture() {
        List<PhysicalProcessor> physList = new ArrayList<>();
        List<LogicalProcessor> logList = new ArrayList<>();

        int logIdSeq = 0;

        // 8 P-Cores (efficiency = 1, com Hyper-Threading)
        for (int i = 0; i < 8; i++) {
            physList.add(new PhysicalProcessor(0, i, 1, "p-core-" + i));
            logList.add(new LogicalProcessor(logIdSeq++, i, 0, 0));
            logList.add(new LogicalProcessor(logIdSeq++, i, 0, 0));
        }

        // 16 E-Cores (efficiency = 0, sem Hyper-Threading)
        for (int i = 8; i < 24; i++) {
            physList.add(new PhysicalProcessor(0, i, 0, "e-core-" + i));
            logList.add(new LogicalProcessor(logIdSeq++, i, 0, 0));
        }

        CentralProcessor proc = createFakeProcessor("GenuineIntel", "13th Gen Intel(R) Core(TM) i9-13900K", 5_800_000_000L, physList, logList);

        CpuTopologyConfig config = CoreAutoTuner.calculateTopology(proc);

        // E-Cores recebem Housekeeping (16) e Netty (17)
        assertThat(config.houseKeepingCoreId()).isEqualTo(16);
        assertThat(config.nettyIoCoreId()).isEqualTo(17);
        // 8 P-Cores exclusivos para workers
        assertThat(config.workerCount()).isEqualTo(8);
        assertThat(config.workerBitMask()).isEqualTo(7);
        assertThat(config.workerCoreIds()).containsExactly(0, 2, 4, 6, 8, 10, 12, 14);
        assertThat(config.architectureSummary()).contains("Heterogêneo: SIM (P:8, E:16)");
    }

    @Test
    @DisplayName("Cenário 3: Servidor AMD EPYC 9654 (60 Cores Físicos Elegíveis -> Potência de 2 = 32 Workers)")
    void testAmdEpycTruncationToPowerOfTwo() {
        List<PhysicalProcessor> physList = new ArrayList<>();
        List<LogicalProcessor> logList = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            physList.add(new PhysicalProcessor(0, i, 0, "epyc-core-" + i));
            logList.add(new LogicalProcessor(i, i, 0, 0));
        }

        CentralProcessor proc = createFakeProcessor("AuthenticAMD", "AMD EPYC 9654 96-Core Processor", 3_700_000_000L, physList, logList);

        CpuTopologyConfig config = CoreAutoTuner.calculateTopology(proc);

        // 50 físicos -> 2 infraestrutura = 48 elegíveis -> Potência de 2 = 32 workers (máscara 31)
        assertThat(config.workerCount()).isEqualTo(32);
        assertThat(config.workerBitMask()).isEqualTo(31);
        assertThat(config.workerCoreIds()).hasSize(32);
        assertThat(config.workerCoreIds()[0]).isEqualTo(2);
        assertThat(config.workerCoreIds()[31]).isEqualTo(33);
    }

    @Test
    @DisplayName("Cenário 4: VPS / CI-CD Virtualizada de 1 vCPU (Fallback Seguro sem IndexOutOfBounds)")
    void testSingleCoreVpsFallback() {
        List<PhysicalProcessor> physList = List.of(new PhysicalProcessor(0, 0, 0, "vps-core-0"));
        List<LogicalProcessor> logList = List.of(new LogicalProcessor(0, 0, 0, 0));

        CentralProcessor proc = createFakeProcessor("QEMU", "QEMU Virtual CPU version 2.5+", 2_400_000_000L, physList, logList);

        CpuTopologyConfig config = CoreAutoTuner.calculateTopology(proc);

        assertThat(config.workerCount()).isEqualTo(1);
        assertThat(config.workerBitMask()).isEqualTo(0);
        assertThat(config.houseKeepingCoreId()).isEqualTo(0);
        assertThat(config.nettyIoCoreId()).isEqualTo(0);
        assertThat(config.workerCoreIds()).containsExactly(0);
        assertThat(config.architectureSummary()).contains("Restrito: 1L/1P");
        assertThat(config.getWorkerCoreIdForPartition(12345)).isEqualTo(0);
    }

    @Test
    @DisplayName("Cenário 5: Teste de Carga e Concorrência Lock-Free (1 milhão de partições paralelas)")
    void testConcurrentLockFreePartitioning() throws InterruptedException {
        int[] cores = new int[] { 6, 7, 8, 9 };
        CpuTopologyConfig config = new CpuTopologyConfig(0, 1, 4, 3, cores, "Test Parallel");

        int threads = 16;
        int operationsPerThread = 100_000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        ConcurrentHashMap<Integer, AtomicInteger> distribution = new ConcurrentHashMap<>();
        distribution.put(6, new AtomicInteger());
        distribution.put(7, new AtomicInteger());
        distribution.put(8, new AtomicInteger());
        distribution.put(9, new AtomicInteger());

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        int key = (threadId * operationsPerThread) + i;
                        int core = config.getWorkerCoreIdForPartition(key);
                        distribution.get(core).incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 1.600.000 operações totais divididas igualmente entre os 4 cores (400.000 em cada)
        assertThat(distribution.get(6).get()).isEqualTo(400_000);
        assertThat(distribution.get(7).get()).isEqualTo(400_000);
        assertThat(distribution.get(8).get()).isEqualTo(400_000);
        assertThat(distribution.get(9).get()).isEqualTo(400_000);
    }
}
