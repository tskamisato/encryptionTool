package com.tfmunir.encryption_tool.controller;

import com.tfmunir.encryption_tool.model.AuthenticationResponse;
import com.tfmunir.encryption_tool.model.LoginRequest;
import com.tfmunir.encryption_tool.model.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/auth")
public interface AuthenticationController {

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request);

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request);

}
