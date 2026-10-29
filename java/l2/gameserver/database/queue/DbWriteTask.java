package l2.gameserver.database.queue;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface funcional para tarefas de escrita atômica em micro-batch.
 */
@FunctionalInterface
public interface DbWriteTask {
    /**
     * Executa a instrução dentro do contexto da transação aberta no micro-batch.
     * @param conn Conexão transacional fornecida pelo worker do WriterPool.
     * @throws SQLException Se ocorrer erro de SQL.
     */
    void execute(Connection conn) throws SQLException;

    /**
     * Chave de particionamento (ex: playerId ou clanId) para rotear para o mesmo worker,
     * prevenindo deadlocks e garantindo ordem sequencial de escritas para a mesma entidade.
     */
    default int getPartitionKey() {
        return 0;
    }
}
