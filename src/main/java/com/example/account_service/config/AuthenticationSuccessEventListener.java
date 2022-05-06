package com.example.account_service.config;

import com.example.account_service.service.LoginAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationSuccessEventListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    private final HttpServletRequest request;
    private final LoginAttemptService loginAttemptService;

    public AuthenticationSuccessEventListener(HttpServletRequest request, LoginAttemptService loginAttemptService) {
        this.request = request;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        final String name = e.getAuthentication().getName();
        loginAttemptService.loginSucceeded(name);
    }
}