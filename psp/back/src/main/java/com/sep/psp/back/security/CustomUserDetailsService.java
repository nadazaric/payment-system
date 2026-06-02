package com.sep.psp.back.security;

import com.sep.psp.back.feature_auth.enumeration.UserRole;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_superadmin.model.SuperAdmin;
import com.sep.psp.back.feature_superadmin.repository.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    SuperAdminRepository superAdminRepository;

    @Autowired
    MerchantAdminRepository merchantAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return superAdminRepository.findByUsername(username)
                .map(this::buildSuperAdminUserDetails)
                .or(() -> merchantAdminRepository.findByUsername(username)
                        .map(this::buildMerchantAdminUserDetails))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    private UserDetails buildSuperAdminUserDetails(SuperAdmin superAdmin) {
        return User.builder()
                .username(superAdmin.getUsername())
                .password(superAdmin.getPasswordHash())
                .authorities(UserRole.SUPER_ADMIN.authority())
                .disabled(!superAdmin.getActive())
                .build();
    }

    private UserDetails buildMerchantAdminUserDetails(MerchantAdmin merchantAdmin) {
        return User.builder()
                .username(merchantAdmin.getUsername())
                .password(merchantAdmin.getPasswordHash())
                .authorities(UserRole.MERCHANT_ADMIN.authority())
                .disabled(!merchantAdmin.getActive())
                .build();
    }

}