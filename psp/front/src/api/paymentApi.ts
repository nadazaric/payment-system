import axiosInstance from "@/api/axiosInstance";
import {
    InitiatePaymentRequest,
    InitiatePaymentResponse,
    PaymentTransaction
} from "@/types/payment";

export const getPayment = async (
    paymentId: string
): Promise<PaymentTransaction> => {
    const response = await axiosInstance.get<PaymentTransaction>(
        `api/payments/${paymentId}`
    );

    return response.data;
};

export const initiatePayment = async (
    paymentId: string,
    request: InitiatePaymentRequest
): Promise<InitiatePaymentResponse> => {
    const response = await axiosInstance.post<InitiatePaymentResponse>(
        `api/payments/${paymentId}/initiate`,
        request
    );

    return response.data;
};