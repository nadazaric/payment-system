"use client";

import { useEffect, useState } from "react";
import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    CircularProgress,
    Divider,
    Snackbar,
    Stack,
    Typography,
    useMediaQuery,
    useTheme
} from "@mui/material";
import {
    getPayment,
    initiatePayment
} from "@/api/paymentApi";
import { PAYMENT_LABELS } from "@/const/label";
import {
    PaymentMethodOption,
    PaymentTransaction
} from "@/types/payment";

type PaymentPageProps = {
    paymentId: string;
};

type InfoItemProps = {
    label: string;
    value: string;
    highlight?: boolean;
};

function InfoItem({
    label,
    value,
    highlight = false
}: InfoItemProps) {
    return (
        <Box
            sx={{
                flex: 1,
                minWidth: 0,
                px: {
                    xs: 0,
                    sm: 3
                },
                py: {
                    xs: 1.5,
                    sm: 0.5
                }
            }}>
            <Typography
                variant="caption"
                color="text.secondary"
                sx={{
                    display: "block",
                    mb: 0.75,
                    fontWeight: 600,
                    letterSpacing: "0.02em"
                }}>
                {label}
            </Typography>

            <Typography
                sx={{
                    fontWeight: highlight ? 700 : 600,
                    fontSize: highlight ? "1.1rem" : "1rem"
                }}>
                {value}
            </Typography>
        </Box>
    );
}

