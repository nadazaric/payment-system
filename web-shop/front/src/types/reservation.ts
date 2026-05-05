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