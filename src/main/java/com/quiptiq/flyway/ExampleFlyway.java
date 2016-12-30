package com.quiptiq.flyway;

import javax.sql.DataSource;

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
}
