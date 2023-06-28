package com.klid.demo_spring_webflux_webclient.runners;

import java.util.UUID;

public class ExecutionContext {

    private static final ThreadLocal<UUID> contextId = ThreadLocal.withInitial(UUID::randomUUID);

    static void clearContext() {
        contextId.remove();
    }

    static UUID getContextId() {
        return contextId.get();
    }
}
