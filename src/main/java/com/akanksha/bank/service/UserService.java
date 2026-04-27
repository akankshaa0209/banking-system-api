package com.akanksha.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akanksha.bank.repository.UserRepository;
import com.akanksha.bank.dto.AuthRequest;
import com.akanksha.bank.dto.AuthResponse;
import com.akanksha.bank.dto.UserRequest;
import com.akanksha.bank.dto.UserResponse;
import com.akanksha.bank.entity.User;
import com.akanksha.bank.entity.Role;
import com.akanksha.bank.exception.BadRequestException;
import com.akanksha.bank.exception.ResourceNotFoundException;
import com.akanksha.bank.config.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

// for bcrypt
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔐 LOGIN API
    public AuthResponse login(AuthRequest request) {

        // 1. Validate input
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new BadRequestException("Email must not be empty");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Password must not be empty");
        }

        // 2. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        // 3. Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        // 4. Generate JWT token (optional, can be added to AuthResponse if needed)
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .message("Login successful")
                .token(token)
                .build();

        // // 4. Success response
        // return AuthResponse.builder()
        // .message("Login successful")
        // .build();
    }

    // 👤 CREATE USER (REGISTER)
    public UserResponse createUser(UserRequest request) {

        // 1. Validate input
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new BadRequestException("Name is required");
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        if (request.getRole() == null || request.getRole().isEmpty()) {
            throw new BadRequestException("Role is required");
        }

        // 2. Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        // 3. Create user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.from(request.getRole()))
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    // 📄 GET ALL USERS
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔍 GET USER BY ID
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return mapToResponse(user);
    }

    // ❌ DELETE USER
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    // 🔁 COMMON MAPPER
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}