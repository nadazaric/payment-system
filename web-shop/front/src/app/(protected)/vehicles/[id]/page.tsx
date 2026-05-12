"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { Dayjs } from "dayjs";

import {
    Alert,
    Box,
    CircularProgress,
    Typography
} from "@mui/material";

import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import VehicleAdminCalendarCard from "@/components/vehicles/VehicleAdminCalendarCard";
import VehicleBookingCard from "@/components/vehicles/VehicleBookingCard";
import VehicleInfoCard from "@/components/vehicles/VehicleInfoCard";

import { getUnavailablePeriods } from "@/api/reservationApi";
import { getVehicleById } from "@/api/vehicleApi";
import { getVehicleOptions } from "@/api/vehicleOptionsApi";

import { useAuthState } from "@/hooks/useAuthState";

import { UnavailablePeriod } from "@/types/reservation";
import { Vehicle } from "@/types/vehicle";
import { InsurancePackage, AdditionalService } from "@/types/vehicleOptions";
import { VEHICLE_DETAILS_PAGE_LABELS } from "@/const/label";

export default function VehicleDetailsPage() {
    const params = useParams();
    const authState = useAuthState();

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
    const isAdmin = authState.role === "ADMIN";

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

    if (loading || authState.isLoading) {
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
                <Typography
                    variant="h4"
                    component="h1"
                    sx={{
                        fontWeight: 700,
                        mb: 3
                    }}>
                    {VEHICLE_DETAILS_PAGE_LABELS.pageTitle}
                </Typography>

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

                    {isAdmin ? (
                        <VehicleAdminCalendarCard
                            unavailablePeriods={unavailablePeriods} />
                    ) : (
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
                            setEndDate={setEndDate}
                            vehicleId={vehicle.id} />
                    )}
                </Box>
            </Box>
        </LocalizationProvider>
    );
}