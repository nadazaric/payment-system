package com.sep.web_shop.back.feature_auth.controller;

import com.sep.web_shop.back.feature_auth.dto.LoginUserDTO;
import com.sep.web_shop.back.feature_auth.dto.RegisterUserDTO;
import com.sep.web_shop.back.feature_auth.dto.UserCredentialsDTO;
import com.sep.web_shop.back.feature_auth.dto.UserDetailsDTO;
import com.sep.web_shop.back.feature_auth.mapper.UserMapper;
import com.sep.web_shop.back.feature_auth.model.User;
import com.sep.web_shop.back.feature_auth.service.interf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired UserService userService;
    @Autowired UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginUserDTO> login(@RequestBody UserCredentialsDTO dto) {

        return new ResponseEntity<>(userService.login(dto), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDetailsDTO> register(@RequestBody RegisterUserDTO dto) throws ResourceNotFoundException {
        User user = this.userService.register(dto);

        return new ResponseEntity<>(userMapper.toUserDetailsDto(user), HttpStatus.CREATED);
    }

}