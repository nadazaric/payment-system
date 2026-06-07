import axiosInstance from "@/api/axiosInstance";
import {
    CreateMerchantSellerAccountRequest,
    MerchantProfile,
    MerchantRegistrationRequest,
    MerchantRegistrationResponse,
    MerchantSellerAccount,
    RegenerateMerchantPasswordResponse,
    UpdateMerchantProfileRequest,
    UpdateMerchantSellerAccountRequest
} from "@/types/merchant";

export const registerMerchant = async (
    request: MerchantRegistrationRequest
): Promise<MerchantRegistrationResponse> => {
    const response = await axiosInstance.post<MerchantRegistrationResponse>(
        "/api/merchant/register",
        request
    );

    return response.data;
};

export const getMerchantProfile = async (): Promise<MerchantProfile> => {
    const response = await axiosInstance.get<MerchantProfile>("/api/merchant/profile");

    return response.data;
};

export const updateMerchantProfile = async (
    request: UpdateMerchantProfileRequest
): Promise<void> => {
    await axiosInstance.put("/api/merchant/profile", request);
};

export const getMerchantSellers = async (): Promise<MerchantSellerAccount[]> => {
    const response = await axiosInstance.get<MerchantSellerAccount[]>("/api/merchant/sellers");

    return response.data;
};

export const createMerchantSeller = async (
    request: CreateMerchantSellerAccountRequest
): Promise<MerchantSellerAccount> => {
    const response = await axiosInstance.post<MerchantSellerAccount>(
        "/api/merchant/sellers",
        request
    );

    return response.data;
};

export const updateMerchantSeller = async (
    sellerId: string,
    request: UpdateMerchantSellerAccountRequest
): Promise<void> => {
    await axiosInstance.put(
        `/api/merchant/sellers/${sellerId}`,
        request
    );
};

export const regenerateMerchantPassword = async (): Promise<RegenerateMerchantPasswordResponse> => {
    const response = await axiosInstance.post<RegenerateMerchantPasswordResponse>(
        "/api/merchant/api-key/regenerate"
    );

    return response.data;
};