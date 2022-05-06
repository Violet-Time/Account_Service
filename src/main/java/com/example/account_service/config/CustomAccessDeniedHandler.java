package com.example.account_service.config;

import com.example.account_service.model.ActionEvent;
import com.example.account_service.model.Event;
import com.example.account_service.repos.EventRepository;
import com.example.account_service.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private EventService eventService;

    public CustomAccessDeniedHandler() {
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception) throws IOException, ServletException {

        eventService.addEvent(ActionEvent.ACCESS_DENIED, request.getRequestURI(), request.getRequestURI());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> data = new HashMap<>();
        data.put(
                "error",
                HttpStatus.FORBIDDEN.getReasonPhrase());
        data.put(
                "path",
                request.getRequestURI());
        data.put(
                "status",
                HttpStatus.FORBIDDEN.value());
        data.put(
                "message",
                "Access Denied!");

        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }
}
