package com.smartparking.controller;

import com.smartparking.dto.request.LoginRequest;
import com.smartparking.dto.request.RegisterRequest;
import com.smartparking.dto.response.AuthResponse;
import com.smartparking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("accessToken", response.getAccessToken(), 15 * 60))
                .header(HttpHeaders.SET_COOKIE, createCookie("refreshToken", response.getRefreshToken(), 7 * 24 * 60 * 60))
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                     @CookieValue(value = "refreshToken", required = false) String refreshCookie,
                                     @RequestBody(required = false) Map<String, String> body) {
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        String refreshToken = refreshCookie != null ? refreshCookie : (body != null ? body.get("refreshToken") : null);
        authService.logout(accessToken, refreshToken);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie("accessToken"))
                .header(HttpHeaders.SET_COOKIE, deleteCookie("refreshToken"))
                .build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(value = "refreshToken", required = false) String refreshCookie,
                                              @RequestBody(required = false) Map<String, String> body) {
        String token = refreshCookie != null ? refreshCookie : (body != null ? body.get("refreshToken") : null);
        AuthResponse response = authService.refreshToken(token);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("accessToken", response.getAccessToken(), 15 * 60))
                .header(HttpHeaders.SET_COOKIE, createCookie("refreshToken", response.getRefreshToken(), 7 * 24 * 60 * 60))
                .body(response);
    }

    private String createCookie(String name, String value, long maxAgeSec) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // Set to true in production
                .path("/")
                .maxAge(maxAgeSec)
                .sameSite("Strict")
                .build().toString();
    }

    private String deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build().toString();
    }
}
