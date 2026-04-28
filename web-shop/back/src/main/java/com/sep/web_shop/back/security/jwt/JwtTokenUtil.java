package com.sep.web_shop.back.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenUtil {

    String generateToken(String role);

    String getUsernameFromJWT(String token);

    boolean validateToken(String token);

    UserDetails getAuthenticatedUser();

    String getJWTFromRequest(HttpServletRequest request);

}
