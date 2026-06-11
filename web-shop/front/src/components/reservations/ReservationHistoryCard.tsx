"use client";

import dayjs from "dayjs";

import {
    Box,
    Card,
    CardContent,
    CardMedia,
    Chip,
    Divider,
    Typography
} from "@mui/material";

import {
    PaymentStatus,
    ReservationHistory,
    ReservationTimeStatus
} from "@/types/reservation";

import { getPaymentStatusLabel } from "@/utils/reservationUtils";
import { RENTAL_HISTORY_PAGE_LABELS } from "@/const/label";

type ReservationHistoryCardProps = {
    reservation: ReservationHistory;
    timeStatus: ReservationTimeStatus | null;
};

const IMAGE_BASE_URL = process.env.NEXT_PUBLIC_IMAGE_BASE_URL ?? "";

const getPaymentStatusStyles = (
    status: PaymentStatus
) => {
    switch (status) {
        case "SUCCESS":
            return {
                bgcolor: "rgba(34, 197, 94, 0.12)",
                color: "#15803D",
                borderColor: "rgba(34, 197, 94, 0.28)"
            };
        case "CREATED":
        case "INITIATED":
            return {
                bgcolor: "rgba(245, 158, 11, 0.12)",
                color: "#B45309",
                borderColor: "rgba(245, 158, 11, 0.28)"
            };
        case "FAILED":
        case "ERROR":
            return {
                bgcolor: "rgba(239, 68, 68, 0.12)",
                color: "#B91C1C",
                borderColor: "rgba(239, 68, 68, 0.28)"
            };
        default:
            return {
                bgcolor: "grey.100",
                color: "text.secondary",
                borderColor: "divider"
            };
    }
};

export default function ReservationHistoryCard({
    reservation
}: ReservationHistoryCardProps) {
    const imageUrl = `${IMAGE_BASE_URL}${reservation.vehicleImagePath}`;

    const formattedStartDate = dayjs(reservation.startDate).format("DD.MM.YYYY");
    const formattedEndDate = dayjs(reservation.endDate).format("DD.MM.YYYY");

    const paymentStatusStyles = getPaymentStatusStyles(reservation.paymentStatus);

    return (
        <Card
            sx={{
                width: "100%",
                maxWidth: "none",
                height: "100%",
                overflow: "hidden",
                display: "flex",
                flexDirection: "column"
            }}>
            <Box
                sx={{
                    width: "100%",
                    height: 180,
                    bgcolor: "grey.100",
                    overflow: "hidden"
                }}>
                <CardMedia
                    component="img"
                    image={imageUrl}
                    alt={reservation.vehicleName}
                    sx={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                        objectPosition: "center"
                    }} />
            </Box>

            <CardContent
                sx={{
                    p: 3,
                    display: "flex",
                    flexDirection: "column",
                    flexGrow: 1
                }}>
                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "flex-start",
                        gap: 2,
                        mb: 1.5
                    }}>
                    <Box
                        sx={{
                            minWidth: 0
                        }}>
                        <Typography
                            variant="h5"
                            sx={{
                                fontWeight: 700,
                                mb: 1,
                                display: "-webkit-box",
                                WebkitLineClamp: 1,
                                WebkitBoxOrient: "vertical",
                                overflow: "hidden"
                            }}>
                            {reservation.vehicleName}
                        </Typography>

                        <Box
                            sx={{
                                display: "flex",
                                flexWrap: "wrap",
                                alignItems: "center",
                                gap: 1
                            }}>

                            <Typography
                                variant="body2"
                                color="text.secondary">
                                {formattedStartDate} - {formattedEndDate}
                            </Typography>
                        </Box>
                    </Box>

                    <Chip
                        label={getPaymentStatusLabel(reservation.paymentStatus)}
                        size="medium"
                        variant="outlined"
                        sx={{
                            fontWeight: 700,
                            flexShrink: 0,
                            bgcolor: paymentStatusStyles.bgcolor,
                            color: paymentStatusStyles.color,
                            borderColor: paymentStatusStyles.borderColor,
                            "& .MuiChip-label": {
                                px: 1
                            }
                        }} />
                </Box>

                <Divider
                    sx={{
                        mb: 2
                    }} />

                <Box
                    sx={{
                        display: "grid",
                        gridTemplateColumns: {
                            xs: "1fr",
                            sm: "1fr 1fr"
                        },
                        gap: 2,
                        mb: 2
                    }}>
                    <Box>
                        <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{
                                mb: 0.5
                            }}>
                            {RENTAL_HISTORY_PAGE_LABELS.insurance}
                        </Typography>

                        <Typography
                            sx={{
                                fontWeight: 700
                            }}>
                            {reservation.insurancePackageName}
                        </Typography>
                    </Box>

                    <Box>
                        <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{
                                mb: 0.5
                            }}>
                            {RENTAL_HISTORY_PAGE_LABELS.additionalServices}
                        </Typography>

                        <Typography
                            sx={{
                                fontWeight: 700,
                                display: "-webkit-box",
                                WebkitLineClamp: 2,
                                WebkitBoxOrient: "vertical",
                                overflow: "hidden"
                            }}>
                            {reservation.additionalServiceNames.length > 0
                                ? reservation.additionalServiceNames.join(", ")
                                : RENTAL_HISTORY_PAGE_LABELS.noAdditionalServices}
                        </Typography>
                    </Box>
                </Box>

                <Box
                    sx={{
                        mt: "auto",
                        display: "flex",
                        justifyContent: "flex-end",
                        alignItems: "flex-end"
                    }}>
                    <Box
                        sx={{
                            textAlign: "right"
                        }}>
                        <Typography
                            variant="body2"
                            color="text.secondary">
                            {RENTAL_HISTORY_PAGE_LABELS.total}
                        </Typography>

                        <Typography
                            variant="h5"
                            sx={{
                                fontWeight: 700,
                                color: "primary.main"
                            }}>
                            {reservation.totalPrice} {RENTAL_HISTORY_PAGE_LABELS.currency}
                        </Typography>
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
}