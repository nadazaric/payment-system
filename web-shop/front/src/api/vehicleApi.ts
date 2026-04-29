import axiosInstance from "@/api/axiosInstance";
import { Vehicle } from "@/types/vehicle";

export const getVehicles = async (): Promise<Vehicle[]> => {
    const response = await axiosInstance.get<Vehicle[]>("/vehicles");
    return response.data;
};