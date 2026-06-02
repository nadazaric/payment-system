package com.sep.psp.back.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenUtil {

    String generateToken(String username, String role, String merchantId);

    String getUsernameFromJWT(String token);

    String getRoleFromJWT(String token);

    String getMerchantIdFromJWT(String token);

    Boolean validateToken(String token);

    UserDetails getAuthenticatedUser();

    String getJWTFromRequest(HttpServletRequest request);

}