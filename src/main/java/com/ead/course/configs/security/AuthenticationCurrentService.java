package com.ead.course.configs.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCurrentService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) getAuthentication().getPrincipal();
    }
}
