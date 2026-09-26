package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.config.SlidingWindowRateLimiter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterRegistry {

    private final Map<String, SlidingWindowRateLimiter> limiters = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter getRateLimiter(String ip, String bucket, int limit) {
        return limiters.computeIfAbsent(bucket + ":" + ip, key -> new SlidingWindowRateLimiter(limit, 60_000));
    }
}
