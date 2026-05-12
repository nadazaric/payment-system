import axiosInstance from "@/api/axiosInstance";
import { VehicleOptions } from "@/types/vehicleOptions";

export const getVehicleOptions = async (): Promise<VehicleOptions> => {
    const response = await axiosInstance.get<VehicleOptions>("/vehicle-options");

    return response.data;
};
