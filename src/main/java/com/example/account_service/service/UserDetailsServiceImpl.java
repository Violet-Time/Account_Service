package com.example.account_service.service;

import com.example.account_service.model.ActionEvent;
import com.example.account_service.model.User;
import com.example.account_service.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final EventService eventService;
    private final HttpServletRequest request;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, EventService eventService, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByEmailIgnoreCase(username);
        if (optional.isEmpty()) {
            eventService.addEvent(ActionEvent.LOGIN_FAILED, username, request.getRequestURI(), request.getRequestURI());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!optional.get().isAccountNonLocked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User account is locked");
        }
        return optional.get();
    }
}
