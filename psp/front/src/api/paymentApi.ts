import axiosInstance from "@/api/axiosInstance";
import { PaymentTransaction } from "@/types/payment";

export const getPayment = async (
    paymentId: string
): Promise<PaymentTransaction> => {
    const response = await axiosInstance.get<PaymentTransaction>(
        `/api/payments/${paymentId}`
    );

    return response.data;
};