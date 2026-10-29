package l2.gameserver.database;

import com.zaxxer.hikari.HikariConfig;
import l2.commons.db.BaseDataConnectionFactory;
import l2.gameserver.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fábrica de Conexões de Alta Concorrência para o GameServer.
 * Implementa Segregação Físico-Lógica de Pools (Writer / Reader) com HikariCP e MariaDB Connector/J.
 */
public class DatabaseFactory extends BaseDataConnectionFactory {
    private static volatile DatabaseFactory instance;

    public static DatabaseFactory getInstance() {
        if (instance == null) {
            synchronized (DatabaseFactory.class) {
                if (instance == null) {
                    instance = new DatabaseFactory(createWriterConfig(), createReaderConfig());
                }
            }
        }
        return instance;
    }

    private DatabaseFactory(HikariConfig writerConfig, HikariConfig readerConfig) {
        super(writerConfig, readerConfig);
    }

    /**
     * Pool dedicado exclusivamente para escritas, transações e micro-batches.
     */
    private static HikariConfig createWriterConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("Lucera2-WriterPool");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(buildJdbcUrl());
        config.setUsername(Config.DATABASE_USER);
        config.setPassword(Config.DATABASE_PASS);

        int writerConnections = Math.max(8, Config.DATABASE_MAX_CONN / 2);
        config.setMaximumPoolSize(writerConnections);
        config.setMinimumIdle(Math.max(4, writerConnections / 2));
        config.setConnectionTimeout(Math.max(5000L, Config.DATABASE_TIMEOUT * 1000L));
        config.setIdleTimeout(600_000L);
        config.setMaxLifetime(1_800_000L);

        applyPerformanceProperties(config);
        return config;
    }

    /**
     * Pool dedicado exclusivamente para consultas concorrentes e leituras (ReadOnly).
     */
    private static HikariConfig createReaderConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("Lucera2-ReaderPool");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(buildJdbcUrl());
        config.setUsername(Config.DATABASE_USER);
        config.setPassword(Config.DATABASE_PASS);

        int readerConnections = Math.max(16, Config.DATABASE_MAX_CONN);
        config.setMaximumPoolSize(readerConnections);
        config.setMinimumIdle(Math.max(8, readerConnections / 2));
        config.setConnectionTimeout(Math.max(5000L, Config.DATABASE_TIMEOUT * 1000L));
        config.setIdleTimeout(600_000L);
        config.setMaxLifetime(1_800_000L);
        config.setReadOnly(true);

        applyPerformanceProperties(config);
        return config;
    }

    private static String buildJdbcUrl() {
        return String.format(
            "jdbc:mariadb://%s:%d/%s?" +
            "useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&" +
            "tcpKeepAlive=true&tcpNoDelay=true&socketTimeout=10000&connectTimeout=5000&" +
            "rewriteBatchedStatements=true&useServerPrepStmts=true&cachePrepStmts=true&" +
            "prepStmtCacheSize=500&prepStmtCacheSqlLimit=2048",
            Config.DATABASE_HOST, Config.DATABASE_PORT, Config.DATABASE_NAME
        );
    }

    private static void applyPerformanceProperties(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("tcpNoDelay", "true");
        config.addDataSourceProperty("tcpKeepAlive", "true");
    }

    @Override
    protected void testDB() throws SQLException {
        try (Connection connection = this.getWriteConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT 1");
        }
        try (Connection connection = this.getReadConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT 1");
        }
    }
}
