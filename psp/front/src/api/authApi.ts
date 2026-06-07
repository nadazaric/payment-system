import axiosInstance from "@/api/axiosInstance";
import { AuthLoginRequest, AuthLoginResponse } from "@/types/auth";

export const login = async (
    request: AuthLoginRequest
): Promise<AuthLoginResponse> => {
    const response = await axiosInstance.post<AuthLoginResponse>(
        "/api/auth/login",
        request
    );

    return response.data;
};