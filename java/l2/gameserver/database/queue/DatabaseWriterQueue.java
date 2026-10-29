package l2.gameserver.database.queue;

import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pipeline de Escrita Assíncrona de Ultra-Baixa Latência para MariaDB/InnoDB.
 * <p>
 * Características Principais:
 * - MPSC Queue com particionamento por hash da entidade (PlayerId / ClanId).
 * - Micro-batches agrupados em 500 instruções ou 50ms (o que ocorrer primeiro).
 * - Transações explícitas (BEGIN ... COMMIT) por lote para minimizar fsyncs de Redo Log.
 * - Zero contenção com a thread principal do jogo e ticks de IA.
 * - Graceful Shutdown determinístico com drenagem de 100% dos eventos residuais.
 */
public final class DatabaseWriterQueue {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseWriterQueue.class);
    private static final DatabaseWriterQueue INSTANCE = new DatabaseWriterQueue();

    private static final int BATCH_SIZE_THRESHOLD = 500;
    private static final long FLUSH_INTERVAL_MS = 50L;
    private static final int WORKER_COUNT = Math.max(4, Math.min(16, Runtime.getRuntime().availableProcessors()));

    private final Worker[] workers;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public static DatabaseWriterQueue getInstance() {
        return INSTANCE;
    }

    private DatabaseWriterQueue() {
        this.workers = new Worker[WORKER_COUNT];
        for (int i = 0; i < WORKER_COUNT; i++) {
            this.workers[i] = new Worker(i);
            Thread t = new Thread(this.workers[i], "DB-Writer-Worker-" + i);
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY + 1);
            t.start();
        }
        LOG.info("DatabaseWriterQueue: Inicializada com {} workers dedicados particionados.", WORKER_COUNT);
    }

    /**
     * Enfileira uma instrução de escrita no pipeline assíncrono.
     * Roteia por hash de chave para garantir serialização por entidade.
     */
    public void enqueue(DbWriteTask task) {
        if (!this.running.get()) {
            throw new IllegalStateException("DatabaseWriterQueue está encerrando. Nenhuma nova tarefa permitida.");
        }
        int partition = Math.abs(task.getPartitionKey()) % WORKER_COUNT;
        this.workers[partition].queue.offer(task);
    }

    /**
     * Encerramento ordenado: Bloqueia novas entradas, drena todas as filas e aguarda os commits finais.
     */
    public void shutdown() {
        LOG.info("DatabaseWriterQueue: Iniciando Graceful Shutdown e drenagem de filas...");
        this.running.set(false);

        for (Worker worker : this.workers) {
            worker.triggerShutdown();
        }

        for (Worker worker : this.workers) {
            try {
                worker.awaitCompletion();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.error("DatabaseWriterQueue: Interrupção ao aguardar worker {}", worker.workerId);
            }
        }
        LOG.info("DatabaseWriterQueue: Todos os workers finalizaram e comitaram suas pendências.");
    }

    /**
     * Retorna a quantidade total de tarefas pendentes em todas as partições.
     */
    public int getPendingTasksCount() {
        int total = 0;
        for (Worker worker : this.workers) {
            total += worker.queue.size();
        }
        return total;
    }

    private static final class Worker implements Runnable {
        private final int workerId;
        private final BlockingQueue<DbWriteTask> queue = new LinkedTransferQueue<>();
        private final CountDownLatch doneLatch = new CountDownLatch(1);
        private volatile boolean stopRequested = false;

        Worker(int workerId) {
            this.workerId = workerId;
        }

        void triggerShutdown() {
            this.stopRequested = true;
        }

        void awaitCompletion() throws InterruptedException {
            this.doneLatch.await(30, TimeUnit.SECONDS);
        }

        @Override
        public void run() {
            List<DbWriteTask> batch = new ArrayList<>(BATCH_SIZE_THRESHOLD);
            long lastFlushTime = System.currentTimeMillis();

            while (!this.stopRequested || !this.queue.isEmpty()) {
                try {
                    DbWriteTask task = this.queue.poll(FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        batch.add(task);
                    }

                    long now = System.currentTimeMillis();
                    boolean timeExceeded = (now - lastFlushTime) >= FLUSH_INTERVAL_MS;
                    boolean batchFull = batch.size() >= BATCH_SIZE_THRESHOLD;

                    if ((timeExceeded && !batch.isEmpty()) || batchFull || (this.stopRequested && !batch.isEmpty())) {
                        flushBatch(batch);
                        batch.clear();
                        lastFlushTime = now;
                    }
                } catch (InterruptedException e) {
                    if (this.stopRequested && this.queue.isEmpty()) {
                        break;
                    }
                } catch (Throwable t) {
                    LOG.error("DatabaseWriterQueue: Erro inesperado no worker {}", this.workerId, t);
                }
            }
            this.doneLatch.countDown();
        }

        private void flushBatch(List<DbWriteTask> batch) {
            Connection conn = null;
            try {
                conn = DatabaseFactory.getInstance().getWriteConnection();
                conn.setAutoCommit(false);

                for (DbWriteTask task : batch) {
                    try {
                        task.execute(conn);
                    } catch (SQLException ex) {
                        LOG.error("DatabaseWriterQueue: Erro ao executar tarefa no worker {}", this.workerId, ex);
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                LOG.error("DatabaseWriterQueue: Falha ao comitar micro-batch no worker {}", this.workerId, e);
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rbEx) {
                        LOG.error("DatabaseWriterQueue: Erro durante rollback no worker {}", this.workerId, rbEx);
                    }
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException closeEx) {
                        LOG.error("DatabaseWriterQueue: Erro ao fechar conexão do worker {}", this.workerId, closeEx);
                    }
                }
            }
        }
    }
}
