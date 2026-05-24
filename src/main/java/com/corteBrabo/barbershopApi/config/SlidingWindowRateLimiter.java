package com.corteBrabo.barbershopApi.config;
import java.util.Deque;
import java.util.LinkedList;

public class SlidingWindowRateLimiter {
    private final Deque<Long> requestLog = new LinkedList<>();
    private final int limit;
    private final long windowSize;

    public SlidingWindowRateLimiter(int limit, long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();

        while (!requestLog.isEmpty() && requestLog.peekFirst() < currentTime - windowSize) {
            requestLog.pollFirst();
        }

        if (requestLog.size() < limit) {
            requestLog.addLast(currentTime);
            return true;
        }
        return false;
    }
}