package journal.repository.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection implements AutoCloseable {
    private static final String CONFIG_FILE = "application.properties";
    private static volatile DBConnection instance;
    private final HikariDataSource dataSource;

    private DBConnection() {
        Properties properties = loadProperties();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.pool.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idleTimeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "10000")));
        this.dataSource = new HikariDataSource(config);
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database properties", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        instance = null;
    }
}
