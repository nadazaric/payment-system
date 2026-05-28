"use client";

import { useState } from "react";
import axios from "axios";
import {
    Alert,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField
} from "@mui/material";
import { createMerchantSeller, updateMerchantSeller } from "@/api/merchantApi";
import { useNotification } from "@/components/common/NotificationProvider";
import { Severity } from "@/const/enums";
import { MERCHANT_LABELS } from "@/const/label";
import {
    CreateMerchantSellerAccountRequest,
    MerchantSellerAccount,
    UpdateMerchantSellerAccountRequest
} from "@/types/merchant";

type SellerDialogProps = {
    open: boolean;
    seller: MerchantSellerAccount | null;
    onClose: () => void;
    onSaved: () => void;
};

type SellerDialogContentProps = {
    seller: MerchantSellerAccount | null;
    onClose: () => void;
    onSaved: () => void;
};

const emptyForm = {
    sellerReference: "",
    displayName: "",
};

const getInitialForm = (seller: MerchantSellerAccount | null) => {
    if (!seller) {
        return emptyForm;
    }

    return {
        sellerReference: seller.sellerReference,
        displayName: seller.displayName,
    };
};

const getErrorMessage = (error: unknown) => {
    if (axios.isAxiosError(error)) {
        return error.response?.data?.message || MERCHANT_LABELS.saveFailed;
    }

    return MERCHANT_LABELS.saveFailed;
};

export default function SellerDialog({
    open,
    seller,
    onClose,
    onSaved
}: SellerDialogProps) {
    if (!open) {
        return null;
    }

    return (
        <SellerDialogContent
            key={seller?.id || "create-seller"}
            seller={seller}
            onClose={onClose}
            onSaved={onSaved} />
    );
}

function SellerDialogContent({
    seller,
    onClose,
    onSaved
}: SellerDialogContentProps) {
    const { showNotification } = useNotification();

    const [form, setForm] = useState(() => getInitialForm(seller));
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const isEditMode = Boolean(seller);

    const updateField = (field: keyof typeof emptyForm, value: string) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));
    };

    const handleClose = () => {
        if (!loading) {
            setError("");
            onClose();
        }
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        setError("");
        setLoading(true);

        try {
            if (seller) {
                const request: UpdateMerchantSellerAccountRequest = form;
                await updateMerchantSeller(seller.id, request);
            } else {
                const request: CreateMerchantSellerAccountRequest = form;
                await createMerchantSeller(request);
            }

            showNotification(MERCHANT_LABELS.savedSuccessfully, Severity.Success);
            onSaved();
            onClose();
        } catch (error) {
            const message = getErrorMessage(error);

            setError(message);
            showNotification(message, Severity.Error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog
            open
            onClose={handleClose}
            maxWidth="sm"
            fullWidth>
            <DialogTitle>
                {isEditMode ? MERCHANT_LABELS.editSellerTitle : MERCHANT_LABELS.createSellerTitle}
            </DialogTitle>

            <DialogContent>
                <Stack
                    component="form"
                    id="seller-form"
                    onSubmit={handleSubmit}
                    spacing={2}
                    sx={{ pt: 1 }}>
                    {error && (
                        <Alert severity="error">
                            {error}
                        </Alert>
                    )}

                    <TextField
                        label={MERCHANT_LABELS.sellerReference}
                        value={form.sellerReference}
                        onChange={(event) => updateField("sellerReference", event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={MERCHANT_LABELS.displayName}
                        value={form.displayName}
                        onChange={(event) => updateField("displayName", event.target.value)}
                        fullWidth
                        required />
                </Stack>
            </DialogContent>

            <DialogActions>
                <Button
                    type="button"
                    onClick={handleClose}
                    disabled={loading}>
                    {MERCHANT_LABELS.cancel}
                </Button>

                <Button
                    type="submit"
                    form="seller-form"
                    variant="contained"
                    disabled={loading}>
                    {loading ? MERCHANT_LABELS.saving : MERCHANT_LABELS.save}
                </Button>
            </DialogActions>
        </Dialog>
    );
}