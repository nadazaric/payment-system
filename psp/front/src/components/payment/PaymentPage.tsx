"use client";

import { useEffect, useState } from "react";
import {
    Alert,
    Box,
    Card,
    CardContent,
    CircularProgress,
    Stack,
    Typography
} from "@mui/material";
import { getPayment } from "@/api/paymentApi";
import { PaymentTransaction } from "@/types/payment";

type PaymentPageProps = {
    paymentId: string;
};

export default function PaymentPage({ paymentId }: PaymentPageProps) {
    const [payment, setPayment] = useState<PaymentTransaction | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let ignore = false;

        const fetchPayment = async () => {
            try {
                const data = await getPayment(paymentId);

                if (!ignore) {
                    setPayment(data);
                }
            } catch {
                if (!ignore) {
                    setError("Failed to load payment.");
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

    if (error || !payment) {
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
                    {error || "Payment not found."}
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
                p: 2
            }}>
            <Card sx={{ width: "100%", maxWidth: 560 }}>
                <CardContent sx={{ p: 4 }}>
                    <Typography
                        variant="h4"
                        sx={{
                            fontWeight: 700,
                            mb: 1
                        }}>
                        Payment
                    </Typography>

                    <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ mb: 3 }}>
                        Payment method selection will be implemented in the next step.
                    </Typography>

                    <Stack spacing={1.5}>
                        <Typography>
                            <strong>Merchant:</strong> {payment.merchantName}
                        </Typography>

                        <Typography>
                            <strong>Seller:</strong> {payment.sellerDisplayName}
                        </Typography>

                        <Typography>
                            <strong>Amount:</strong> {payment.amount} {payment.currency}
                        </Typography>

                        <Typography>
                            <strong>Status:</strong> {payment.status}
                        </Typography>
                    </Stack>
                </CardContent>
            </Card>
        </Box>
    );
}