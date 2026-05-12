import axiosInstance from "@/api/axiosInstance";
import { CreateReservationRequest, ReservationDetails, ReservationHistory, UnavailablePeriod } from "@/types/reservation";

const ROOT_PATH = "/reservations"
export const getUnavailablePeriods = async (vehicleId: number): Promise<UnavailablePeriod[]> => {
    const response = await axiosInstance.get<UnavailablePeriod[]>(`${ROOT_PATH}/unavailable-periods/vehicles/${vehicleId}`);

    return response.data;
};

export const createReservation = async (request: CreateReservationRequest): Promise<ReservationDetails> => {
    const response = await axiosInstance.post<ReservationDetails>(`${ROOT_PATH}`, request);

    return response.data;
};

export const getReservations = async (): Promise<ReservationHistory[]> => {
    const response = await axiosInstance.get<ReservationHistory[]>(`${ROOT_PATH}`);

    return response.data;
};

export const getReservationsByVehicle = async (vehicleId: number): Promise<ReservationHistory[]> => {
    const response = await axiosInstance.get<ReservationHistory[]>(`${ROOT_PATH}/vehicles/${vehicleId}`);

    return response.data;
};