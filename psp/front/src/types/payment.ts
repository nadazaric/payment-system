export enum PaymentStatus {
    Created = "CREATED",
    Initiated = "INITIATED",
    Success = "SUCCESS",
    Failed = "FAILED",
    Error = "ERROR",
    Expired = "EXPIRED",
    Cancelled = "CANCELLED",
}

export type PaymentTransaction = {
    paymentId: string;
    merchantName: string;
    sellerReference: string;
    sellerDisplayName: string;
    amount: number;
    currency: string;
    status: PaymentStatus;
};