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

import { getReservations } from "@/api/reservationApi";
import ReservationHistoryCard from "@/components/reservations/ReservationHistoryCard";

import {
    ReservationHistory,
    ReservationTimeStatus
} from "@/types/reservation";

import { getReservationTimeStatus } from "@/utils/reservationUtils";
import { RENTAL_HISTORY_PAGE_LABELS } from "@/const/label";

type ReservationFilter =
    | ReservationTimeStatus
    | "PAYMENT_FAILED";

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
        value: "PAYMENT_FAILED"
    }
];

export default function RentalHistoryPage() {
    const [reservations, setReservations] = useState<ReservationHistory[]>([]);
    const [selectedFilter, setSelectedFilter] = useState<ReservationFilter>(RESERVATION_FILTERS[0].value);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let ignore = false;

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
                    setLoading(false);
                }
            });

        return () => {
            ignore = true;
        };
    }, []);

    const reservationsWithStatus = useMemo(() => {
        return reservations.map((reservation) => ({
            reservation,
            timeStatus: getReservationTimeStatus(reservation)
        }));
    }, [reservations]);

    const filteredReservations = useMemo(() => {
        if (selectedFilter === "PAYMENT_FAILED") {
            return reservationsWithStatus.filter(({ reservation }) =>
                reservation.paymentStatus === "FAILED"
            );
        }

        return reservationsWithStatus.filter(({ reservation, timeStatus }) =>
            reservation.paymentStatus !== "FAILED"
            && timeStatus === selectedFilter
        );
    }, [
        reservationsWithStatus,
        selectedFilter
    ]);

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

            {error && (
                <Alert
                    severity="error"
                    sx={{
                        mb: 3
                    }}>
                    {error}
                </Alert>
            )}

            {!error && reservations.length > 0 && (
                <Box
                    sx={{
                        display: "flex",
                        flexWrap: "wrap",
                        gap: 1,
                        mb: 3
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
            )}

            {!error && reservations.length === 0 && (
                <Alert
                    severity="info">
                    {RENTAL_HISTORY_PAGE_LABELS.noReservations}
                </Alert>
            )}

            {!error && reservations.length > 0 && filteredReservations.length === 0 && (
                <Alert
                    severity="info">
                    {RENTAL_HISTORY_PAGE_LABELS.noFilteredReservations}
                </Alert>
            )}

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
        </Box>
    );
}