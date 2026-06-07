package com.sep.psp.back.feature_auth.enumeration;

public enum UserRole {

    SUPER_ADMIN,
    MERCHANT_ADMIN;

    public String authority() {
        return "ROLE_" + this.name();
    }

}