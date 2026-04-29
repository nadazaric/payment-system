export type LoginRequest = {
    username: string;
    password: string;
};

export type LoginResponse = {
    id: number;
    username: string;
    accessToken: string;
};