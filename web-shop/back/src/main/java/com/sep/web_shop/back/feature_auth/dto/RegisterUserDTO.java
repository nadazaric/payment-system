package com.sep.web_shop.back.feature_auth.dto;

public record RegisterUserDTO(
        String name,
        String username,
        String email,
        String password
) { }
