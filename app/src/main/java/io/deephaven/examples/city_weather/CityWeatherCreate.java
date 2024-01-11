package io.deephaven.examples.city_weather;

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
 * Creates an {@link InMemoryKeyBackedInputTable in-memory key-backed input table} named {@value #CITY_WEATHER_NAME},
 * with headers {@link #CITY}, {@link #TIMESTAMP}, and {@link #DEGREES}.
 */
public class CityWeatherCreate extends SessionHelper {

    /**
     * The city weather table, named "city_weather".
     */
    public static final String CITY_WEATHER_NAME = "city_weather";

    /**
     * A {@link ColumnHeader#ofString(String) String column header} named "City".
     */
    public static final ColumnHeader<String> CITY = ColumnHeader.ofString("City");

    /**
     * An {@link ColumnHeader#ofInstant(String) Instant column header} named "Timestamp".
     */
    public static final ColumnHeader<Instant> TIMESTAMP = ColumnHeader.ofInstant("Timestamp");

    /**
     * A {@link ColumnHeader#ofDouble(String) double column header} named "Degrees".
     */
    public static final ColumnHeader<Double> DEGREES = ColumnHeader.ofDouble("Degrees");

    private static final Logger log = LoggerFactory.getLogger(CityWeatherCreate.class);

    @Override
    public void execute(Session session) throws Exception {
        List<String> key = Collections.singletonList(CITY.name());
        TableHeader header = TableHeader.of(CITY, TIMESTAMP, DEGREES);
        log.info("Creating a key-backed in-memory input table with a string column '{}', a timestamp column '{}', and a double column '{}'...",
                CITY.name(), TIMESTAMP.name(), DEGREES.name());
        try (final TableHandle handle = session.execute(InMemoryKeyBackedInputTable.of(header, key))) {
            log.info("Publishing the table with the scope name '{}'...", CITY_WEATHER_NAME);
            // Publish the table so that it can be accessed in the query scope by other sessions
            session.publish(CITY_WEATHER_NAME, handle).get();
        }
        log.info("Success!");
    }

    public static void main(String[] args) throws Exception {
        new CityWeatherCreate().run();
    }
}
