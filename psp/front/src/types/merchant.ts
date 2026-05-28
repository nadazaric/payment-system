import { PaymentMethod } from "@/types/paymentMethod";

export type MerchantRegistrationRequest = {
    merchantName: string;
    currency: string;
    successUrl: string;
    failUrl: string;
    errorUrl: string;
    adminUsername: string;
    adminPassword: string;
    adminName: string;
};

export type MerchantRegistrationResponse = {
    merchantId: string;
    merchantPassword: string;
    merchantName: string;
    currency: string;
    adminUsername: string;
    defaultSellerId: string;
    defaultSellerReference: string;
};

export type MerchantProfile = {
    merchantId: string;
    merchantName: string;
    currency: string;
    successUrl: string;
    failUrl: string;
    errorUrl: string;
    merchantActive: boolean;
    adminUsername: string;
    adminName: string;
};

export type UpdateMerchantProfileRequest = {
    merchantName: string;
    currency: string;
    successUrl: string;
    failUrl: string;
    errorUrl: string;
};

export type MerchantSellerAccount = {
    id: string;
    sellerReference: string;
    displayName: string;
    active: boolean;
    availablePaymentMethods: PaymentMethod[];
};

export type CreateMerchantSellerAccountRequest = {
    sellerReference: string;
    displayName: string;
};

export type UpdateMerchantSellerAccountRequest = {
    sellerReference: string;
    displayName: string;
};

export type UpdateSellerPaymentMethodsRequest = {
    paymentMethodCodes: string[];
};

export type RegenerateMerchantPasswordResponse = {
    merchantPassword: string;
};
