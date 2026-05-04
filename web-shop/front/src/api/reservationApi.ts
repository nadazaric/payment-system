import axiosInstance from "@/api/axiosInstance";
import { UnavailablePeriod } from "@/types/reservation";

export const getUnavailablePeriods = async (vehicleId: number): Promise<UnavailablePeriod[]> => {
    const response = await axiosInstance.get<UnavailablePeriod[]>(
        `/reservations/vehicles/${vehicleId}/unavailable-periods`
    );

    return response.data;
};