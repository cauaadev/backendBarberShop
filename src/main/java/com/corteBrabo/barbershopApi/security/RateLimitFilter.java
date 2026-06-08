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
        return !request.getRequestURI().equals("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        SlidingWindowRateLimiter limiter = registry.getRateLimiter(ip);

        if (limiter.allowRequest()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Muitas tentativas de login. Tente novamente em 60 segundos.\"}"
            );
        }
    }
}
