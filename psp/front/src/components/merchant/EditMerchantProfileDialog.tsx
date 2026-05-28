"use client";

import { useEffect, useState } from "react";
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField
} from "@mui/material";
import { updateMerchantProfile } from "@/api/merchantApi";
import { MERCHANT_LABELS } from "@/const/label";
import { MerchantProfile, UpdateMerchantProfileRequest } from "@/types/merchant";

type EditMerchantProfileDialogProps = {
    open: boolean;
    profile: MerchantProfile | null;
    onClose: () => void;
    onSaved: () => void;
};

const emptyForm: UpdateMerchantProfileRequest = {
    merchantName: "",
    currency: "",
    successUrl: "",
    failUrl: "",
    errorUrl: "",
};

export default function EditMerchantProfileDialog({
    open,
    profile,
    onClose,
    onSaved
}: EditMerchantProfileDialogProps) {
    const [form, setForm] = useState<UpdateMerchantProfileRequest>(emptyForm);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (profile) {
            setForm({
                merchantName: profile.merchantName,
                currency: profile.currency,
                successUrl: profile.successUrl,
                failUrl: profile.failUrl,
                errorUrl: profile.errorUrl,
            });
        }
    }, [profile]);

    const updateField = (field: keyof UpdateMerchantProfileRequest, value: string) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setLoading(true);

        try {
            await updateMerchantProfile({
                ...form,
                currency: form.currency.toUpperCase(),
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
            <DialogTitle>{MERCHANT_LABELS.editProfileTitle}</DialogTitle>
            <DialogContent>
                <Stack
                    component="form"
                    id="edit-merchant-profile-form"
                    onSubmit={handleSubmit}
                    spacing={2}
                    sx={{ pt: 1 }}>
                    <TextField
                        label={MERCHANT_LABELS.merchantName}
                        value={form.merchantName}
                        onChange={(event) => updateField("merchantName", event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={MERCHANT_LABELS.currency}
                        value={form.currency}
                        onChange={(event) => updateField("currency", event.target.value)}
                        fullWidth
                        required
                        slotProps={{ htmlInput: { maxLength: 3 } }} />

                    <TextField
                        label={MERCHANT_LABELS.successUrl}
                        value={form.successUrl}
                        onChange={(event) => updateField("successUrl", event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={MERCHANT_LABELS.failUrl}
                        value={form.failUrl}
                        onChange={(event) => updateField("failUrl", event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={MERCHANT_LABELS.errorUrl}
                        value={form.errorUrl}
                        onChange={(event) => updateField("errorUrl", event.target.value)}
                        fullWidth
                        required />
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button
                    type="button"
                    onClick={onClose}
                    disabled={loading}>
                    {MERCHANT_LABELS.cancel}
                </Button>

                <Button
                    type="submit"
                    form="edit-merchant-profile-form"
                    variant="contained"
                    disabled={loading}>
                    {loading ? MERCHANT_LABELS.saving : MERCHANT_LABELS.save}
                </Button>
            </DialogActions>
        </Dialog>
    );
}
