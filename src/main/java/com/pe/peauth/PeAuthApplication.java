package com.pe.peauth;

import com.pe.peauth.dto.auth.request.AuthRequest;
import com.pe.peauth.dto.auth.request.RegisterRequest;
import com.pe.peauth.dto.auth.responce.AuthResponse;
import com.pe.peauth.entity.User;
import com.pe.peauth.enums.Role;
import com.pe.peauth.repository.UserRepository;
import com.pe.peauth.service.AuthService;
import com.pe.peauth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
@EnableDiscoveryClient
@AllArgsConstructor
public class PeAuthApplication {

	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(PeAuthApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AuthService authService, UserRepository userRepository) {
		return args -> {
			Optional<User> user = userRepository.findByEmail("admin@gmail.com");
			if (user.isPresent())
				return;

			RegisterRequest admin = RegisterRequest.builder()
					.email("admin@gmail.com")
					.password("admin")
					.role(Role.ADMIN)
					.build();

			AuthResponse response = authService.register(admin, Role.ADMIN);
			System.out.println(response);
		};
	}

}
