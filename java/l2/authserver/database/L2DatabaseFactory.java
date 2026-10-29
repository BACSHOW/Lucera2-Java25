package l2.authserver.database;

import com.zaxxer.hikari.HikariConfig;
import l2.authserver.Config;
import l2.commons.db.BaseDataConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class L2DatabaseFactory extends BaseDataConnectionFactory {
    private static volatile L2DatabaseFactory instance;

    public static L2DatabaseFactory getInstance() {
        if (instance == null) {
            synchronized (L2DatabaseFactory.class) {
                if (instance == null) {
                    instance = new L2DatabaseFactory(createHikariConfig());
                }
            }
        }
        return instance;
    }

    private L2DatabaseFactory(HikariConfig config) {
        super(config);
    }

    private static HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = String.format("jdbc:mariadb://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false",
            Config.DATABASE_HOST, Config.DATABASE_PORT, Config.DATABASE_NAME);

        config.setPoolName("Lucera2-AuthServer-Pool");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(Config.DATABASE_USER);
        config.setPassword(Config.DATABASE_PASS);
        config.setMaximumPoolSize(Math.max(5, Config.DATABASE_MAX_CONN));
        config.setMinimumIdle(2);
        config.setConnectionTimeout(Math.max(5000L, Config.DATABASE_TIMEOUT * 1000L));

        return config;
    }

    @Override
    protected void testDB() throws SQLException {
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT 1");
        }
    }
}
