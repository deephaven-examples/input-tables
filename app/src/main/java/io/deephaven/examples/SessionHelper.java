package io.deephaven.examples;

import io.deephaven.client.DaggerDeephavenSessionRoot;
import io.deephaven.client.impl.ChannelHelper;
import io.deephaven.client.impl.Session;
import io.deephaven.uri.DeephavenTarget;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class SessionHelper {

    private static final Logger log = LoggerFactory.getLogger(SessionHelper.class);

    public abstract void execute(Session session) throws Exception;

    public void run() throws Exception {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        DeephavenTarget target = TargetHelper.target();
        ManagedChannel channel = ChannelHelper.channel(target);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> onShutdown(scheduler, channel)));
        run(scheduler, channel, target);
        scheduler.shutdownNow();
        channel.shutdownNow();
    }

    public void run(ScheduledExecutorService scheduler, ManagedChannel channel, DeephavenTarget target) throws Exception {
        log.info("Connecting to Deephaven @ '{}'...", target);
        try (Session session = DaggerDeephavenSessionRoot.create()
                .factoryBuilder()
                .scheduler(scheduler)
                .managedChannel(channel)
                .build()
                .newSession()) {
            log.info("Connected to Deephaven @ '{}'", target);
            execute(session);
        }
    }

    private static void onShutdown(ScheduledExecutorService scheduler,
                                   ManagedChannel managedChannel) {
        scheduler.shutdownNow();
        managedChannel.shutdownNow();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Scheduler not shutdown after 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            if (!managedChannel.awaitTermination(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Channel not shutdown after 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
