package com.example.taskManager.util;

import com.example.taskManager.exception.types.UnauthorizedException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (path.equals("/api/auth/register") || path.equals("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new UnauthorizedException("Отсутствует заголовок Authorization");

        String token = authHeader.substring(7);
        String username = jwtUtil.verifyToken(token);

        if (username == null) throw new UnauthorizedException("Недействительный токен");

        httpRequest.setAttribute("username", username);
        chain.doFilter(request, response);
    }
}