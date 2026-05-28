import axiosInstance from "@/api/axiosInstance";
import { PaymentMethod } from "@/types/paymentMethod";

export const getPaymentMethods = async (): Promise<PaymentMethod[]> => {
    const response = await axiosInstance.get<PaymentMethod[]>("/api/payment-methods");

    return response.data;
};
