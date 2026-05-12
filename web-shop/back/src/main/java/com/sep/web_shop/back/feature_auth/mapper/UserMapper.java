package com.sep.web_shop.back.feature_auth.mapper;

import com.sep.web_shop.back.feature_auth.dto.RegisterUserDTO;
import com.sep.web_shop.back.feature_auth.dto.UserDetailsDTO;
import com.sep.web_shop.back.feature_auth.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(RegisterUserDTO registerUserDTO);

    UserDetailsDTO toUserDetailsDto(User user);

}
