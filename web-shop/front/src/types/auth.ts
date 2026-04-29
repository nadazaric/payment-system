export type LoginRequest = {
    username: string;
    password: string;
};

export type LoginResponse = {
    id: number;
    username: string;
    accessToken: string;
};

export type RegisterRequest = {
    username: string;
    password: string;
    email: string;
    name: string;
}

export type JwtPayload = {
    sub?: string;
    role?: string;
    iat?: number;
    exp?: number;
};