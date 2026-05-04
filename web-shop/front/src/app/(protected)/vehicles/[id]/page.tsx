"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { Dayjs } from "dayjs";

import {
    Alert,
    Box,
    CircularProgress
} from "@mui/material";

import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import VehicleBookingCard from "@/components/vehicles/VehicleBookingCard";
import VehicleInfoCard from "@/components/vehicles/VehicleInfoCard";
import { getUnavailablePeriods } from "@/api/reservationApi";
import { getVehicleById } from "@/api/vehicleApi";
import { getVehicleOptions } from "@/api/vehicleOptionsApi";
import { UnavailablePeriod } from "@/types/reservation";
import { Vehicle } from "@/types/vehicle";
import { InsurancePackage, AdditionalService } from "@/types/vehicleOptions";
import { VEHICLE_DETAILS_PAGE_LABELS } from "@/const/label";

export default function VehicleDetailsPage() {
    const params = useParams();

    const [vehicle, setVehicle] = useState<Vehicle | null>(null);
    const [insurancePackages, setInsurancePackages] = useState<InsurancePackage[]>([]);
    const [additionalServices, setAdditionalServices] = useState<AdditionalService[]>([]);
    const [unavailablePeriods, setUnavailablePeriods] = useState<UnavailablePeriod[]>([]);
    const [selectedInsuranceId, setSelectedInsuranceId] = useState<number | null>(null);
    const [selectedAdditionalServiceIds, setSelectedAdditionalServiceIds] = useState<number[]>([]);
    const [startDate, setStartDate] = useState<Dayjs | null>(null);
    const [endDate, setEndDate] = useState<Dayjs | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const vehicleId = Number(params.id);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [
                    vehicleData,
                    vehicleOptionsData,
                    unavailablePeriodsData
                ] = await Promise.all([
                    getVehicleById(vehicleId),
                    getVehicleOptions(),
                    getUnavailablePeriods(vehicleId)
                ]);

                setVehicle(vehicleData);
                setInsurancePackages(vehicleOptionsData.insurancePackages);
                setAdditionalServices(vehicleOptionsData.additionalServices);
                setUnavailablePeriods(unavailablePeriodsData);

                if (vehicleOptionsData.insurancePackages.length > 0) {
                    setSelectedInsuranceId(vehicleOptionsData.insurancePackages[0].id);
                }
            } catch {
                setError(VEHICLE_DETAILS_PAGE_LABELS.loadingError);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [vehicleId]);

    if (loading) {
        return (
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    mt: 8
                }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Alert
                severity="error">
                {error}
            </Alert>
        );
    }

    if (!vehicle) {
        return (
            <Alert
                severity="warning">
                {VEHICLE_DETAILS_PAGE_LABELS.vehicleNotFound}
            </Alert>
        );
    }

    return (
        <LocalizationProvider
            dateAdapter={AdapterDayjs}>
            <Box
                sx={{
                    width: "100%",
                    maxWidth: "none",
                    px: {
                        xs: 2,
                        md: 4
                    },
                    py: 3
                }}>

                <Box
                    sx={{
                        display: "grid",
                        gridTemplateColumns: {
                            xs: "1fr",
                            lg: "1fr 2fr"
                        },
                        gap: 3,
                        width: "100%",
                        alignItems: "stretch"
                    }}>
                    <VehicleInfoCard
                        vehicle={vehicle} />

                    <VehicleBookingCard
                        vehiclePricePerDay={vehicle.pricePerDay}
                        insurancePackages={insurancePackages}
                        additionalServices={additionalServices}
                        unavailablePeriods={unavailablePeriods}
                        selectedInsuranceId={selectedInsuranceId}
                        selectedAdditionalServiceIds={selectedAdditionalServiceIds}
                        startDate={startDate}
                        endDate={endDate}
                        setSelectedInsuranceId={setSelectedInsuranceId}
                        setSelectedAdditionalServiceIds={setSelectedAdditionalServiceIds}
                        setStartDate={setStartDate}
                        setEndDate={setEndDate} />
                </Box>
            </Box>
        </LocalizationProvider>
    );
}