package com.fruitforge.fruitLibs.api.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void connect() throws SQLException;

    void disconnect();

    boolean isConnected();

    Connection getConnection() throws SQLException;

    CompletableFuture<Void> executeAsync(String sql, Object... params);

    CompletableFuture<Integer> executeUpdateAsync(String sql, Object... params);

    <T> CompletableFuture<T> queryAsync(String sql, ResultSetMapper<T> mapper, Object... params);

    void createTable(String tableName, String... columns);

    DatabaseType getType();
}