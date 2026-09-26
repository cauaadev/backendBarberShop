package com.corteBrabo.barbershopApi.security;

import com.corteBrabo.barbershopApi.config.SlidingWindowRateLimiter;
import com.corteBrabo.barbershopApi.service.RateLimiterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class  RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterRegistry registry;

    public RateLimitFilter(RateLimiterRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return bucketOf(request) == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String bucket = bucketOf(request);
        int limit = bucket.equals("booking") ? 10 : 5;
        SlidingWindowRateLimiter limiter = registry.getRateLimiter(request.getRemoteAddr(), bucket, limit);

        if (limiter.allowRequest()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                "{\"message\":\"Muitas tentativas. Tente novamente em 60 segundos.\"}"
            );
        }
    }

    private static String bucketOf(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod())) return null;
        String uri = request.getRequestURI();
        if (uri.equals("/auth/login")) return "login";
        if (uri.equals("/auth/signup")) return "signup";
        if (uri.startsWith("/public/") && uri.endsWith("/bookings")) return "booking";
        return null;
    }
}