export default function PaymentPage({
    paymentId
}: PaymentPageProps) {
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    const [payment, setPayment] = useState<PaymentTransaction | null>(null);
    const [selectedMethodCode, setSelectedMethodCode] = useState("");
    const [initiatingMethodCode, setInitiatingMethodCode] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [messageOpen, setMessageOpen] = useState(false);

    useEffect(() => {
        let ignore = false;

        const fetchPayment = async () => {
            try {
                const data = await getPayment(paymentId);

                if (!ignore) {
                    setPayment(data);
                    setSelectedMethodCode(data.selectedPaymentMethodCode || "");
                }
            } catch {
                if (!ignore) {
                    setError(PAYMENT_LABELS.loadingError);
                }
            } finally {
                if (!ignore) {
                    setLoading(false);
                }
            }
        };

        void fetchPayment();

        return () => {
            ignore = true;
        };
    }, [paymentId]);

    const paymentAlreadyInitiated = payment?.status === "INITIATED";

    const handleMethodSelect = async (paymentMethod: PaymentMethodOption) => {
        if (!payment || payment.status !== "CREATED") {
            return;
        }

        setInitiatingMethodCode(paymentMethod.code);
        setError("");

        try {
            const response = await initiatePayment(
                paymentId,
                {
                    paymentMethodCode: paymentMethod.code
                }
            );

            setSelectedMethodCode(response.selectedPaymentMethodCode);

            setPayment((currentPayment) => {
                if (!currentPayment) {
                    return currentPayment;
                }

                return {
                    ...currentPayment,
                    status: response.status,
                    selectedPaymentMethodCode: response.selectedPaymentMethodCode
                };
            });

            if (response.redirectUrl) {
                window.location.assign(response.redirectUrl);
                return;
            }

            setMessageOpen(true);
        } catch {
            setError("Failed to initiate payment.");
        }
    };

    if (loading) {
        return (
            <Box
                sx={{
                    minHeight: "100vh",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    bgcolor: "background.default"
                }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error && !payment) {
        return (
            <Box
                sx={{
                    minHeight: "100vh",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    bgcolor: "background.default",
                    p: 2
                }}>
                <Alert severity="error">
                    {error || PAYMENT_LABELS.notFound}
                </Alert>
            </Box>
        );
    }

    if (!payment) {
        return (
            <Box
                sx={{
                    minHeight: "100vh",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    bgcolor: "background.default",
                    p: 2
                }}>
                <Alert severity="error">
                    {PAYMENT_LABELS.notFound}
                </Alert>
            </Box>
        );
    }

    return (
        <Box
            sx={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                bgcolor: "background.default",
                p: {
                    xs: 2,
                    sm: 3
                }
            }}>
            <Card
                sx={{
                    width: "100%",
                    maxWidth: 760
                }}>
                <CardContent
                    sx={{
                        p: {
                            xs: 3,
                            sm: 4
                        }
                    }}>
                    <Stack spacing={4}>
                        <Box>
                            <Typography
                                variant="h4"
                                sx={{
                                    fontWeight: 800,
                                    mb: 1
                                }}>
                                {PAYMENT_LABELS.title}
                            </Typography>

                            <Typography
                                variant="body1"
                                color="text.secondary">
                                {PAYMENT_LABELS.subtitle}
                            </Typography>
                        </Box>

                        <Stack
                            direction={{
                                xs: "column",
                                sm: "row"
                            }}
                            spacing={0}
                            sx={{
                                mx: {
                                    xs: 0,
                                    sm: -3
                                }
                            }}
                            divider={
                                <Divider
                                    flexItem
                                    orientation={isMobile ? "horizontal" : "vertical"} />
                            }>
                            <InfoItem
                                label={PAYMENT_LABELS.merchant}
                                value={payment.merchantName} />

                            <InfoItem
                                label={PAYMENT_LABELS.seller}
                                value={payment.sellerDisplayName} />

                            <InfoItem
                                label={PAYMENT_LABELS.amount}
                                value={`${payment.amount} ${payment.currency}`}
                                highlight />
                        </Stack>

                        {error && (
                            <Alert severity="error">
                                {error}
                            </Alert>
                        )}

                        <Box>
                            {payment.paymentMethods.length === 0 ? (
                                <Alert severity="warning">
                                    {PAYMENT_LABELS.noPaymentMethods}
                                </Alert>
                            ) : (
                                <Stack spacing={1.5}>
                                    {payment.paymentMethods.map((paymentMethod) => {
                                        const selected = selectedMethodCode === paymentMethod.code;
                                        const currentMethodInitiating = initiatingMethodCode === paymentMethod.code;
                                        const showProcessing = currentMethodInitiating || (
                                            paymentAlreadyInitiated && selected
                                        );

                                        return (
                                            <Box
                                                key={paymentMethod.code}
                                                sx={{
                                                    border: "1px solid",
                                                    borderColor: selected ? "primary.main" : "divider",
                                                    bgcolor: selected ? "rgba(99, 91, 255, 0.04)" : "background.paper",
                                                    borderRadius: 3,
                                                    px: 2.5,
                                                    py: 2,
                                                    transition: "all 0.2s ease"
                                                }}>
                                                <Box
                                                    sx={{
                                                        display: "flex",
                                                        alignItems: {
                                                            xs: "flex-start",
                                                            sm: "center"
                                                        },
                                                        justifyContent: "space-between",
                                                        flexDirection: {
                                                            xs: "column",
                                                            sm: "row"
                                                        },
                                                        gap: 1.5
                                                    }}>
                                                    <Typography
                                                        sx={{
                                                            fontWeight: 700,
                                                            fontSize: "1.05rem"
                                                        }}>
                                                        {paymentMethod.displayName}
                                                    </Typography>

                                                    <Button
                                                        type="button"
                                                        variant={selected ? "contained" : "outlined"}
                                                        disabled={
                                                            Boolean(initiatingMethodCode)
                                                            || paymentAlreadyInitiated
                                                        }
                                                        onClick={() => handleMethodSelect(paymentMethod)}
                                                        startIcon={
                                                            showProcessing ? (
                                                                <CircularProgress
                                                                    size={16}
                                                                    color="inherit" />
                                                            ) : undefined
                                                        }
                                                        sx={{
                                                            minWidth: 132,
                                                            px: 3
                                                        }}>
                                                        {showProcessing
                                                            ? PAYMENT_LABELS.processing
                                                            : PAYMENT_LABELS.continue}
                                                    </Button>
                                                </Box>
                                            </Box>
                                        );
                                    })}
                                </Stack>
                            )}
                        </Box>
                    </Stack>
                </CardContent>
            </Card>

            <Snackbar
                open={messageOpen}
                autoHideDuration={3000}
                onClose={() => setMessageOpen(false)}
                message={PAYMENT_LABELS.methodSelected} />
        </Box>
    );
}