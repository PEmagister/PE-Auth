package com.pe.peauth.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import util.JwtUtil;

import java.io.IOException;

@Component
public class GatewayFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String gatewayHeader = request.getHeader("X-Gateway-Allow");

        if (gatewayHeader == null || !gatewayHeader.equals(JwtUtil.getSignInKey().toString())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied ");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
