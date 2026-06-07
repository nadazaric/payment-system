package com.sep.psp.back.feature_auth.service.interf;

import com.sep.psp.back.feature_auth.dto.LoginRequest;
import com.sep.psp.back.feature_auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}