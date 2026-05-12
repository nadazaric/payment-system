export type InsurancePackage = {
    id: number;
    name: string;
    description: string;
    pricePerDay: number;
};

export type AdditionalService = {
    id: number;
    name: string;
    description: string;
    pricePerDay: number;
};

export type VehicleOptions = {
    insurancePackages: InsurancePackage[];
    additionalServices: AdditionalService[];
};