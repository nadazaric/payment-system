import axiosInstance from "@/api/axiosInstance";
import { LoginRequest, LoginResponse } from "@/types/auth";

export const login = async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axiosInstance.post<LoginResponse>("user/login", data);
    return response.data;
};