package io.deephaven.examples;

import io.deephaven.uri.DeephavenTarget;

import java.net.URI;

public class TargetHelper {
    public static final DeephavenTarget DEFAULT_TARGET = DeephavenTarget.builder()
            .isSecure(false)
            .host("localhost")
            .port(10000)
            .build();

    public static DeephavenTarget target() {
        String deephavenTarget = System.getenv("DEEPHAVEN_TARGET");
        if (deephavenTarget == null) {
            return DEFAULT_TARGET;
        }
        return DeephavenTarget.of(URI.create(deephavenTarget));
    }
}
