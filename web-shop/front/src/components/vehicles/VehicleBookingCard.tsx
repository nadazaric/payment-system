"use client";

import React from "react";
import { useRouter } from "next/navigation";
import dayjs, { Dayjs } from "dayjs";

import {
    Box,
    Button,
    Card,
    CardContent,
    Checkbox,
    Divider,
    Radio,
    Tooltip,
    Typography
} from "@mui/material";

import { DateCalendar } from "@mui/x-date-pickers/DateCalendar";
import { PickerDay } from "@mui/x-date-pickers/PickerDay";

import { createReservation } from "@/api/reservationApi";
import { InsurancePackage, AdditionalService } from "@/types/vehicleOptions";
import { UnavailablePeriod } from "@/types/reservation";
import { VEHICLE_DETAILS_PAGE_LABELS } from "@/const/label";

type RangePickerDayProps = React.ComponentProps<typeof PickerDay>;

type VehicleBookingCardProps = {
    vehicleId: number;
    vehiclePricePerDay: number;
    insurancePackages: InsurancePackage[];
    additionalServices: AdditionalService[];
    unavailablePeriods: UnavailablePeriod[];
    selectedInsuranceId: number | null;
    selectedAdditionalServiceIds: number[];
    startDate: Dayjs | null;
    endDate: Dayjs | null;
    setSelectedInsuranceId: (id: number) => void;
    setSelectedAdditionalServiceIds: React.Dispatch<React.SetStateAction<number[]>>;
    setStartDate: (date: Dayjs | null) => void;
    setEndDate: (date: Dayjs | null) => void;
};

