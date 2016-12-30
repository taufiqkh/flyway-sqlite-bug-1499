package com.quiptiq.flyway;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import org.flywaydb.core.Flyway;

/**
 * Example of flyway bug with sqlite driver
 */
public class ExampleFlyway {
    private static final String DB_NAME = "test-v1.db";
    private static final String DB_WORKAROUND_NAME = "test2-v1.db";
    private static final String SQL_NAME = "V2__test.sql";

    public static void main(String[] args) throws URISyntaxException, IOException {
        File target = new File(DB_NAME);
        File migrationDir = new File("migrations");
        migrationDir.mkdirs();
        Path migrationTarget = migrationDir.toPath().resolve(SQL_NAME);

        Path dbResource = new File(
                ExampleFlyway.class
                        .getClassLoader()
                        .getResource(DB_NAME)
                        .toURI()
        ).toPath();
        Files.copy(dbResource, target.toPath());
        Path migrationResource = new File(
                ExampleFlyway.class
                .getClassLoader()
                .getResource(SQL_NAME)
                .toURI()
        ).toPath();
        Files.copy(migrationResource, migrationTarget);

        // The following uses a workaround - a wrapper around the
        // SQLiteDataSource that only has a single connection that it returns
//        Files.copy(dbResource, new File(DB_WORKAROUND_NAME).toPath());
//        attemptWorkaroundMigration();

        attemptMigration();

    }

    private static void attemptMigration() {
        SQLiteConfig config = new SQLiteConfig();
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl("jdbc:sqlite:" + DB_NAME);
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("filesystem:migrations");
        flyway.migrate();
    }

    private static void attemptWorkaroundMigration() {
        SQLiteConfig config = new SQLiteConfig();
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        SQLiteDataSource dataSource = new SQLiteDataSource(config);
        dataSource.setUrl("jdbc:sqlite:" + DB_WORKAROUND_NAME);
        Flyway flyway = new Flyway();
        flyway.setDataSource(new WorkaroundDataSource(dataSource));
        flyway.setLocations("filesystem:migrations");
        flyway.migrate();
    }
}
