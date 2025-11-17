package com.fruitforge.fruitLibs.core.database;

import com.fruitforge.fruitLibs.api.database.DatabaseType;

public class DatabaseConfig {

    private final DatabaseType type;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String filePath;

    private final int minimumIdle;
    private final int maximumPoolSize;
    private final long connectionTimeout;
    private final long idleTimeout;
    private final long maxLifetime;

    private DatabaseConfig(Builder builder) {
        this.type = builder.type;
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.filePath = builder.filePath;
        this.minimumIdle = builder.minimumIdle;
        this.maximumPoolSize = builder.maximumPoolSize;
        this.connectionTimeout = builder.connectionTimeout;
        this.idleTimeout = builder.idleTimeout;
        this.maxLifetime = builder.maxLifetime;
    }

    public DatabaseType getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    public static Builder builder(DatabaseType type) {
        return new Builder(type);
    }

    public static class Builder {
        private final DatabaseType type;
        private String host = "localhost";
        private int port;
        private String database = "minecraft";
        private String username = "root";
        private String password = "";
        private String filePath = "database.db";

        private int minimumIdle = 2;
        private int maximumPoolSize = 10;
        private long connectionTimeout = 30000;
        private long idleTimeout = 600000;
        private long maxLifetime = 1800000;

        private Builder(DatabaseType type) {
            this.type = type;
            this.port = getDefaultPort(type);
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder minimumIdle(int minimumIdle) {
            this.minimumIdle = minimumIdle;
            return this;
        }

        public Builder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder connectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder idleTimeout(long idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        public Builder maxLifetime(long maxLifetime) {
            this.maxLifetime = maxLifetime;
            return this;
        }

        public DatabaseConfig build() {
            return new DatabaseConfig(this);
        }

        private int getDefaultPort(DatabaseType type) {
            return switch (type) {
                case MYSQL, MARIADB -> 3306;
                case POSTGRESQL -> 5432;
                default -> 0;
            };
        }
    }
}