package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.config.SlidingWindowRateLimiter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterRegistry {

    private final Map<String, SlidingWindowRateLimiter> limiters = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter getRateLimiter(String ip) {
        return limiters.computeIfAbsent(ip, key -> new SlidingWindowRateLimiter(5, 60_000));
    }
}
