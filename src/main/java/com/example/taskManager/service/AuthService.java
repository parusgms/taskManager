package com.example.taskManager.service;

import com.example.taskManager.dto.AuthResponse;
import com.example.taskManager.dto.LoginRequest;
import com.example.taskManager.dto.RegisterRequest;
import com.example.taskManager.entity.User;
import com.example.taskManager.mapper.UserMapper;
import com.example.taskManager.repository.UserRepository;
import com.example.taskManager.util.JwtUtil;
import com.example.taskManager.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = userMapper.toUser(request, passwordHash);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(
                request.username()
        ).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
