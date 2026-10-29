package l2.gameserver.network.l2.netty.disruptor.topology;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoreAutoTunerTest {
    private static final Logger _log = LoggerFactory.getLogger(CoreAutoTunerTest.class);

    @Test
    @DisplayName("Deve inspecionar o processador real do host e calcular a topologia ótima")
    void testResolveHostTopology() {
        CpuTopologyConfig config = CoreAutoTuner.resolve();
        assertThat(config).isNotNull();

        // Imprime o relatório formatado nos logs para validação
        config.printReport(_log);

        // Validações mecânicas da topologia
        assertThat(config.workerCount())
            .as("Worker count deve ser sempre maior que zero")
            .isGreaterThan(0);

        assertThat(Integer.bitCount(config.workerCount()))
            .as("Worker count deve ser estritamente uma potência de 2")
            .isEqualTo(1);

        assertThat(config.workerBitMask())
            .as("workerBitMask deve ser sempre workerCount - 1")
            .isEqualTo(config.workerCount() - 1);

        assertThat(config.workerCoreIds())
            .as("A quantidade de IDs de cores deve bater exatamente com workerCount")
            .hasSize(config.workerCount());

        assertThat(config.architectureSummary())
            .as("O resumo da arquitetura não pode ser vazio")
            .isNotBlank();
    }

    @Test
    @DisplayName("Garante a invariante de imutabilidade estrita do CpuTopologyConfig (cópia defensiva)")
    void testImmutabilityDefense() {
        int[] originalCores = new int[] { 2, 4 };
        CpuTopologyConfig config = new CpuTopologyConfig(
            0,
            1,
            2,
            1,
            originalCores,
            "Teste Imutabilidade"
        );

        // Modifica o array original externo
        originalCores[0] = 999;
        assertThat(config.workerCoreIds()[0])
            .as("A alteração no array externo não pode violar a imutabilidade do record")
            .isEqualTo(2);

        // Modifica o array retornado pelo accessor
        int[] accessorCores = config.workerCoreIds();
        accessorCores[0] = 888;
        assertThat(config.workerCoreIds()[0])
            .as("A alteração no array do accessor não pode violar a imutabilidade do record")
            .isEqualTo(2);
    }

    @Test
    @DisplayName("Validação do mascaramento binário lock-free getWorkerCoreIdForPartition")
    void testBitmaskPartitioning() {
        int[] cores = new int[] { 4, 5, 6, 7 }; // 4 workers
        CpuTopologyConfig config = new CpuTopologyConfig(
            0,
            1,
            4,
            3, // 4 - 1
            cores,
            "AMD Ryzen Simulado"
        );

        // Para qualquer chave, o resultado deve cair ciclicamente entre 4 e 7
        assertThat(config.getWorkerCoreIdForPartition(0)).isEqualTo(4);
        assertThat(config.getWorkerCoreIdForPartition(1)).isEqualTo(5);
        assertThat(config.getWorkerCoreIdForPartition(2)).isEqualTo(6);
        assertThat(config.getWorkerCoreIdForPartition(3)).isEqualTo(7);
        assertThat(config.getWorkerCoreIdForPartition(4)).isEqualTo(4);
        assertThat(config.getWorkerCoreIdForPartition(15)).isEqualTo(7);
        assertThat(config.getWorkerCoreIdForPartition(16)).isEqualTo(4);
    }

    @Test
    @DisplayName("Rejeição imediata de configurações inválidas ou que não sejam potência de 2")
    void testInvalidConfigurations() {
        assertThatThrownBy(() -> new CpuTopologyConfig(
            0, 1, 3, 2, new int[] { 2, 3, 4 }, "Inválido"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("power of two");

        assertThatThrownBy(() -> new CpuTopologyConfig(
            0, 1, 4, 2, new int[] { 2, 3, 4, 5 }, "Máscara errada"
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("workerBitMask must be (workerCount - 1)");
    }
}
