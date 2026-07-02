package com.example.taskManager.service;

import com.example.taskManager.dto.requests.LoginRequest;
import com.example.taskManager.dto.requests.RegisterRequest;
import com.example.taskManager.dto.responses.AuthResponse;
import com.example.taskManager.entity.User;
import com.example.taskManager.exception.types.DuplicateResourceException;
import com.example.taskManager.exception.types.InvalidCredentialsException;
import com.example.taskManager.exception.types.UserNotFoundException;
import com.example.taskManager.mapper.UserMapper;
import com.example.taskManager.repository.UserRepository;
import com.example.taskManager.util.JwtUtil;
import com.example.taskManager.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegister(request);
        String passwordHash = passwordEncoder.encode(request.password());
        User user = userMapper.toUser(request, passwordHash);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new InvalidCredentialsException("Неверный пароль");

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    private void validateRegister(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            throw new DuplicateResourceException("Пользователь с таким именем уже существует");
        if (userRepository.existsByEmail(request.email()))
            throw new DuplicateResourceException("Пользователь с такой почтой уже существует");
    }
}