export type MerchantLoginRequest = {
    username: string;
    password: string;
};

export type MerchantLoginResponse = {
    token: string;
};

export type JwtPayload = {
    sub?: string;
    role?: string;
    merchantId?: string;
    iat?: number;
    exp?: number;
};
