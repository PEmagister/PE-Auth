package com.pe.peauth.service;

import com.pe.peauth.dto.auth.request.AuthRequest;
import com.pe.peauth.dto.auth.responce.AuthResponse;
import com.pe.peauth.dto.auth.request.RegisterRequest;
import com.pe.peauth.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthService {

    AuthResponse register(RegisterRequest request, Role role);

    AuthResponse authenticate(AuthRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
