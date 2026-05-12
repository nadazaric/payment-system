export type VehicleType = "ECONOMY" | "COMPACT" | "SUV" | "LUXURY" | "VAN";

export type Vehicle = {
    id: number;
    name: string;
    description: string;
    type: VehicleType;
    pricePerDay: number;
    imagePath: string;
};

export type CreateVehicleRequest = {
    name: string;
    description: string;
    type: string;
    pricePerDay: number;
    image: File;
};