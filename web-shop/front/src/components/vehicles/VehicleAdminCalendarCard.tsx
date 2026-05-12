"use client";

import React from "react";
import dayjs, { Dayjs } from "dayjs";

import {
    Box,
    Card,
    CardContent,
    Typography
} from "@mui/material";

import { DateCalendar } from "@mui/x-date-pickers/DateCalendar";
import { PickerDay } from "@mui/x-date-pickers/PickerDay";

import { UnavailablePeriod } from "@/types/reservation";
import { VEHICLE_DETAILS_PAGE_LABELS } from "@/const/label";

type AdminCalendarDayProps = React.ComponentProps<typeof PickerDay>;

type VehicleAdminCalendarCardProps = {
    unavailablePeriods: UnavailablePeriod[];
};

export default function VehicleAdminCalendarCard({
    unavailablePeriods
}: VehicleAdminCalendarCardProps) {
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

    const AdminCalendarDay = (dayProps: AdminCalendarDayProps) => {
        const day = dayProps.day as Dayjs;
        const isReserved = isUnavailableDate(day);

        return (
            <PickerDay
                {...dayProps}
                selected={false}
                sx={{
                    cursor: "default",
                    ...(isReserved && {
                        bgcolor: "rgba(15, 118, 110, 0.16)",
                        color: "primary.main",
                        fontWeight: 700,
                        "&:hover": {
                            bgcolor: "rgba(15, 118, 110, 0.16)"
                        }
                    })
                }} />
        );
    };

    return (
        <Card
            sx={{
                width: "100%",
                maxWidth: "none",
                minWidth: 0,
                justifySelf: "stretch",
                height: "fit-content"
            }}>
            <CardContent
                sx={{
                    p: 3
                }}>
                <Typography
                    variant="h5"
                    sx={{
                        fontWeight: 700,
                        mb: 1
                    }}>
                    {VEHICLE_DETAILS_PAGE_LABELS.availabilityTitle}
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                        mb: 2
                    }}>
                    {VEHICLE_DETAILS_PAGE_LABELS.availabilitySubtitle}
                </Typography>

                <Box
                    sx={{
                        width: "100%",
                        overflow: "visible",
                        pb: 2
                    }}>
                    <DateCalendar
                        value={dayjs()}
                        readOnly
                        disableHighlightToday
                        slots={{
                            day: AdminCalendarDay
                        }}
                        sx={{
                            width: "100%",
                            maxWidth: "none",
                            height: "auto",
                            minHeight: 390,
                            overflow: "visible",
                            m: 0,
                            p: 0,

                            "& .MuiPickersCalendarHeader-root": {
                                px: 0,
                                mb: 3
                            },

                            "& .MuiDayCalendar-root": {
                                width: "100%",
                                minHeight: 310,
                                overflow: "visible"
                            },

                            "& .MuiDayCalendar-header": {
                                width: "100%",
                                display: "grid",
                                gridTemplateColumns: "repeat(7, 1fr)",
                                mb: 2
                            },

                            "& .MuiDayCalendar-weekDayLabel": {
                                width: "100%",
                                justifySelf: "center"
                            },

                            "& .MuiDayCalendar-slideTransition": {
                                minHeight: 300,
                                overflow: "visible"
                            },

                            "& .MuiPickersSlideTransition-root": {
                                minHeight: 300,
                                overflow: "visible"
                            },

                            "& .MuiDayCalendar-monthContainer": {
                                width: "100%",
                                minHeight: 300,
                                overflow: "visible"
                            },

                            "& .MuiDayCalendar-weekContainer": {
                                width: "100%",
                                display: "grid",
                                gridTemplateColumns: "repeat(7, 1fr)",
                                justifyItems: "center",
                                mb: 1.25
                            },

                            "& .MuiPickersDay-root": {
                                margin: 0
                            }
                        }} />
                </Box>
            </CardContent>
        </Card>
    );
}