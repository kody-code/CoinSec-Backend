package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.AuthResponse;
import com.kody.coinsec.backend.dto.LoginRequest;
import com.kody.coinsec.backend.dto.SetupRequest;

public interface AuthService {

    AuthResponse setup(SetupRequest request);

    AuthResponse login(LoginRequest request);

    void logout();
}
