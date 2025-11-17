package com.fruitforge.fruitLibs.core.event.events;

import com.fruitforge.fruitLibs.api.database.DatabaseType;
import com.fruitforge.fruitLibs.api.event.Event;

public class DatabaseConnectEvent extends Event {

    private final DatabaseType type;
    private final String host;
    private final String database;
    private final long connectionTime;

    public DatabaseConnectEvent(DatabaseType type, String host, String database, long connectionTime) {
        this.type = type;
        this.host = host;
        this.database = database;
        this.connectionTime = connectionTime;
    }

    public DatabaseType getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public long getConnectionTime() {
        return connectionTime;
    }
}