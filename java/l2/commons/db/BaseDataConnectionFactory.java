package l2.commons.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Pool de Conexões JDBC de Alta Performance com HikariCP 6.3.0 e MariaDB Connector 3.5.1.
 * <p>
 * Suporta Segregação Físico-Lógica de Pools (Writer / Reader):
 * - Writer Pool: Otimizado para baixa latência de commits e micro-batches transacionais.
 * - Reader Pool: Otimizado para queries concorrentes, read-only e compatibilidade com Virtual Threads.
 */
public abstract class BaseDataConnectionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDataConnectionFactory.class);
    protected final HikariDataSource writerDataSource;
    protected final HikariDataSource readerDataSource;

    /**
     * Construtor para pool único unificado (usado por AuthServer ou instâncias simples).
     */
    protected BaseDataConnectionFactory(HikariConfig config) {
        try {
            this.writerDataSource = new HikariDataSource(config);
            this.readerDataSource = this.writerDataSource;
            this.testDB();
            LOG.info("DatabaseFactory: Unified connection pool tested and working (HikariCP + MariaDB Driver).");
        } catch (SQLException e) {
            throw new RuntimeException("Can't init database connection pool", e);
        }
    }

    /**
     * Construtor para arquitetura segregrada Writer/Reader (usado pelo GameServer).
     */
    protected BaseDataConnectionFactory(HikariConfig writerConfig, HikariConfig readerConfig) {
        try {
            this.writerDataSource = new HikariDataSource(writerConfig);
            this.readerDataSource = new HikariDataSource(readerConfig);
            this.testDB();
            LOG.info("DatabaseFactory: Segregated Writer and Reader pools initialized successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Can't init segregated database connection pools", e);
        }
    }

    /**
     * Retorna uma conexão padrão (retrocompatível; roteia para o writer pool).
     */
    public Connection getConnection() throws SQLException {
        return getWriteConnection();
    }

    /**
     * Retorna conexão para operações de Escrita / Transações.
     */
    public Connection getWriteConnection() throws SQLException {
        if (this.writerDataSource.isClosed()) {
            throw new IllegalStateException("Writer connection pool has been disposed.");
        }
        return this.writerDataSource.getConnection();
    }

    /**
     * Retorna conexão otimizada para Leitura (ReadOnly).
     */
    public Connection getReadConnection() throws SQLException {
        if (this.readerDataSource.isClosed()) {
            throw new IllegalStateException("Reader connection pool has been disposed.");
        }
        return this.readerDataSource.getConnection();
    }

    protected abstract void testDB() throws SQLException;

    public synchronized void shutdown() throws SQLException {
        LOG.info("DatabaseFactory: Shutting down HikariCP connection pools...");
        if (this.writerDataSource != null && !this.writerDataSource.isClosed()) {
            this.writerDataSource.close();
        }
        if (this.readerDataSource != null && this.readerDataSource != this.writerDataSource && !this.readerDataSource.isClosed()) {
            this.readerDataSource.close();
        }
        LOG.info("DatabaseFactory: HikariCP connection pools closed cleanly.");
    }

    public DataSource getDataSource() {
        return this.writerDataSource;
    }

    public DataSource getWriterDataSource() {
        return this.writerDataSource;
    }

    public DataSource getReaderDataSource() {
        return this.readerDataSource;
    }
}
