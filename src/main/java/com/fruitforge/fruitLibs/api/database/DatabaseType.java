package com.fruitforge.fruitLibs.api.database;

public enum DatabaseType {
    SQLITE("org.sqlite.JDBC", "jdbc:sqlite:{file}"),
    MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}:{port}/{database}?autoReconnect=true&useSSL=false&serverTimezone=UTC"),
    MARIADB("org.mariadb.jdbc.Driver", "jdbc:mariadb://{host}:{port}/{database}?autoReconnect=true"),
    POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://{host}:{port}/{database}");

    private final String driverClass;
    private final String urlTemplate;

    DatabaseType(String driverClass, String urlTemplate) {
        this.driverClass = driverClass;
        this.urlTemplate = urlTemplate;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }
}