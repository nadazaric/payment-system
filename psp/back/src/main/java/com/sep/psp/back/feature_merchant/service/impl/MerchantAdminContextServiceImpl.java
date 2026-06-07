package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantAdminContextService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MerchantAdminContextServiceImpl implements MerchantAdminContextService {

    @Autowired
    MerchantAdminRepository merchantAdminRepository;

    @Override
    public MerchantAdmin getAuthenticatedMerchantAdmin() {
        String username = getAuthenticatedUsername();

        return getMerchantAdminByUsername(username);
    }

    @Override
    public MerchantAdmin getMerchantAdminByUsername(String username) {
        return merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));
    }

    @Override
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Authenticated user not found.");
        }

        return authentication.getName();
    }

}