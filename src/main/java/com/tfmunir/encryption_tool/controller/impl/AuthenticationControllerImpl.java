package com.tfmunir.encryption_tool.controller.impl;


import com.tfmunir.encryption_tool.controller.AuthenticationController;
import com.tfmunir.encryption_tool.model.AuthenticationResponse;
import com.tfmunir.encryption_tool.model.LoginRequest;
import com.tfmunir.encryption_tool.model.RegisterRequest;
import com.tfmunir.encryption_tool.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl  implements AuthenticationController {

    private final IAuthService authService;

    @Override
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
