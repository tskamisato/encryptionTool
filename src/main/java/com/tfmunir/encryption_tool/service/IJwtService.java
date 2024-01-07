package com.tfmunir.encryption_tool.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface IJwtService {
    String extractUsername(String jwt);

    String generateToken(UserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}
