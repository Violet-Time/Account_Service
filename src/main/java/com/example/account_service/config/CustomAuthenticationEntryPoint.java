package com.example.account_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        /*response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());*/

        /*response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("3453vHTTP Status 401 - " + authEx.getMessage());*/

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, Object> data = new HashMap<>();
        data.put(
                "error",
                HttpStatus.UNAUTHORIZED.getReasonPhrase());
        data.put(
                "path",
                request.getRequestURI());
        data.put(
                "status",
                HttpStatus.UNAUTHORIZED.value());
        data.put(
                "message",
                "User account is locked");

        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("Baeldung");
        super.afterPropertiesSet();
    }
}
