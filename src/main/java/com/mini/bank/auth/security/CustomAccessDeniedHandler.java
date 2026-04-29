package com.mini.bank.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.bank.auth.dto.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiError apiError = new ApiError(
                "Forbidden - You don't have permission",
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.name(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
