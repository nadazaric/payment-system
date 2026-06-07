"use client";

import {
    Box,
    Button,
    Stack,
    Typography
} from "@mui/material";
import { MERCHANT_LABELS } from "@/const/label";
import { SellerPaymentMethod } from "@/types/merchant";
import { PaymentMethod } from "@/types/paymentMethod";

type PaymentMethodsListProps = {
    paymentMethods: PaymentMethod[];
    sellerPaymentMethods: SellerPaymentMethod[];
    loadingCode: string;
    onConfigureClick: (paymentMethod: PaymentMethod) => void;
    onRemoveClick: (paymentMethodCode: string) => void;
};

const getSellerPaymentMethod = (
    sellerPaymentMethods: SellerPaymentMethod[],
    paymentMethodCode: string
) => {
    return sellerPaymentMethods.find((paymentMethod) => paymentMethod.code === paymentMethodCode);
};

const getStatusText = (
    addedToSeller: boolean,
    configurationRequired: boolean
) => {
    if (!addedToSeller) {
        return "";
    }

    if (configurationRequired) {
        return `${MERCHANT_LABELS.selectedForSeller} · ${MERCHANT_LABELS.reconfigurationRequired}`;
    }

    return MERCHANT_LABELS.selectedForSeller;
};

export default function PaymentMethodsList({
    paymentMethods,
    sellerPaymentMethods,
    loadingCode,
    onConfigureClick,
    onRemoveClick
}: PaymentMethodsListProps) {
    return (
        <>
            <Typography
                variant="body2"
                color="text.secondary"
                sx={{ mb: 2 }}>
            </Typography>

            <Stack spacing={2}>
                {paymentMethods.map((method) => {
                    const sellerPaymentMethod = getSellerPaymentMethod(
                        sellerPaymentMethods,
                        method.code
                    );

                    const addedToSeller = Boolean(sellerPaymentMethod);
                    const configurationRequired = Boolean(sellerPaymentMethod?.configurationRequired);
                    const statusText = getStatusText(
                        addedToSeller,
                        configurationRequired
                    );

                    return (
                        <Box
                            key={method.code}
                            sx={{
                                border: "1px solid",
                                borderColor: "divider",
                                borderRadius: 3,
                                p: 2.5
                            }}>
                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: { xs: "flex-start", sm: "center" },
                                    flexDirection: { xs: "column", sm: "row" },
                                    gap: 2
                                }}>
                                <Box>
                                    <Typography sx={{ fontWeight: 700 }}>
                                        {method.displayName}
                                    </Typography>

                                    {statusText && (
                                        <Typography
                                            variant="body2"
                                            color={configurationRequired ? "warning.main" : "text.secondary"}
                                            sx={{ mt: 0.5 }}>
                                            {statusText}
                                        </Typography>
                                    )}
                                </Box>

                                <Stack
                                    direction="row"
                                    spacing={1}
                                    useFlexGap
                                    sx={{ flexWrap: "wrap" }}>
                                    <Button
                                        type="button"
                                        variant="contained"
                                        size="small"
                                        disabled={Boolean(loadingCode) || !method.active}
                                        onClick={() => onConfigureClick(method)}>
                                        {addedToSeller
                                            ? MERCHANT_LABELS.reconfigurePaymentMethod
                                            : MERCHANT_LABELS.configurePaymentMethod}
                                    </Button>

                                    {addedToSeller && (
                                        <Button
                                            type="button"
                                            variant="outlined"
                                            color="warning"
                                            size="small"
                                            disabled={Boolean(loadingCode)}
                                            onClick={() => onRemoveClick(method.code)}>
                                            {MERCHANT_LABELS.removePaymentMethod}
                                        </Button>
                                    )}
                                </Stack>
                            </Box>
                        </Box>
                    );
                })}
            </Stack>
        </>
    );

}