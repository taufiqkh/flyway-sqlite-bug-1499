### Flyway example for bug #1499
This is a minimal project for [Flyway bug #1499](https://github.com/flyway/flyway/issues/1499).

To run:
```
mvn exec:java
```

This copies `src/main/resources/test-v1.db` into the current directory, 
`src/main/resources/V2__test.sql` into `migrations/V2_test.sql` and attempts to perform a 
migration. 