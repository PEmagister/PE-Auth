package com.pe.peauth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.peauth.dto.auth.request.AuthRequest;
import com.pe.peauth.dto.auth.request.RegisterRequest;
import com.pe.peauth.dto.auth.responce.AuthResponse;
import com.pe.peauth.entity.Token;
import com.pe.peauth.entity.User;
import com.pe.peauth.enums.Role;
import com.pe.peauth.enums.TokenType;
import com.pe.peauth.repository.TokenRepository;
import com.pe.peauth.repository.UserRepository;
import com.pe.peauth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import util.JwtUtil;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  @Override
  public AuthResponse register(RegisterRequest request, Role role) {
    var user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(role)
        .build();
    var savedUser = repository.save(user);

    var jwtToken = JwtUtil.generateToken(Long.valueOf(savedUser.getId()), savedUser.getEmail(), savedUser.getRole().name());
    var refreshToken = JwtUtil.generateRefreshToken(Long.valueOf(savedUser.getId()), savedUser.getEmail(), savedUser.getRole().name());
    saveUserToken(savedUser, jwtToken);
    return AuthResponse.builder()
            .id(savedUser.getId())
            .role(savedUser.getRole())
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  @Override
  public AuthResponse authenticate(AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = JwtUtil.generateToken(Long.valueOf(user.getId()), user.getEmail(), user.getRole().name());
    var refreshToken = JwtUtil.generateRefreshToken(Long.valueOf(user.getId()), user.getEmail(), user.getRole().name());
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthResponse.builder()
            .id(user.getId())
            .role(user.getRole())
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  @Override
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = JwtUtil.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (JwtUtil.isTokenValid(refreshToken, userEmail)) {
        var accessToken = JwtUtil.generateToken(Long.valueOf(user.getId()), user.getEmail(), user.getRole().name()); //jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public boolean hasId(Integer id){
    String username =  SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = userRepository.findByEmail(username);
    return user.orElseThrow().getId().equals(id);
  }

}
