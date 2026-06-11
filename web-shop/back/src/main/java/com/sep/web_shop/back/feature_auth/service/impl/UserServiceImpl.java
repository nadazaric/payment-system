package com.sep.web_shop.back.feature_auth.service.impl;

import com.sep.web_shop.back.feature_auth.dto.LoginUserDTO;
import com.sep.web_shop.back.feature_auth.dto.RegisterUserDTO;
import com.sep.web_shop.back.feature_auth.dto.UserCredentialsDTO;
import com.sep.web_shop.back.feature_auth.enumeration.Role;
import com.sep.web_shop.back.feature_auth.mapper.UserMapper;
import com.sep.web_shop.back.feature_auth.model.User;
import com.sep.web_shop.back.feature_auth.repository.UserRepository;
import com.sep.web_shop.back.security.jwt.JwtTokenUtil;
import com.sep.web_shop.back.shared.logging.LogStrings;
import com.sep.web_shop.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.AuthenticationException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements com.sep.web_shop.back.feature_auth.service.interf.UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public User getByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, username);
        return user.get();
    }

    @Override
    public User register(RegisterUserDTO dto) throws ResourceNotFoundException {
        boolean alreadyExist = alreadyExistWithUsername(dto.username(), dto.email());
        if (alreadyExist) throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("User with username '%s' or email '%s' already exist!", dto.username(), dto.email()));

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER);

        return userRepository.save(user);
    }

    @Override
    public LoginUserDTO login(UserCredentialsDTO dto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User not found: %s", username)));

            String token = jwtService.generateToken(user.getRole().toString());

            appLoggerService.info(
                    LogStrings.Feature.AUTH,
                    LogStrings.Action.USER_LOGGED_IN,
                    "username={} role={}",
                    user.getUsername(),
                    user.getRole()
            );

            return new LoginUserDTO(user.getId(), user.getUsername(), token);
        } catch (AuthenticationException e) {
            appLoggerService.warn(
                    LogStrings.Feature.AUTH,
                    LogStrings.Action.USER_LOGIN_REJECTED,
                    "reason={} username={} error={}",
                    LogStrings.Reason.INVALID_CREDENTIALS,
                    dto.username(),
                    e.getClass().getSimpleName()
            );

            throw e;
        }
    }

    public boolean alreadyExistWithUsername(String username, String email) {
        List<User> userEntity = userRepository.findByUsernameOrEmail(username, email);
        return !userEntity.isEmpty();
    }

}