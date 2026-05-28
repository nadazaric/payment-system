"use client";

import { useMemo, useState } from "react";
import {
    Alert,
    Button,
    Checkbox,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControlLabel,
    Stack,
    Typography
} from "@mui/material";
import { updateSellerPaymentMethods } from "@/api/merchantApi";
import { MERCHANT_LABELS } from "@/const/label";
import { MerchantSellerAccount } from "@/types/merchant";
import { PaymentMethod } from "@/types/paymentMethod";

type PaymentMethodsDialogProps = {
    open: boolean;
    seller: MerchantSellerAccount | null;
    paymentMethods: PaymentMethod[];
    onClose: () => void;
    onSaved: () => void;
};

type PaymentMethodsDialogContentProps = {
    open: boolean;
    seller: MerchantSellerAccount;
    paymentMethods: PaymentMethod[];
    onClose: () => void;
    onSaved: () => void;
};

export default function PaymentMethodsDialog({
    open,
    seller,
    paymentMethods,
    onClose,
    onSaved
}: PaymentMethodsDialogProps) {
    if (!open || !seller) {
        return null;
    }

    return (
        <PaymentMethodsDialogContent
            key={seller.id}
            open={open}
            seller={seller}
            paymentMethods={paymentMethods}
            onClose={onClose}
            onSaved={onSaved} />
    );
}

function PaymentMethodsDialogContent({
    open,
    seller,
    paymentMethods,
    onClose,
    onSaved
}: PaymentMethodsDialogContentProps) {
    const [selectedCodes, setSelectedCodes] = useState<string[]>(
        () => seller.availablePaymentMethods.map((method) => method.code)
    );
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const sellerTitle = useMemo(() => {
        return `${seller.displayName} (${seller.sellerReference})`;
    }, [seller.displayName, seller.sellerReference]);

    const handleToggle = (code: string) => {
        setSelectedCodes((current) => {
            if (current.includes(code)) {
                return current.filter((item) => item !== code);
            }

            return [...current, code];
        });
    };

    const handleSave = async () => {
        if (selectedCodes.length === 0) {
            setError(MERCHANT_LABELS.configurePaymentMethodsDescription);
            return;
        }

        setLoading(true);
        setError("");

        try {
            await updateSellerPaymentMethods(seller.id, {
                paymentMethodCodes: selectedCodes,
            });

            onSaved();
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={loading ? undefined : onClose}
            maxWidth="sm"
            fullWidth>
            <DialogTitle>{MERCHANT_LABELS.configurePaymentMethodsTitle}</DialogTitle>

            <DialogContent>
                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mb: 2 }}>
                    {sellerTitle}
                </Typography>

                <Alert
                    severity="info"
                    sx={{ mb: 2 }}>
                    {MERCHANT_LABELS.configurePaymentMethodsDescription}
                </Alert>

                <Stack spacing={1}>
                    {paymentMethods.map((method) => (
                        <FormControlLabel
                            key={method.code}
                            control={
                                <Checkbox
                                    checked={selectedCodes.includes(method.code)}
                                    onChange={() => handleToggle(method.code)} />
                            }
                            label={method.displayName} />
                    ))}
                </Stack>

                {error && (
                    <Alert
                        severity="error"
                        sx={{ mt: 2 }}>
                        {error}
                    </Alert>
                )}
            </DialogContent>

            <DialogActions>
                <Button
                    type="button"
                    onClick={onClose}
                    disabled={loading}>
                    {MERCHANT_LABELS.cancel}
                </Button>

                <Button
                    type="button"
                    variant="contained"
                    onClick={handleSave}
                    disabled={loading}>
                    {loading ? MERCHANT_LABELS.saving : MERCHANT_LABELS.save}
                </Button>
            </DialogActions>
        </Dialog>
    );
}