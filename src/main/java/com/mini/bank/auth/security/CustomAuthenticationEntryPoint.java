package com.mini.bank.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.bank.auth.dto.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiError apiError = new ApiError(
          "Unauthorized - Token missing or invalid",
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.name(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiError));

    }
}
