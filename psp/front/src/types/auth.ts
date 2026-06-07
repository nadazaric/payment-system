export enum UserRole {
    SuperAdmin = "SUPER_ADMIN",
    MerchantAdmin = "MERCHANT_ADMIN",
}

export type AuthLoginRequest = {
    username: string;
    password: string;
};

export type AuthLoginResponse = {
    token: string;
    role: UserRole;
};

export type JwtPayload = {
    sub?: string;
    role?: UserRole;
    merchantId?: string;
    iat?: number;
    exp?: number;
};