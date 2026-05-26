package com.smartparking.util;

import com.smartparking.entity.User;
import com.smartparking.exception.BadRequestException;
import com.smartparking.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            throw new BadRequestException("Not authenticated");
        }
        return details.getUser();
    }
}
