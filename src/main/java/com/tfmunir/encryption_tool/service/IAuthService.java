package com.tfmunir.encryption_tool.service;

import com.tfmunir.encryption_tool.model.AuthenticationResponse;
import com.tfmunir.encryption_tool.model.LoginRequest;
import com.tfmunir.encryption_tool.model.RegisterRequest;

public interface IAuthService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request);
}
