"use client";

import {
    Box,
    Button,
    Card,
    CardContent,
    Stack,
    Typography
} from "@mui/material";
import { useRouter } from "next/navigation";

import { PAYMENT_RESULT_PAGE_LABELS } from "@/const/label";

type PaymentResultType = "success" | "failed" | "error";

type PaymentResultPageContentProps = {
    resultType: PaymentResultType;
};

const resultConfig = {
    success: {
        title: PAYMENT_RESULT_PAGE_LABELS.successTitle,
        description: PAYMENT_RESULT_PAGE_LABELS.successDescription,
        chipLabel: "SUCCESS",
        chipColor: "success" as const
    },
    failed: {
        title: PAYMENT_RESULT_PAGE_LABELS.failedTitle,
        description: PAYMENT_RESULT_PAGE_LABELS.failedDescription,
        chipLabel: "FAILED",
        chipColor: "warning" as const
    },
    error: {
        title: PAYMENT_RESULT_PAGE_LABELS.errorTitle,
        description: PAYMENT_RESULT_PAGE_LABELS.errorDescription,
        chipLabel: "ERROR",
        chipColor: "error" as const
    }
};

export default function PaymentResultPageContent({
    resultType
}: PaymentResultPageContentProps) {
    const router = useRouter();

    const config = resultConfig[resultType];

    return (
        <Box
            sx={{
                minHeight: "calc(100vh - 120px)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center"
            }}>
            <Card
                sx={{
                    width: "100%",
                    maxWidth: 680,
                    borderRadius: 4,
                    boxShadow: "0 16px 50px rgba(15, 23, 42, 0.10)"
                }}>
                <CardContent sx={{ p: 5 }}>
                    <Stack spacing={3}>
                        <Stack spacing={1.5}>
                            <Typography
                                variant="h4"
                                sx={{ fontWeight: 800 }}>
                                {config.title}
                            </Typography>

                            <Typography
                                color="text.secondary"
                                sx={{
                                    fontSize: 18,
                                    lineHeight: 1.6
                                }}>
                                {config.description}
                            </Typography>
                        </Stack>

                        <Stack
                            direction={{
                                xs: "column",
                                sm: "row"
                            }}
                            spacing={1.5}>
                            <Button
                                variant="contained"
                                onClick={() => router.push("/rental-history")}>
                                {PAYMENT_RESULT_PAGE_LABELS.viewHistoryButton}
                            </Button>

                            <Button
                                variant="outlined"
                                onClick={() => router.push("/vehicles")}>
                                {PAYMENT_RESULT_PAGE_LABELS.vehiclesButton}
                            </Button>
                        </Stack>
                    </Stack>
                </CardContent>
            </Card>
        </Box>
    );
}