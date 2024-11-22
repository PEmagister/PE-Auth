package com.pe.peauth.controller;


import com.pe.peauth.dto.auth.request.AuthRequest;
import com.pe.peauth.dto.auth.request.RegisterRequest;
import com.pe.peauth.dto.auth.responce.AuthResponse;
import com.pe.peauth.enums.Role;
import com.pe.peauth.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
public class AuthController {

  private final AuthServiceImpl service;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request, Role.STUDENT));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

}
