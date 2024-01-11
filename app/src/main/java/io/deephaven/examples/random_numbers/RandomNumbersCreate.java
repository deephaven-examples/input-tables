package io.deephaven.examples.random_numbers;

import io.deephaven.client.impl.Session;
import io.deephaven.client.impl.TableHandle;
import io.deephaven.examples.SessionHelper;
import io.deephaven.qst.column.header.ColumnHeader;
import io.deephaven.qst.table.InMemoryKeyBackedInputTable;
import io.deephaven.qst.table.TableHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Creates an {@link InMemoryKeyBackedInputTable in-memory key-backed input table} named {@value #RANDOM_NUMBERS_NAME},
 * with headers {@link #INDEX}, {@link #TIMESTAMP}, {@link #ITERATION}, {@link #INT}, {@link #LONG}, and {@link #DOUBLE}.
 */
public class RandomNumbersCreate extends SessionHelper {

    /**
     * The random numbers table, named "random_numbers".
     */
    public static final String RANDOM_NUMBERS_NAME = "random_numbers";

    /**
     * An {@link ColumnHeader#ofInt(String) int column header} named "Index".
     */
    public static final ColumnHeader<Integer> INDEX = ColumnHeader.ofInt("Index");

    /**
     * An {@link ColumnHeader#ofInstant(String) Instant column header} named "Timestamp".
     */
    public static final ColumnHeader<Instant> TIMESTAMP = ColumnHeader.ofInstant("Timestamp");

    /**
     * An {@link ColumnHeader#ofInt(String) int column header} named "Iteration".
     */
    public static final ColumnHeader<Integer> ITERATION = ColumnHeader.ofInt("Iteration");

    /**
     * An {@link ColumnHeader#ofInt(String) int column header} named "Int".
     */
    public static final ColumnHeader<Integer> INT = ColumnHeader.ofInt("Int");

    /**
     * A {@link ColumnHeader#ofLong(String) long column header} named "Long".
     */
    public static final ColumnHeader<Long> LONG = ColumnHeader.ofLong("Long");

    /**
     * A {@link ColumnHeader#ofDouble(String) double column header} named "Double".
     */
    public static final ColumnHeader<Double> DOUBLE = ColumnHeader.ofDouble("Double");

    private static final Logger log = LoggerFactory.getLogger(RandomNumbersCreate.class);

    @Override
    public void execute(Session session) throws Exception {
        List<String> key = Collections.singletonList(INDEX.name());
        TableHeader header = TableHeader.of(INDEX, TIMESTAMP, ITERATION, INT, LONG, DOUBLE);
        log.info("Creating a key-backed in-memory input table with columns '{}', '{}', '{}', '{}', '{}', and '{}'",
                INDEX.name(), TIMESTAMP.name(), ITERATION.name(), INT.name(), LONG.name(), DOUBLE.name());
        try (final TableHandle handle = session.execute(InMemoryKeyBackedInputTable.of(header, key))) {
            log.info("Publishing the table with the scope name '{}'...", RANDOM_NUMBERS_NAME);
            session.publish(RANDOM_NUMBERS_NAME, handle).get();
            log.info("Success!");
        }
    }

    public static void main(String[] args) throws Exception {
        new RandomNumbersCreate().run();
    }
}
