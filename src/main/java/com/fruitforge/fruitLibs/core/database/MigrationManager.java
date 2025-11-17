package com.fruitforge.fruitLibs.core.database;

import com.fruitforge.fruitLibs.api.database.Database;
import com.fruitforge.fruitLibs.api.database.Migration;
import com.fruitforge.fruitLibs.core.logging.LogManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MigrationManager {

    private final Database database;
    private final LogManager logManager;
    private final List<Migration> migrations;

    public MigrationManager(Database database, LogManager logManager) {
        this.database = database;
        this.logManager = logManager;
        this.migrations = new ArrayList<>();

        createMigrationsTable();
    }

    public void register(Migration migration) {
        migrations.add(migration);
        migrations.sort(Comparator.comparingInt(Migration::getVersion));
    }

    public void runPendingMigrations() {
        int currentVersion = getCurrentVersion();

        logManager.info("Current database version: " + currentVersion);

        for (Migration migration : migrations) {
            if (migration.getVersion() > currentVersion) {
                logManager.info("Running migration v" + migration.getVersion() + ": " + migration.getDescription());

                try {
                    migration.up(database);
                    updateVersion(migration.getVersion());
                    logManager.success("Migration v" + migration.getVersion() + " completed");

                } catch (Exception e) {
                    logManager.error("Migration v" + migration.getVersion() + " failed", e);
                    throw new RuntimeException("Migration failed", e);
                }
            }
        }
    }

    public void rollback(int targetVersion) {
        int currentVersion = getCurrentVersion();

        if (targetVersion >= currentVersion) {
            logManager.warning("Target version must be lower than current version");
            return;
        }

        List<Migration> reverseMigrations = new ArrayList<>(migrations);
        reverseMigrations.sort(Comparator.comparingInt(Migration::getVersion).reversed());

        for (Migration migration : reverseMigrations) {
            if (migration.getVersion() <= currentVersion && migration.getVersion() > targetVersion) {
                logManager.info("Rolling back migration v" + migration.getVersion());

                try {
                    migration.down(database);
                    updateVersion(migration.getVersion() - 1);
                    logManager.success("Rollback v" + migration.getVersion() + " completed");

                } catch (Exception e) {
                    logManager.error("Rollback v" + migration.getVersion() + " failed", e);
                    throw new RuntimeException("Rollback failed", e);
                }
            }
        }
    }

    private void createMigrationsTable() {
        database.createTable("fruitlibs_migrations",
                "version INTEGER PRIMARY KEY",
                "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
        );
    }

    private int getCurrentVersion() {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT MAX(version) as version FROM fruitlibs_migrations"
             );
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("version");
            }

        } catch (SQLException e) {
            logManager.error("Failed to get current migration version", e);
        }

        return 0;
    }

    private void updateVersion(int version) {
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO fruitlibs_migrations (version) VALUES (?)"
             )) {

            stmt.setInt(1, version);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logManager.error("Failed to update migration version", e);
            throw new RuntimeException(e);
        }
    }
}