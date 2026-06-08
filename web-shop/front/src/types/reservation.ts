export type PaymentStatus =
    | "CREATED"
    | "INITIATED"
    | "SUCCESS"
    | "FAILED"
    | "ERROR"
    | "EXPIRED"
    | "CANCELLED";

export type ReservationTimeStatus = "ACTIVE" | "UPCOMING" | "COMPLETED";

export type UnavailablePeriod = {
    startDate: string;
    endDate: string;
};

export type CreateReservationRequest = {
    vehicleId: number;
    startDate: string;
    endDate: string;
    insurancePackageId: number;
    additionalServiceIds: number[];
};

export type CreateReservationResponse = {
    reservationId: number;
    redirectUrl: string;
    paymentStatus: PaymentStatus;
};

export type ReservationDetails = {
    id: number;
    vehicleId: number;
    startDate: string;
    endDate: string;
    insurancePackageId: number;
    additionalServiceIds: number[];
    totalPrice: number;
    status: string;
};

export type ReservationHistory = {
    id: number;
    vehicleId: number;
    vehicleName: string;
    vehicleImagePath: string;
    vehicleType: string;
    startDate: string;
    endDate: string;
    insurancePackageName: string;
    additionalServiceNames: string[];
    totalPrice: number;
    paymentStatus: PaymentStatus;
};