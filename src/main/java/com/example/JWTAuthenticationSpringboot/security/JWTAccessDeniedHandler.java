package com.example.JWTAuthenticationSpringboot.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

// Mirrors JWTAthenticationEntryPoint (401 for unauthenticated requests) but
// for authenticated requests that lack the required role, e.g. a non-ADMIN
// user calling an /admin/** endpoint -> 403 instead of Spring's default
// whitelabel error page.
@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access Denied !! You do not have permission to perform this action\"}");
    }
}
