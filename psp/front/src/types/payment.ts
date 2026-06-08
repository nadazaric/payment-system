export type PaymentStatus =
    | "CREATED"
    | "INITIATED"
    | "SUCCESS"
    | "FAILED"
    | "ERROR"
    | "EXPIRED"
    | "CANCELLED";

export type PaymentMethodOption = {
    code: string;
    displayName: string;
};

export type PaymentTransaction = {
    paymentId: string;
    merchantName: string;
    sellerReference: string;
    sellerDisplayName: string;
    amount: number;
    currency: string;
    status: PaymentStatus;
    paymentMethods: PaymentMethodOption[];
};