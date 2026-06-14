package com.smartparking.service;

import com.smartparking.dto.request.LoginRequest;
import com.smartparking.dto.request.RegisterRequest;
import com.smartparking.dto.response.AuthResponse;
import com.smartparking.entity.BlacklistedToken;
import com.smartparking.entity.RefreshToken;
import com.smartparking.entity.Role;
import com.smartparking.entity.User;
import com.smartparking.entity.enums.RoleType;
import com.smartparking.event.UserRegisteredEvent;
import com.smartparking.exception.BadRequestException;
import com.smartparking.repository.BlacklistedTokenRepository;
import com.smartparking.repository.RefreshTokenRepository;
import com.smartparking.repository.RoleRepository;
import com.smartparking.repository.UserRepository;
import com.smartparking.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final MailService mailService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getEmail());
        validatePasswordComplexity(request.getPassword());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new BadRequestException("Email already registered");
        }
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new BadRequestException("Default role not configured"));

        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(Set.of(userRole))
                .emailVerified(false)
                .verificationToken(verificationToken)
                .build();
        userRepository.save(user);
        
        mailService.sendVerificationEmail(user.getEmail(), verificationToken);
        
        try {
            eventPublisher.publishEvent(new UserRegisteredEvent(user));
        } catch (Exception e) {
            log.error("Event publishing failed for user {}: {}", request.getEmail(), e.getMessage());
        }
        
        // Note: Production-ready usually returns a message saying "Verify your email" 
        // instead of logging them in immediately if emailVerified=false prevents login.
        return AuthResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(LocalDateTime.now())) {
            throw new LockedException("Account is locked. Try again after " + user.getLockoutUntil());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            
            // Success - reset attempts
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            return buildAuthResponse(user);
        } catch (Exception e) {
            handleFailedLogin(user);
            throw new BadRequestException("Invalid email or password");
        }
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockoutUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            log.warn("User {} account locked due to too many failed attempts", user.getEmail());
        }
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            mailService.sendPasswordResetEmail(email, token);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        validatePasswordComplexity(newPassword);
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));
        
        if (user.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(rt -> {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            });
        }
        if (accessToken != null) {
            blacklistedTokenRepository.save(BlacklistedToken.builder()
                    .token(accessToken)
                    .expiryDate(jwtService.extractExpiration(accessToken).toInstant())
                    .build());
        }
    }

    public AuthResponse refreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(this::verifyExpiration)
                .map(rt -> buildAuthResponse(rt.getUser(), rt))
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Refresh token expired. Please login again");
        }
        if (token.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }
        return token;
    }

    private AuthResponse buildAuthResponse(User user) {
        return buildAuthResponse(user, null);
    }

    private AuthResponse buildAuthResponse(User user, RefreshToken oldToken) {
        String accessToken = jwtService.generateAccessToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(), "", user.getRoles().stream().map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.getName().name())).toList()
        ));
        String refreshTokenString = UUID.randomUUID().toString();

        if (oldToken != null) {
            oldToken.setRevoked(true);
            refreshTokenRepository.save(oldToken);
        }

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusMillis(jwtService.getRefreshExpirationMs()))
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }

    private void validatePasswordComplexity(String password) {
        if (password.length() < 8) throw new BadRequestException("Password must be at least 8 characters");
        if (!password.matches(".*[A-Z].*")) throw new BadRequestException("Password must contain at least one uppercase letter");
        if (!password.matches(".*[0-9].*")) throw new BadRequestException("Password must contain at least one digit");
        if (!password.matches(".*[!@#$%^&*()].*")) throw new BadRequestException("Password must contain at least one special character");
    }
}
