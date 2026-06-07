import axiosInstance from "@/api/axiosInstance";
import {
    CreateExpectedPluginRequest,
    CreateExpectedPluginResponse,
    PaymentPlugin,
    UpdatePluginStatusRequest
} from "@/types/plugin";

export const getPlugins = async (): Promise<PaymentPlugin[]> => {
    const response = await axiosInstance.get<PaymentPlugin[]>(
        "/api/super-admin/plugins"
    );

    return response.data;
};

export const createExpectedPlugin = async (
    request: CreateExpectedPluginRequest
): Promise<CreateExpectedPluginResponse> => {
    const response = await axiosInstance.post<CreateExpectedPluginResponse>(
        "/api/super-admin/plugins",
        request
    );

    return response.data;
};

export const updatePluginStatus = async (
    request: UpdatePluginStatusRequest
): Promise<PaymentPlugin> => {
    const response = await axiosInstance.patch<PaymentPlugin>(
        "/api/super-admin/plugins/status",
        request
    );

    return response.data;
};