"use client";

import { useMemo, useState } from "react";
import axios from "axios";
import {
    Alert,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle
} from "@mui/material";
import {
    configureSellerPaymentMethod,
    removeSellerPaymentMethod
} from "@/api/merchantApi";
import PaymentMethodConfigurationForm from "@/components/merchant/PaymentMethodConfigurationForm";
import PaymentMethodsList from "@/components/merchant/PaymentMethodsList";
import { MERCHANT_LABELS } from "@/const/label";
import {
    MerchantSellerAccount,
    SellerPaymentMethod
} from "@/types/merchant";
import {
    PaymentMethod,
    PaymentMethodConfigField
} from "@/types/paymentMethod";

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

const parseConfigFields = (configSchemaJson: string): PaymentMethodConfigField[] => {
    if (!configSchemaJson) {
        return [];
    }

    try {
        const parsedValue = JSON.parse(configSchemaJson);

        if (!Array.isArray(parsedValue)) {
            return [];
        }

        return parsedValue.filter((field) => Boolean(field.fieldName));
    } catch {
        return [];
    }
};

const getErrorMessage = (error: unknown) => {
    if (axios.isAxiosError(error)) {
        return error.response?.data?.message || MERCHANT_LABELS.saveFailed;
    }

    return MERCHANT_LABELS.saveFailed;
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
    const [selectedMethod, setSelectedMethod] = useState<PaymentMethod | null>(null);
    const [configurationValues, setConfigurationValues] = useState<Record<string, string>>({});
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [loadingCode, setLoadingCode] = useState("");

    const sellerPaymentMethods = useMemo(() => {
        return seller.paymentMethods ?? [];
    }, [seller.paymentMethods]);

    const selectedConfigFields = useMemo(() => {
        if (!selectedMethod) {
            return [];
        }

        return parseConfigFields(selectedMethod.configSchemaJson);
    }, [selectedMethod]);

    const handleClose = () => {
        if (loading || loadingCode) {
            return;
        }

        setSelectedMethod(null);
        setConfigurationValues({});
        setError("");
        onClose();
    };

    const openConfigurationView = (paymentMethod: PaymentMethod) => {
        const configFields = parseConfigFields(paymentMethod.configSchemaJson);

        const initialValues = configFields.reduce<Record<string, string>>(
            (values, field) => ({
                ...values,
                [field.fieldName]: "",
            }),
            {}
        );

        setSelectedMethod(paymentMethod);
        setConfigurationValues(initialValues);
        setError("");
    };

    const closeConfigurationView = () => {
        if (loading) {
            return;
        }

        setSelectedMethod(null);
        setConfigurationValues({});
        setError("");
    };

    const updateConfigurationValue = (
        fieldName: string,
        value: string
    ) => {
        setConfigurationValues((current) => ({
            ...current,
            [fieldName]: value,
        }));
    };

    const validateConfigurationValues = () => {
        return selectedConfigFields.every((field) => {
            const value = configurationValues[field.fieldName];

            return value !== undefined && value.trim() !== "";
        });
    };

    const handleConfigure = async () => {
        if (!selectedMethod) {
            return;
        }

        if (selectedConfigFields.length === 0) {
            setError(MERCHANT_LABELS.noConfigurationFields);
            return;
        }

        if (!validateConfigurationValues()) {
            setError(MERCHANT_LABELS.missingConfigurationValues);
            return;
        }

        setLoading(true);
        setError("");

        try {
            await configureSellerPaymentMethod(
                seller.id,
                selectedMethod.code,
                {
                    values: configurationValues,
                }
            );

            onSaved();
        } catch (error) {
            setError(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    };

    const handleRemove = async (paymentMethodCode: string) => {
        setLoadingCode(paymentMethodCode);
        setError("");

        try {
            await removeSellerPaymentMethod(
                seller.id,
                paymentMethodCode
            );

            onSaved();
        } catch (error) {
            setError(getErrorMessage(error));
        } finally {
            setLoadingCode("");
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth="md"
            fullWidth>
            <DialogTitle>
                {`${selectedMethod
                    ? MERCHANT_LABELS.paymentMethodConfigurationTitle
                    : MERCHANT_LABELS.configurePaymentMethodsTitle} • ${seller.displayName}`}
            </DialogTitle>

            <DialogContent>
                {selectedMethod ? (
                    <PaymentMethodConfigurationForm
                        paymentMethod={selectedMethod}
                        configFields={selectedConfigFields}
                        values={configurationValues}
                        onValueChange={updateConfigurationValue} />
                ) : (
                    <PaymentMethodsList
                        paymentMethods={paymentMethods}
                        sellerPaymentMethods={sellerPaymentMethods}
                        loadingCode={loadingCode}
                        onConfigureClick={openConfigurationView}
                        onRemoveClick={handleRemove} />
                )}

                {error && (
                    <Alert
                        severity="error"
                        sx={{ mt: 2 }}>
                        {error}
                    </Alert>
                )}
            </DialogContent>

            <DialogActions>
                {selectedMethod ? (
                    <>
                        <Button
                            type="button"
                            onClick={closeConfigurationView}
                            disabled={loading}>
                            {MERCHANT_LABELS.back}
                        </Button>

                        <Button
                            type="button"
                            variant="contained"
                            onClick={handleConfigure}
                            disabled={loading || selectedConfigFields.length === 0}>
                            {loading ? MERCHANT_LABELS.saving : MERCHANT_LABELS.save}
                        </Button>
                    </>
                ) : (
                    <Button
                        type="button"
                        onClick={handleClose}
                        disabled={Boolean(loadingCode)}>
                        {MERCHANT_LABELS.close}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
}