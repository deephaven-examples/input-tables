package io.deephaven.examples.random_numbers;

import io.deephaven.client.impl.FlightSession;
import io.deephaven.client.impl.ScopeId;
import io.deephaven.examples.FlightSessionHelper;
import io.deephaven.qst.column.header.ColumnHeaders6;
import org.apache.arrow.memory.BufferAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Random;

import static io.deephaven.examples.random_numbers.RandomNumbersCreate.DOUBLE;
import static io.deephaven.examples.random_numbers.RandomNumbersCreate.INDEX;
import static io.deephaven.examples.random_numbers.RandomNumbersCreate.INT;
import static io.deephaven.examples.random_numbers.RandomNumbersCreate.ITERATION;
import static io.deephaven.examples.random_numbers.RandomNumbersCreate.LONG;
import static io.deephaven.examples.random_numbers.RandomNumbersCreate.RANDOM_NUMBERS_NAME;
import static io.deephaven.examples.random_numbers.RandomNumbersCreate.TIMESTAMP;

/**
 * Logic to add entries into the input table {@value RandomNumbersCreate#RANDOM_NUMBERS_NAME}.
 */
public class RandomNumbersGenerator extends FlightSessionHelper {

    private static final ColumnHeaders6<Integer, Instant, Integer, Integer, Long, Double> HEADER = INDEX.header(TIMESTAMP).header(ITERATION).header(INT).header(LONG).header(DOUBLE);

    private static final Logger log = LoggerFactory.getLogger(RandomNumbersGenerator.class);

    private final Random random;

    private final int numIterations;
    private final long sleepMillis;

    private final double changePercentage;
    private final int indexBeginInclusive;
    private final int indexEndExclusive;

    /**
     * Constructs a new instance.
     *
     * @param random the random source
     * @param numIterations the number of loops to run
     * @param sleepMillis the sleep time between each iteration
     * @param changePercentage the chance an index will be updated during any given iteration
     * @param indexBeginInclusive the lowest index to consider, inclusive
     * @param indexEndExclusive the highest index to consider, exclusive
     */
    public RandomNumbersGenerator(Random random, int numIterations, long sleepMillis, double changePercentage, int indexBeginInclusive, int indexEndExclusive) {
        this.random = random;
        this.numIterations = numIterations;
        this.sleepMillis = sleepMillis;
        this.changePercentage = changePercentage;
        this.indexBeginInclusive = indexBeginInclusive;
        this.indexEndExclusive = indexEndExclusive;
    }

    @Override
    public void execute(BufferAllocator allocator, FlightSession session) throws Exception {
        log.info("Starting random numbers generator loop...");
        for (int iter = 0; iter < numIterations; ++iter) {
            updateIndices(iter, allocator, session);
            Thread.sleep(sleepMillis);
        }
    }

    private void updateIndices(int iteration, BufferAllocator allocator, FlightSession session) {
        Instant now = Instant.now();
        ColumnHeaders6<Integer, Instant, Integer, Integer, Long, Double>.Rows rows = HEADER.start(indexEndExclusive - indexBeginInclusive);
        int numRows = 0;
        for (int i = indexBeginInclusive; i < indexEndExclusive; ++i) {
            if (random.nextDouble() > changePercentage) {
                continue;
            }
            rows.row(i, now, iteration, random.nextInt(), random.nextLong(), random.nextDouble());
            ++numRows;
        }
        if (numRows > 0) {
            log.info("Updating {} rows...", numRows);
            session.addToInputTable(new ScopeId(RANDOM_NUMBERS_NAME), rows.newTable(), allocator);
        }
    }

    public static String usage() {
        return "Usage: <program> [numIterations sleepMillis changePercentage indexBeginInclusive indexEndExclusive]";
    }

    public static void main(String[] args) throws Exception {
        final int numIterations;
        final int sleepMillis;
        final double changePercentage;
        final int indexBeginInclusive;
        final int indexEndExclusive;
        if (args.length == 0) {
            numIterations = Integer.MAX_VALUE;
            sleepMillis = 100;
            changePercentage = 0.01;
            indexBeginInclusive = 0;
            indexEndExclusive = 100;
        } else if (args.length == 5) {
            numIterations = Integer.parseInt(args[0]);
            sleepMillis = Integer.parseInt(args[1]);
            changePercentage = Double.parseDouble(args[2]);
            indexBeginInclusive = Integer.parseInt(args[3]);
            indexEndExclusive = Integer.parseInt(args[4]);
        } else {
            throw new IllegalArgumentException("Expected 6 arguments: " + usage());
        }
        new RandomNumbersGenerator(new Random(), numIterations, sleepMillis, changePercentage, indexBeginInclusive, indexEndExclusive).run();
    }
}
