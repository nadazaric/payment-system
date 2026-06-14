import dayjs from "dayjs";

import {
    PaymentStatus,
    ReservationHistory,
    ReservationTimeStatus
} from "@/types/reservation";

export const isFinalFailedPaymentStatus = (
    status: PaymentStatus
) => {
    return status === "FAILED" || status === "ERROR";
};

export const isPaymentConfirmed = (
    status: PaymentStatus
) => {
    return status === "SUCCESS";
};

export const isPaymentPending = (
    status: PaymentStatus
) => {
    return status === "CREATED" || status === "INITIATED";
};

export const getReservationTimeStatus = (
    reservation: ReservationHistory
): ReservationTimeStatus | null => {
    if (!isPaymentConfirmed(reservation.paymentStatus)) {
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
    status: PaymentStatus
) => {
    switch (status) {
        case "CREATED":
            return "Created";
        case "INITIATED":
            return "Payment pending";
        case "SUCCESS":
            return "Paid";
        case "FAILED":
            return "Payment failed";
        case "ERROR":
            return "Payment error";
        case "EXPIRED":
            return "Expired";
        case "CANCELLED":
            return "Cancelled";
        default:
            return status;
    }
};

export const getPaymentMethodCodeLabel = (
    status: string
) => {
    switch (status) {
        case "CARD":
            return "Card";
        case "QR_CODE":
            return "QR";
        default:
            return status;
    }
};