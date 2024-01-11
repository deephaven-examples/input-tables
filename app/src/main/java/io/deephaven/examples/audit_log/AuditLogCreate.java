package io.deephaven.examples.audit_log;

import io.deephaven.client.impl.Session;
import io.deephaven.client.impl.TableHandle;
import io.deephaven.examples.SessionHelper;
import io.deephaven.qst.column.header.ColumnHeader;
import io.deephaven.qst.table.InMemoryAppendOnlyInputTable;
import io.deephaven.qst.table.TableHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * Creates an {@link InMemoryAppendOnlyInputTable in-memory append-only input table} named {@value #AUDIT_LOG_NAME},
 * with headers {@link #TIMESTAMP}, {@link #TYPE}, and {@link #LOG}.
 */
public class AuditLogCreate extends SessionHelper {

    /**
     * The audit log table, named "audit_log".
     */
    public static final String AUDIT_LOG_NAME = "audit_log";

    /**
     * An {@link ColumnHeader#ofInstant(String) Instant column header} named "Timestamp".
     */
    public static final ColumnHeader<Instant> TIMESTAMP = ColumnHeader.ofInstant("Timestamp");

    /**
     * A {@link ColumnHeader#ofString(String) String column header} named "Type".
     */
    public static final ColumnHeader<String> TYPE = ColumnHeader.ofString("Type");

    /**
     * A {@link ColumnHeader#ofString(String) String column header} named "Log".
     */
    public static final ColumnHeader<String> LOG = ColumnHeader.ofString("Log");

    private static final Logger log = LoggerFactory.getLogger(AuditLogCreate.class);

    @Override
    public void execute(Session session) throws Exception {
        log.info("Creating an append-only in-memory input table with a timestamp column '{}', a string column '{}', and a string column '{}'...",
                TIMESTAMP.name(), TYPE.name(), LOG.name());
        try (TableHandle handle = session.execute(InMemoryAppendOnlyInputTable.of(TableHeader.of(TIMESTAMP, TYPE, LOG)))) {
            log.info("Publishing the table with the scope name '{}'...", AUDIT_LOG_NAME);
            // Publish the table so that it can be accessed in the query scope by other sessions
            session.publish(AUDIT_LOG_NAME, handle).get();
            log.info("Success!");
        }
    }

    public static void main(String[] args) throws Exception {
        new AuditLogCreate().run();
    }
}
