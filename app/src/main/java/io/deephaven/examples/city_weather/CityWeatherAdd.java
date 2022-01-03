package io.deephaven.examples.city_weather;

import io.deephaven.client.impl.FlightSession;
import io.deephaven.client.impl.ScopeId;
import io.deephaven.examples.FlightSessionHelper;
import io.deephaven.qst.column.header.ColumnHeaders3;
import org.apache.arrow.memory.BufferAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.deephaven.examples.city_weather.CityWeatherCreate.CITY;
import static io.deephaven.examples.city_weather.CityWeatherCreate.CITY_WEATHER_NAME;
import static io.deephaven.examples.city_weather.CityWeatherCreate.DEGREES;
import static io.deephaven.examples.city_weather.CityWeatherCreate.TIMESTAMP;

/**
 * A holder for an entry an entry into the input table {@value CityWeatherCreate#CITY_WEATHER_NAME}.
 */
public class CityWeatherAdd extends FlightSessionHelper {

    private static final ColumnHeaders3<String, Instant, Double> HEADER = CITY.header(TIMESTAMP).header(DEGREES);

    private static final Logger log = LoggerFactory.getLogger(CityWeatherAdd.class);

    private final String city;
    private final Instant timestamp;
    private final double degrees;

    /**
     * Constructs a new city weather holder.
     *
     * @param city the value for {@link CityWeatherCreate#CITY}
     * @param timestamp the value for {@link CityWeatherCreate#TIMESTAMP}
     * @param degrees the value for {@link CityWeatherCreate#DEGREES}
     */
    public CityWeatherAdd(String city, Instant timestamp, double degrees) {
        this.city = city;
        this.timestamp = timestamp;
        this.degrees = degrees;
    }

    void appendTo(ColumnHeaders3<String, Instant, Double>.Rows builder) {
        builder.row(city, timestamp, degrees);
    }

    /**
     * Add {@link this} observation into the input table {@value CityWeatherCreate#CITY_WEATHER_NAME}.
     */
    @Override
    public void execute(BufferAllocator allocator, FlightSession session) throws Exception {
        execute(allocator, session, Collections.singletonList(this));
    }

    /**
     * Add the {@code observations} into the input table {@value CityWeatherCreate#CITY_WEATHER_NAME}.
     */
    public static void execute(BufferAllocator allocator, FlightSession session, Collection<CityWeatherAdd> observations) throws Exception {
        if (observations.isEmpty()) {
            log.info("No observations to add, skipping.");
            return;
        }
        ColumnHeaders3<String, Instant, Double>.Rows builder = HEADER.start(observations.size());
        for (CityWeatherAdd observation : observations) {
            observation.appendTo(builder);
        }
        log.info("Adding {} observation(s) to the input table '{}'...", observations.size(), CITY_WEATHER_NAME);
        session.addToInputTable(new ScopeId(CITY_WEATHER_NAME), builder.newTable(), allocator).get(5, TimeUnit.SECONDS);
        log.info("Success!");
    }

    public static String usage() {
        return "Usage: <program> city degrees [city degrees ...]";
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected at least two arguments. " + usage());
        }
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Expected an even number of arguments. " + usage());
        }
        Instant now = Instant.now();
        List<CityWeatherAdd> arguments = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            arguments.add(new CityWeatherAdd(args[i], now, Double.parseDouble(args[i + 1])));
        }
        new FlightSessionHelper() {
            @Override
            public void execute(BufferAllocator allocator, FlightSession session) throws Exception {
                CityWeatherAdd.execute(allocator, session, arguments);
            }
        }.run();
    }
}
