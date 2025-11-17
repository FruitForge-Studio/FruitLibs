package com.fruitforge.fruitLibs.api.database;

public interface Migration {

    int getVersion();

    String getDescription();

    void up(Database database);

    void down(Database database);
}