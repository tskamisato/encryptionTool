package com.tfmunir.encryption_tool.service.impl;

import com.tfmunir.encryption_tool.model.Role;
import com.tfmunir.encryption_tool.model.ToolUser;
import com.tfmunir.encryption_tool.repository.ToolUserRepository;
import com.tfmunir.encryption_tool.model.AuthenticationResponse;
import com.tfmunir.encryption_tool.model.LoginRequest;
import com.tfmunir.encryption_tool.model.RegisterRequest;
import com.tfmunir.encryption_tool.service.IAuthService;
import com.tfmunir.encryption_tool.service.IFileSecureCypher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final ToolUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IFileSecureCypher fileSecureCypher;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        var user = ToolUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userKey(fileSecureCypher.generateSecretKey())
                .userVector(fileSecureCypher.generateUserVector())
                .role(Role.USER)
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = repository.findByUsername(request.getUsername()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

}
