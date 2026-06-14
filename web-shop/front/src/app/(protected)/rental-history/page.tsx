"use client";

import { useEffect, useMemo, useState } from "react";

import {
    Alert,
    Box,
    Chip,
    CircularProgress,
    Typography
} from "@mui/material";

import Grid from "@mui/material/Grid";

import {
    getReservations,
    getReservationsByVehicle
} from "@/api/reservationApi";
import { getVehicles } from "@/api/vehicleApi";
import ReservationHistoryCard from "@/components/reservations/ReservationHistoryCard";
import { useAuthState } from "@/hooks/useAuthState";

import {
    ReservationHistory,
    ReservationTimeStatus
} from "@/types/reservation";

import { Vehicle } from "@/types/vehicle";
import {
    getReservationTimeStatus,
    isFinalFailedPaymentStatus
} from "@/utils/reservationUtils";
import { RENTAL_HISTORY_PAGE_LABELS } from "@/const/label";

type ReservationFilter =
    | ReservationTimeStatus
    | "PAYMENT_ISSUES";

const RESERVATION_FILTERS: {
    label: string;
    value: ReservationFilter;
}[] = [
        {
            label: RENTAL_HISTORY_PAGE_LABELS.activeChip,
            value: "ACTIVE"
        },
        {
            label: RENTAL_HISTORY_PAGE_LABELS.upcomingChip,
            value: "UPCOMING"
        },
        {
            label: RENTAL_HISTORY_PAGE_LABELS.completedChip,
            value: "COMPLETED"
        },
        {
            label: RENTAL_HISTORY_PAGE_LABELS.paymentFailedChip,
            value: "PAYMENT_ISSUES"
        }
    ];

