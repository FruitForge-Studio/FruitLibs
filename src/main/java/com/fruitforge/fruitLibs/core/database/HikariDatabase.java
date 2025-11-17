package com.fruitforge.fruitLibs.core.database;

import com.fruitforge.fruitLibs.api.database.Database;
import com.fruitforge.fruitLibs.api.database.DatabaseType;
import com.fruitforge.fruitLibs.api.database.ResultSetMapper;
import com.fruitforge.fruitLibs.core.logging.LogManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HikariDatabase implements Database {

    private final DatabaseConfig config;
    private final LogManager logManager;
    private final ExecutorService executor;
    private HikariDataSource dataSource;

    public HikariDatabase(DatabaseConfig config, LogManager logManager) {
        this.config = config;
        this.logManager = logManager;
        this.executor = Executors.newFixedThreadPool(
                Math.max(2, config.getMaximumPoolSize() / 2),
                r -> {
                    Thread thread = new Thread(r, "FruitLibs-Database-Worker");
                    thread.setDaemon(true);
                    return thread;
                }
        );
    }

    @Override
    public void connect() throws SQLException {
        if (isConnected()) {
            logManager.warning("Database is already connected");
            return;
        }

        try {
            Class.forName(config.getType().getDriverClass());

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(buildJdbcUrl());

            if (config.getType() != DatabaseType.SQLITE) {
                hikariConfig.setUsername(config.getUsername());
                hikariConfig.setPassword(config.getPassword());
            }

            hikariConfig.setMinimumIdle(config.getMinimumIdle());
            hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
            hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
            hikariConfig.setIdleTimeout(config.getIdleTimeout());
            hikariConfig.setMaxLifetime(config.getMaxLifetime());

            hikariConfig.setPoolName("FruitLibs-HikariCP");
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");

            dataSource = new HikariDataSource(hikariConfig);

            logManager.success("Connected to " + config.getType().name() + " database");

        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + config.getType().getDriverClass(), e);
        }
    }

    @Override
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logManager.info("Disconnected from database");
        }

        executor.shutdown();
    }

    @Override
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!isConnected()) {
            throw new SQLException("Database is not connected");
        }
        return dataSource.getConnection();
    }

    @Override
    public CompletableFuture<Void> executeAsync(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                setParameters(stmt, params);
                stmt.execute();

            } catch (SQLException e) {
                logManager.error("Error executing SQL: " + sql, e);
                throw new RuntimeException(e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Integer> executeUpdateAsync(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                setParameters(stmt, params);
                return stmt.executeUpdate();

            } catch (SQLException e) {
                logManager.error("Error executing update: " + sql, e);
                throw new RuntimeException(e);
            }
        }, executor);
    }

    @Override
    public <T> CompletableFuture<T> queryAsync(String sql, ResultSetMapper<T> mapper, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                setParameters(stmt, params);

                try (ResultSet rs = stmt.executeQuery()) {
                    return mapper.map(rs);
                }

            } catch (SQLException e) {
                logManager.error("Error executing query: " + sql, e);
                throw new RuntimeException(e);
            }
        }, executor);
    }

    @Override
    public void createTable(String tableName, String... columns) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }

        sql.append(")");

        executeAsync(sql.toString()).join();
        logManager.debug("Created table: " + tableName);
    }

    @Override
    public DatabaseType getType() {
        return config.getType();
    }

    private String buildJdbcUrl() {
        String url = config.getType().getUrlTemplate();

        if (config.getType() == DatabaseType.SQLITE) {
            return url.replace("{file}", config.getFilePath());
        }

        return url
                .replace("{host}", config.getHost())
                .replace("{port}", String.valueOf(config.getPort()))
                .replace("{database}", config.getDatabase());
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}