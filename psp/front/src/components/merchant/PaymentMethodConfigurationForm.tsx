"use client";

import {
    Alert,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import { MERCHANT_LABELS } from "@/const/label";
import {
    PaymentMethod,
    PaymentMethodConfigField
} from "@/types/paymentMethod";

type PaymentMethodConfigurationFormProps = {
    paymentMethod: PaymentMethod;
    configFields: PaymentMethodConfigField[];
    values: Record<string, string>;
    onValueChange: (
        fieldName: string,
        value: string
    ) => void;
};

const getFieldLabel = (fieldName: string) => {
    return fieldName
        .replace(/([A-Z])/g, " $1")
        .replace(/^./, (firstLetter) => firstLetter.toUpperCase());
};

const getInputType = (fieldType: string) => {
    if (fieldType.toUpperCase() === "PASSWORD") {
        return "password";
    }

    if (fieldType.toUpperCase() === "NUMBER") {
        return "number";
    }

    return "text";
};

export default function PaymentMethodConfigurationForm({
    paymentMethod,
    configFields,
    values,
    onValueChange
}: PaymentMethodConfigurationFormProps) {
    return (
        <>
            <Typography
                variant="h6"
                sx={{
                    fontWeight: 600,
                    mb: 2,
                    fontSize: 18
                }}>
                {paymentMethod.displayName}
            </Typography>

            <Stack spacing={2}>
                {configFields.length === 0 && (
                    <Alert severity="warning">
                        {MERCHANT_LABELS.noConfigurationFields}
                    </Alert>
                )}

                {configFields.map((field) => (
                    <TextField
                        key={field.fieldName}
                        label={getFieldLabel(field.fieldName)}
                        type={getInputType(field.fieldType)}
                        value={values[field.fieldName] || ""}
                        onChange={(event) => onValueChange(
                            field.fieldName,
                            event.target.value
                        )}
                        fullWidth
                        required />
                ))}
            </Stack>
        </>
    );
}