export default function VehicleBookingCard({
    vehicleId,
    vehiclePricePerDay,
    insurancePackages,
    additionalServices,
    unavailablePeriods,
    selectedInsuranceId,
    selectedAdditionalServiceIds,
    startDate,
    endDate,
    setSelectedInsuranceId,
    setSelectedAdditionalServiceIds,
    setStartDate,
    setEndDate
}: VehicleBookingCardProps) {
    const router = useRouter();

    const [saving, setSaving] = React.useState(false);
    const [reservationError, setReservationError] = React.useState("");

    const isUnavailableDate = (date: Dayjs) => {
        return unavailablePeriods.some((period) => {
            const unavailableStartDate = dayjs(period.startDate);
            const unavailableEndDate = dayjs(period.endDate);

            return (
                date.isSame(unavailableStartDate, "day")
                || date.isSame(unavailableEndDate, "day")
                || (
                    date.isAfter(unavailableStartDate, "day")
                    && date.isBefore(unavailableEndDate, "day")
                )
            );
        });
    };

    const selectedRangeContainsUnavailableDate = () => {
        if (!startDate || !endDate) {
            return false;
        }

        let currentDate = startDate;

        while (currentDate.isBefore(endDate, "day") || currentDate.isSame(endDate, "day")) {
            if (isUnavailableDate(currentDate)) {
                return true;
            }

            currentDate = currentDate.add(1, "day");
        }

        return false;
    };

    const handleDateSelect = (newValue: Dayjs | null) => {
        if (!newValue) {
            return;
        }

        if (!startDate || endDate) {
            setStartDate(newValue);
            setEndDate(null);
            return;
        }

        if (newValue.isBefore(startDate, "day")) {
            setStartDate(newValue);
            setEndDate(null);
            return;
        }

        if (newValue.isSame(startDate, "day")) {
            setEndDate(null);
            return;
        }

        setEndDate(newValue);
    };

    const RangePickerDay = (dayProps: RangePickerDayProps) => {
        const day = dayProps.day as Dayjs;

        const isUnavailable = isUnavailableDate(day);

        const isRangeStart = Boolean(
            startDate
            && day.isSame(startDate, "day")
        );

        const isRangeEnd = Boolean(
            endDate
            && day.isSame(endDate, "day")
        );

        const isInRange = Boolean(
            startDate
            && endDate
            && day.isAfter(startDate, "day")
            && day.isBefore(endDate, "day")
        );

        return (
            <PickerDay
                {...dayProps}
                selected={isRangeStart || isRangeEnd}
                sx={{
                    ...(isInRange && {
                        borderRadius: 0,
                        bgcolor: "rgba(15, 118, 110, 0.10)",
                        color: "text.primary",
                        "&:hover": {
                            bgcolor: "rgba(15, 118, 110, 0.16)"
                        }
                    }),
                    ...(isRangeStart && {
                        borderTopLeftRadius: "50%",
                        borderBottomLeftRadius: "50%",
                        borderTopRightRadius: endDate ? 0 : "50%",
                        borderBottomRightRadius: endDate ? 0 : "50%",
                        bgcolor: "primary.main",
                        color: "primary.contrastText",
                        fontWeight: 700,
                        "&:hover": {
                            bgcolor: "primary.dark"
                        }
                    }),
                    ...(isRangeEnd && {
                        borderTopRightRadius: "50%",
                        borderBottomRightRadius: "50%",
                        borderTopLeftRadius: 0,
                        borderBottomLeftRadius: 0,
                        bgcolor: "primary.main",
                        color: "primary.contrastText",
                        fontWeight: 700,
                        "&:hover": {
                            bgcolor: "primary.dark"
                        }
                    }),
                    ...(isUnavailable && {
                        bgcolor: "rgba(239, 68, 68, 0.12)",
                        color: "#DC2626",
                        textDecoration: "line-through",
                        fontWeight: 700,
                        "&.Mui-disabled": {
                            bgcolor: "rgba(239, 68, 68, 0.12)",
                            color: "#DC2626",
                            opacity: 1
                        },
                        "&:hover": {
                            bgcolor: "rgba(239, 68, 68, 0.18)"
                        }
                    })
                }} />
        );
    };

    const handleAdditionalServiceChange = (serviceId: number) => {
        setSelectedAdditionalServiceIds((previousSelectedIds) => {
            if (previousSelectedIds.includes(serviceId)) {
                return previousSelectedIds.filter((id) => id !== serviceId);
            }

            return [
                ...previousSelectedIds,
                serviceId
            ];
        });
    };

    const selectedInsurance = insurancePackages.find((insurancePackage) =>
        insurancePackage.id === selectedInsuranceId
    );

    const selectedAdditionalServices = additionalServices.filter((service) =>
        selectedAdditionalServiceIds.includes(service.id)
    );

    const additionalServicesPrice = selectedAdditionalServices.reduce(
        (sum, service) => sum + service.pricePerDay,
        0
    );

    const totalPerDay = vehiclePricePerDay
        + (selectedInsurance?.pricePerDay ?? 0)
        + additionalServicesPrice;

    const numberOfDays = startDate && endDate && endDate.isAfter(startDate, "day")
        ? endDate.diff(startDate, "day")
        : 0;

    const totalPrice = totalPerDay * numberOfDays;

    const hasInvalidPeriod = Boolean(
        startDate
        && endDate
        && !endDate.isAfter(startDate, "day")
    );

    const hasUnavailableDates = selectedRangeContainsUnavailableDate();

    const dateErrorMessage = hasInvalidPeriod
        ? VEHICLE_DETAILS_PAGE_LABELS.invalidPeriodError
        : hasUnavailableDates
            ? VEHICLE_DETAILS_PAGE_LABELS.unavailablePeriodError
            : "";

    const hasDateError = Boolean(dateErrorMessage);

    const canBuy = Boolean(
        startDate
        && endDate
        && numberOfDays > 0
        && !hasInvalidPeriod
        && !hasUnavailableDates
        && selectedInsuranceId
    );

    const calendarValue = endDate ?? startDate ?? dayjs();

    const handleBuy = async () => {
        if (!startDate || !endDate || !selectedInsuranceId) {
            return;
        }

        try {
            setSaving(true);
            setReservationError("");

            await createReservation({
                vehicleId,
                startDate: startDate.format("YYYY-MM-DD"),
                endDate: endDate.format("YYYY-MM-DD"),
                insurancePackageId: selectedInsuranceId,
                additionalServiceIds: selectedAdditionalServiceIds
            });

            router.push("/vehicles");
        } catch {
            setReservationError("Failed to create reservation.");
        } finally {
            setSaving(false);
        }
    };

    return (
        <Card
            sx={{
                width: "100%",
                maxWidth: "none",
                minWidth: 0,
                justifySelf: "stretch"
            }}>
            <CardContent
                sx={{
                    p: 3
                }}>
                <Box
                    sx={{
                        display: "grid",
                        gridTemplateColumns: {
                            xs: "1fr",
                            md: "1fr 1fr"
                        },
                        gap: 3,
                        alignItems: "start"
                    }}>
                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "column",
                            gap: 3
                        }}>
                        <Box>
                            <Typography
                                variant="h6"
                                sx={{
                                    fontWeight: 700,
                                    mb: 2
                                }}>
                                {VEHICLE_DETAILS_PAGE_LABELS.insuranceTitle}
                            </Typography>

                            <Box
                                sx={{
                                    display: "grid",
                                    gridTemplateColumns: {
                                        xs: "1fr"
                                    },
                                    gap: 1.25
                                }}>
                                {insurancePackages.map((insurancePackage) => {
                                    const isSelected = selectedInsuranceId === insurancePackage.id;

                                    return (
                                        <Box
                                            key={insurancePackage.id}
                                            onClick={() => setSelectedInsuranceId(insurancePackage.id)}
                                            sx={{
                                                border: "1px solid",
                                                borderColor: isSelected ? "primary.main" : "divider",
                                                borderRadius: "12px",
                                                p: 1.5,
                                                cursor: "pointer",
                                                bgcolor: isSelected ? "rgba(0, 128, 128, 0.04)" : "background.paper",
                                                transition: "0.15s ease",
                                                "&:hover": {
                                                    borderColor: "primary.main",
                                                    bgcolor: isSelected ? "rgba(0, 128, 128, 0.06)" : "grey.50"
                                                }
                                            }}>
                                            <Box
                                                sx={{
                                                    display: "flex",
                                                    flexDirection: "column",
                                                    gap: 0.75
                                                }}>
                                                <Box
                                                    sx={{
                                                        display: "grid",
                                                        gridTemplateColumns: "1fr auto auto",
                                                        alignItems: "center",
                                                        gap: 1.5
                                                    }}>
                                                    <Typography
                                                        variant="body1"
                                                        sx={{
                                                            fontWeight: 700
                                                        }}>
                                                        {insurancePackage.name}
                                                    </Typography>

                                                    <Typography
                                                        variant="body2"
                                                        sx={{
                                                            fontWeight: 700,
                                                            color: insurancePackage.pricePerDay === 0 ? "success.main" : "primary.main",
                                                            whiteSpace: "nowrap"
                                                        }}>
                                                        {insurancePackage.pricePerDay === 0
                                                            ? VEHICLE_DETAILS_PAGE_LABELS.included
                                                            : `+${insurancePackage.pricePerDay} ${VEHICLE_DETAILS_PAGE_LABELS.currency}`}
                                                    </Typography>

                                                    <Radio
                                                        checked={isSelected}
                                                        onClick={(event) => event.stopPropagation()}
                                                        onChange={() => setSelectedInsuranceId(insurancePackage.id)}
                                                        size="small"
                                                        sx={{
                                                            p: 0
                                                        }} />
                                                </Box>

                                                <Typography
                                                    variant="body2"
                                                    color="text.secondary"
                                                    sx={{
                                                        display: "-webkit-box",
                                                        WebkitLineClamp: 2,
                                                        WebkitBoxOrient: "vertical",
                                                        overflow: "hidden"
                                                    }}>
                                                    {insurancePackage.description}
                                                </Typography>
                                            </Box>
                                        </Box>
                                    );
                                })}
                            </Box>
                        </Box>

                        <Box>
                            <Typography
                                variant="h6"
                                sx={{
                                    fontWeight: 700,
                                    mb: 2
                                }}>
                                {VEHICLE_DETAILS_PAGE_LABELS.additionalServicesTitle}
                            </Typography>

                            {additionalServices.length === 0 && (
                                <Typography
                                    variant="body2"
                                    color="text.secondary">
                                    {VEHICLE_DETAILS_PAGE_LABELS.noAdditionalServices}
                                </Typography>
                            )}

                            <Box
                                sx={{
                                    display: "grid",
                                    gridTemplateColumns: {
                                        xs: "1fr"
                                    },
                                    gap: 1.25
                                }}>
                                {additionalServices.map((service) => {
                                    const isSelected = selectedAdditionalServiceIds.includes(service.id);

                                    return (
                                        <Box
                                            key={service.id}
                                            onClick={() => handleAdditionalServiceChange(service.id)}
                                            sx={{
                                                border: "1px solid",
                                                borderColor: isSelected ? "primary.main" : "divider",
                                                borderRadius: "12px",
                                                p: 1.5,
                                                cursor: "pointer",
                                                bgcolor: isSelected ? "rgba(0, 128, 128, 0.04)" : "background.paper",
                                                transition: "0.15s ease",
                                                "&:hover": {
                                                    borderColor: "primary.main",
                                                    bgcolor: isSelected ? "rgba(0, 128, 128, 0.06)" : "grey.50"
                                                }
                                            }}>
                                            <Box
                                                sx={{
                                                    display: "flex",
                                                    flexDirection: "column",
                                                    gap: 0.75
                                                }}>
                                                <Box
                                                    sx={{
                                                        display: "grid",
                                                        gridTemplateColumns: "1fr auto auto",
                                                        alignItems: "center",
                                                        gap: 1.5
                                                    }}>
                                                    <Typography
                                                        variant="body1"
                                                        sx={{
                                                            fontWeight: 700
                                                        }}>
                                                        {service.name}
                                                    </Typography>

                                                    <Typography
                                                        variant="body2"
                                                        sx={{
                                                            fontWeight: 700,
                                                            color: "primary.main",
                                                            whiteSpace: "nowrap"
                                                        }}>
                                                        +{service.pricePerDay} {VEHICLE_DETAILS_PAGE_LABELS.currency}
                                                    </Typography>

                                                    <Checkbox
                                                        checked={isSelected}
                                                        onClick={(event) => event.stopPropagation()}
                                                        onChange={() => handleAdditionalServiceChange(service.id)}
                                                        size="small"
                                                        sx={{
                                                            p: 0
                                                        }} />
                                                </Box>

                                                <Typography
                                                    variant="body2"
                                                    color="text.secondary"
                                                    sx={{
                                                        display: "-webkit-box",
                                                        WebkitLineClamp: 2,
                                                        WebkitBoxOrient: "vertical",
                                                        overflow: "hidden"
                                                    }}>
                                                    {service.description}
                                                </Typography>
                                            </Box>
                                        </Box>
                                    );
                                })}
                            </Box>
                        </Box>
                    </Box>

                    <Box
                        sx={{
                            display: "flex",
                            flexDirection: "column",
                            gap: 2
                        }}>
                        <Box>
                            <Box
                                sx={{
                                    display: "flex",
                                    alignItems: "center",
                                    gap: 1,
                                    mb: 2
                                }}>
                                <Typography
                                    variant="h6"
                                    sx={{
                                        fontWeight: 700
                                    }}>
                                    {VEHICLE_DETAILS_PAGE_LABELS.rentalPeriodTitle}
                                </Typography>

                                {hasDateError && (
                                    <Tooltip
                                        title={dateErrorMessage}
                                        arrow>
                                        <Box
                                            component="span"
                                            sx={{
                                                width: 18,
                                                height: 18,
                                                borderRadius: "50%",
                                                bgcolor: "#DC2626",
                                                color: "#ffffff",
                                                display: "inline-flex",
                                                alignItems: "center",
                                                justifyContent: "center",
                                                fontSize: 12,
                                                fontWeight: 700,
                                                cursor: "help",
                                                lineHeight: 1
                                            }}>
                                            !
                                        </Box>
                                    </Tooltip>
                                )}
                            </Box>

                            <Box
                                sx={{
                                    border: "1px solid",
                                    borderColor: "divider",
                                    borderRadius: "12px",
                                    display: "flex",
                                    justifyContent: "center",
                                    p: 1,
                                    mb: 2
                                }}>
                                <DateCalendar
                                    value={calendarValue}
                                    minDate={dayjs()}
                                    shouldDisableDate={isUnavailableDate}
                                    onChange={handleDateSelect}
                                    slots={{
                                        day: RangePickerDay
                                    }} />
                            </Box>
                        </Box>

                        <Box
                            sx={{
                                p: 2,
                                borderRadius: "12px",
                                bgcolor: "grey.50",
                                border: "1px solid",
                                borderColor: "divider"
                            }}>
                            <Typography
                                variant="h6"
                                sx={{
                                    fontWeight: 700,
                                    mb: 2
                                }}>
                                {VEHICLE_DETAILS_PAGE_LABELS.summaryTitle}
                            </Typography>

                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    mb: 1.5
                                }}>
                                <Typography
                                    color="text.secondary">
                                    {VEHICLE_DETAILS_PAGE_LABELS.totalPerDay}
                                </Typography>

                                <Typography
                                    sx={{
                                        fontWeight: 700
                                    }}>
                                    {totalPerDay} {VEHICLE_DETAILS_PAGE_LABELS.currency}
                                </Typography>
                            </Box>

                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    mb: 1.5
                                }}>
                                <Typography
                                    color="text.secondary">
                                    {VEHICLE_DETAILS_PAGE_LABELS.numberOfDays}
                                </Typography>

                                <Typography
                                    sx={{
                                        fontWeight: 700
                                    }}>
                                    {numberOfDays}
                                </Typography>
                            </Box>

                            <Divider
                                sx={{
                                    my: 2
                                }} />

                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: "center",
                                    gap: 2
                                }}>
                                <Typography
                                    variant="h6"
                                    sx={{
                                        fontWeight: 700
                                    }}>
                                    {VEHICLE_DETAILS_PAGE_LABELS.totalPrice}
                                </Typography>

                                <Typography
                                    variant="h5"
                                    sx={{
                                        fontWeight: 700,
                                        color: "primary.main"
                                    }}>
                                    {totalPrice} {VEHICLE_DETAILS_PAGE_LABELS.currency}
                                </Typography>
                            </Box>
                        </Box>

                        {reservationError && (
                            <Typography
                                variant="body2"
                                sx={{
                                    color: "error.main",
                                    fontWeight: 600
                                }}>
                                {reservationError}
                            </Typography>
                        )}

                        <Button
                            fullWidth
                            variant="contained"
                            size="large"
                            disabled={!canBuy || saving}
                            onClick={handleBuy}>
                            {saving ? "Saving..." : VEHICLE_DETAILS_PAGE_LABELS.buyButton}
                        </Button>
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
}