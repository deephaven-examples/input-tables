package io.deephaven.examples.audit_log;

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

import static io.deephaven.examples.audit_log.AuditLogCreate.AUDIT_LOG_NAME;
import static io.deephaven.examples.audit_log.AuditLogCreate.LOG;
import static io.deephaven.examples.audit_log.AuditLogCreate.TIMESTAMP;
import static io.deephaven.examples.audit_log.AuditLogCreate.TYPE;

/**
 * A holder for an entry an entry into the input table {@value AuditLogCreate#AUDIT_LOG_NAME}.
 */
public class AuditLogAdd extends FlightSessionHelper {

    private static final ColumnHeaders3<Instant, String, String> HEADER = TIMESTAMP.header(TYPE).header(LOG);

    private static final Logger log = LoggerFactory.getLogger(AuditLogAdd.class);

    private final Instant timestamp;
    private final String type;
    private final String logValue;

    /**
     * Constructs a new audit log holder.
     *
     * @param timestamp the value for {@link AuditLogCreate#TIMESTAMP}
     * @param type the value for {@link AuditLogCreate#TYPE}
     * @param logValue the value for {@link AuditLogCreate#LOG}
     */
    public AuditLogAdd(Instant timestamp, String type, String logValue) {
        this.timestamp = timestamp;
        this.type = type;
        this.logValue = logValue;
    }

    void appendTo(ColumnHeaders3<Instant, String, String>.Rows builder) {
        builder.row(timestamp, type, logValue);
    }

    /**
     * Add {@link this} observation into the input table {@value AuditLogCreate#AUDIT_LOG_NAME}.
     */
    @Override
    public void execute(BufferAllocator allocator, FlightSession session) throws Exception {
        execute(allocator, session, Collections.singletonList(this));
    }

    /**
     * Add the {@code observations} into the input table {@value AuditLogCreate#AUDIT_LOG_NAME}.
     */
    public static void execute(BufferAllocator allocator, FlightSession session, Collection<AuditLogAdd> audits) throws Exception {
        if (audits.isEmpty()) {
            log.info("No audits to add, skipping.");
            return;
        }
        ColumnHeaders3<Instant, String, String>.Rows builder = HEADER.start(audits.size());
        for (AuditLogAdd audit : audits) {
            audit.appendTo(builder);
        }
        log.info("Adding {} audit(s) to the input table '{}'...", audits.size(), AUDIT_LOG_NAME);
        session.addToInputTable(new ScopeId(AUDIT_LOG_NAME), builder.newTable(), allocator).get(5, TimeUnit.SECONDS);
        log.info("Success!");
    }

    public static String usage() {
        return "Usage: <program> type log [type log ...]";
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected at least two arguments. " + usage());
        }
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Expected an even number of arguments. " + usage());
        }
        Instant now = Instant.now();
        List<AuditLogAdd> arguments = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            arguments.add(new AuditLogAdd(now, args[i], args[i + 1]));
        }
        new FlightSessionHelper() {
            @Override
            public void execute(BufferAllocator allocator, FlightSession session) throws Exception {
                AuditLogAdd.execute(allocator, session, arguments);
            }
        }.run();
    }
}
