package com.smartparking.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${app.rate-limit.requests-per-minute:60}")
    private int defaultRequestsPerMinute;

    @Value("${app.rate-limit.auth-requests-per-minute:5}")
    private int authRequestsPerMinute;

    private final Map<String, Bucket> generalCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String clientIp = getClientIp(request);
        
        // Stricter limits for authentication endpoints to prevent brute-force
        if (path != null && (path.contains("/auth/login") || path.contains("/auth/register"))) {
            Bucket bucket = authCache.computeIfAbsent(clientIp, k -> createBucket(authRequestsPerMinute));
            if (!bucket.tryConsume(1)) {
                sendErrorResponse(response, "Too many authentication attempts. Please try again later.");
                return;
            }
        } else {
            // General rate limiting for all other endpoints
            Bucket bucket = generalCache.computeIfAbsent(clientIp, k -> createBucket(defaultRequestsPerMinute));
            if (!bucket.tryConsume(1)) {
                sendErrorResponse(response, "Rate limit exceeded. Please wait a minute.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    private Bucket createBucket(int limitCount) {
        Bandwidth limit = Bandwidth.classic(limitCount, Refill.greedy(limitCount, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
