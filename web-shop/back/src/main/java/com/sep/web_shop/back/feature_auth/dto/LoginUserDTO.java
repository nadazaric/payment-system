package com.sep.web_shop.back.feature_auth.dto;

public record LoginUserDTO(
        Long id,
        String username,
        String accessToken
) { }
