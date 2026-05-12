import dayjs from "dayjs";

import {
    ReservationHistory,
    ReservationTimeStatus
} from "@/types/reservation";

export const getReservationTimeStatus = (
    reservation: ReservationHistory
): ReservationTimeStatus | null => {
    if (reservation.paymentStatus === "FAILED") {
        return null;
    }

    const today = dayjs();
    const startDate = dayjs(reservation.startDate);
    const endDate = dayjs(reservation.endDate);

    if (today.isBefore(startDate, "day")) {
        return "UPCOMING";
    }

    if (today.isAfter(endDate, "day")) {
        return "COMPLETED";
    }

    return "ACTIVE";
};

export const getReservationTimeStatusLabel = (
    status: ReservationTimeStatus
) => {
    switch (status) {
        case "ACTIVE":
            return "Active";
        case "UPCOMING":
            return "Upcoming";
        case "COMPLETED":
            return "Completed";
        default:
            return status;
    }
};

export const getPaymentStatusLabel = (
    status: string
) => {
    switch (status) {
        case "PENDING":
            return "Pending";
        case "PAID":
            return "Paid";
        case "FAILED":
            return "Failed";
        default:
            return status;
    }
};