package com.sep.psp.back.security;

import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired MerchantAdminRepository merchantAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this username: " + username));

        if (!merchantAdmin.isActive()) {
            throw new DisabledException("Merchant admin account is disabled.");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("MERCHANT_ADMIN");

        return new org.springframework.security.core.userdetails.User(
                merchantAdmin.getUsername(),
                merchantAdmin.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }
}