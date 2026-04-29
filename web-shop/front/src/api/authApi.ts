import axiosInstance from "@/api/axiosInstance";
import { LoginRequest, LoginResponse, RegisterRequest } from "@/types/auth";

export const login = async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axiosInstance.post<LoginResponse>("user/login", data);
    return response.data;
};

export const register = async (data: RegisterRequest): Promise<void> => {
    await axiosInstance.post("/user/register", data);
};