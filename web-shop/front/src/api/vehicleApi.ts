import axiosInstance from "@/api/axiosInstance";
import { CreateVehicleRequest, Vehicle } from "@/types/vehicle";

export const getVehicles = async (): Promise<Vehicle[]> => {
    const response = await axiosInstance.get<Vehicle[]>("/vehicles");
    return response.data;
};

export const getVehicleById = async (id: number): Promise<Vehicle> => {
    console.log(id)
    const response = await axiosInstance.get<Vehicle>(`/vehicles/${id}`);

    return response.data;
};

export const createVehicle = async (request: CreateVehicleRequest): Promise<Vehicle> => {
    const formData = new FormData();

    formData.append(
        "vehicle",
        new Blob(
            [
                JSON.stringify({
                    name: request.name,
                    description: request.description,
                    type: request.type,
                    pricePerDay: request.pricePerDay
                })
            ],
            {
                type: "application/json"
            }
        )
    );

    formData.append("image", request.image);

    const response = await axiosInstance.post<Vehicle>(
        "/vehicles",
        formData
    );

    return response.data;
};