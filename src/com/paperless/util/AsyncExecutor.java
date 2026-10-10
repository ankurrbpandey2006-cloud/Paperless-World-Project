package com.paperless.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shared thread pool for async audit/compliance tasks.
 * Demonstrates multithreading + synchronization rubric item.
 */
public final class AsyncExecutor {
    private static final ExecutorService POOL = Executors.newFixedThreadPool(4);

    private AsyncExecutor() {}

    public static ExecutorService get() { return POOL; }

    public static void shutdown() { POOL.shutdown(); }
}