export default function RentalHistoryPage() {
    const authState = useAuthState();

    const [reservations, setReservations] = useState<ReservationHistory[]>([]);
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [selectedVehicleId, setSelectedVehicleId] = useState<number | "">("");
    const [selectedFilter, setSelectedFilter] = useState<ReservationFilter>(RESERVATION_FILTERS[0].value);
    const [initialLoading, setInitialLoading] = useState(true);
    const [reservationsLoading, setReservationsLoading] = useState(false);
    const [error, setError] = useState("");

    const isAdmin = authState.role === "ADMIN";

    useEffect(() => {
        if (authState.isLoading) {
            return;
        }

        let ignore = false;

        if (!isAdmin) {
            getReservations()
                .then((data) => {
                    if (!ignore) {
                        setReservations(data);
                    }
                })
                .catch(() => {
                    if (!ignore) {
                        setError(RENTAL_HISTORY_PAGE_LABELS.loadingError);
                    }
                })
                .finally(() => {
                    if (!ignore) {
                        setInitialLoading(false);
                    }
                });

            return () => {
                ignore = true;
            };
        }

        getVehicles()
            .then((data) => {
                if (!ignore) {
                    setVehicles(data);

                    if (data.length > 0) {
                        setSelectedVehicleId(data[0].id);
                    } else {
                        setInitialLoading(false);
                    }
                }
            })
            .catch(() => {
                if (!ignore) {
                    setError(RENTAL_HISTORY_PAGE_LABELS.loadingVehiclesError);
                    setInitialLoading(false);
                }
            });

        return () => {
            ignore = true;
        };
    }, [
        authState.isLoading,
        isAdmin
    ]);

    useEffect(() => {
        if (
            authState.isLoading
            || !isAdmin
            || selectedVehicleId === ""
        ) {
            return;
        }

        let ignore = false;

        getReservationsByVehicle(selectedVehicleId)
            .then((data) => {
                if (!ignore) {
                    setReservations(data);
                }
            })
            .catch(() => {
                if (!ignore) {
                    setError(RENTAL_HISTORY_PAGE_LABELS.loadingError);
                }
            })
            .finally(() => {
                if (!ignore) {
                    setInitialLoading(false);
                    setReservationsLoading(false);
                }
            });

        return () => {
            ignore = true;
        };
    }, [
        authState.isLoading,
        isAdmin,
        selectedVehicleId
    ]);

    const reservationsWithStatus = useMemo(() => {
        return reservations.map((reservation) => ({
            reservation,
            timeStatus: getReservationTimeStatus(reservation)
        }));
    }, [reservations]);

    const filteredReservations = useMemo(() => {
        if (selectedFilter === "PAYMENT_ISSUES") {
            return reservationsWithStatus.filter(({ reservation }) =>
                isFinalFailedPaymentStatus(reservation.paymentStatus)
            );
        }

        return reservationsWithStatus.filter(({ reservation, timeStatus }) =>
            reservation.paymentStatus === "SUCCESS"
            && timeStatus === selectedFilter
        );
    }, [
        reservationsWithStatus,
        selectedFilter
    ]);

    const handleVehicleChange = (vehicleId: number) => {
        if (vehicleId === selectedVehicleId) {
            return;
        }

        setError("");
        setReservationsLoading(true);
        setSelectedVehicleId(vehicleId);
    };

    if (initialLoading || authState.isLoading) {
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

    return (
        <Box>
            <Box
                sx={{
                    mb: 3
                }}>
                <Typography
                    variant="h4"
                    component="h1"
                    gutterBottom
                    sx={{
                        fontWeight: 700
                    }}>
                    {RENTAL_HISTORY_PAGE_LABELS.title}
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary">
                    {RENTAL_HISTORY_PAGE_LABELS.description}
                </Typography>
            </Box>

            {!error && isAdmin && vehicles.length > 0 && (
                <Box
                    sx={{
                        mb: 2.5
                    }}>
                    <Box
                        sx={{
                            display: "flex",
                            flexWrap: "wrap",
                            gap: 1
                        }}>
                        {vehicles.map((vehicle) => {
                            const isSelected = selectedVehicleId === vehicle.id;

                            return (
                                <Chip
                                    key={vehicle.id}
                                    label={vehicle.name}
                                    clickable
                                    color={isSelected ? "primary" : "default"}
                                    variant={isSelected ? "filled" : "outlined"}
                                    onClick={() => handleVehicleChange(vehicle.id)}
                                    sx={{
                                        fontWeight: 600
                                    }} />
                            );
                        })}
                    </Box>
                </Box>
            )}

            {!error && (
                <Box
                    sx={{
                        mb: 3
                    }}>
                    <Box
                        sx={{
                            display: "flex",
                            flexWrap: "wrap",
                            gap: 1
                        }}>
                        {RESERVATION_FILTERS.map((filter) => {
                            const isSelected = selectedFilter === filter.value;

                            return (
                                <Chip
                                    key={filter.value}
                                    label={filter.label}
                                    clickable
                                    color={isSelected ? "primary" : "default"}
                                    variant={isSelected ? "filled" : "outlined"}
                                    onClick={() => setSelectedFilter(filter.value)}
                                    sx={{
                                        fontWeight: 600
                                    }} />
                            );
                        })}
                    </Box>
                </Box>
            )}

            {error && (
                <Alert
                    severity="error"
                    sx={{
                        mb: 3
                    }}>
                    {error}
                </Alert>
            )}

            {!error && isAdmin && vehicles.length === 0 && (
                <Alert
                    severity="info"
                    sx={{
                        mb: 3
                    }}>
                    {RENTAL_HISTORY_PAGE_LABELS.noVehicles}
                </Alert>
            )}

            {!error && reservations.length === 0 && !reservationsLoading && (
                <Alert
                    severity="info">
                    {RENTAL_HISTORY_PAGE_LABELS.noReservations}
                </Alert>
            )}

            {!error && reservations.length > 0 && filteredReservations.length === 0 && !reservationsLoading && (
                <Alert
                    severity="info">
                    {RENTAL_HISTORY_PAGE_LABELS.noFilteredReservations}
                </Alert>
            )}

            {reservationsLoading && (
                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "center",
                        my: 4
                    }}>
                    <CircularProgress
                        size={28} />
                </Box>
            )}

            {!reservationsLoading && (
                <Grid
                    container
                    spacing={3}>
                    {filteredReservations.map(({ reservation, timeStatus }) => (
                        <Grid
                            key={reservation.id}
                            size={{
                                xs: 12,
                                sm: 6,
                                md: 4,
                                lg: 4
                            }}>
                            <ReservationHistoryCard
                                reservation={reservation}
                                timeStatus={timeStatus} />
                        </Grid>
                    ))}
                </Grid>
            )}
        </Box>
    );
}