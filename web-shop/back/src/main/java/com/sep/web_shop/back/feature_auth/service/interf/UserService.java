package com.sep.web_shop.back.feature_auth.service.interf;

import com.sep.web_shop.back.feature_auth.dto.LoginUserDTO;
import com.sep.web_shop.back.feature_auth.dto.UserCredentialsDTO;
import com.sep.web_shop.back.feature_auth.model.User;
import com.sep.web_shop.back.feature_auth.dto.RegisterUserDTO;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

public interface UserService {

    User getByUsername(String username) throws Exception;

    User register(RegisterUserDTO dto) throws ResourceNotFoundException;

    LoginUserDTO login(UserCredentialsDTO dto);

}