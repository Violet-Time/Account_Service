package com.example.account_service.config;

import com.example.account_service.model.ActionEvent;
import com.example.account_service.model.User;
import com.example.account_service.model.UserAccess;
import com.example.account_service.service.EventService;
import com.example.account_service.service.LoginAttemptService;
import com.example.account_service.service.UserService;
import org.apache.tomcat.util.buf.UDecoder;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final HttpServletRequest request;
    private final LoginAttemptService loginAttemptService;
    private final UserService userService;
    private final EventService eventService;

    public AuthenticationFailureListener(HttpServletRequest request, LoginAttemptService loginAttemptService,
                                         UserService userService, EventService eventService) {
        this.request = request;
        this.loginAttemptService = loginAttemptService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        final String name = e.getAuthentication().getName();
        eventService.addEvent(ActionEvent.LOGIN_FAILED, name, request.getRequestURI(), request.getRequestURI());
        loginAttemptService.loginFailed(name);
        User user = userService.findByEmail(name);
        if (loginAttemptService.isBlocked(name) && user.isAccountNonLocked()) {
            eventService.addEvent(ActionEvent.BRUTE_FORCE, name, request.getRequestURI(), request.getRequestURI());
            try {
                userService.setAccess(new UserAccess(name, "LOCK"), e.getAuthentication().getName());
            } catch (ResponseStatusException ignore) {
            }
            //userService.lockUser(user, name);
            loginAttemptService.loginSucceeded(name);
        }
    }
}