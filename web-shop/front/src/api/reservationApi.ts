import axiosInstance from "@/api/axiosInstance";
import { CreateReservationRequest, ReservationDetails, UnavailablePeriod } from "@/types/reservation";

export const getUnavailablePeriods = async (vehicleId: number): Promise<UnavailablePeriod[]> => {
    const response = await axiosInstance.get<UnavailablePeriod[]>(
        `/reservations/vehicles/${vehicleId}/unavailable-periods`
    );

    return response.data;
};

export const createReservation = async (request: CreateReservationRequest): Promise<ReservationDetails> => {
    const response = await axiosInstance.post<ReservationDetails>(
        "/reservations",
        request
    );

    return response.data;
};