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
import { createMerchantSeller, updateMerchantSeller } from "@/api/merchantApi";
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

const emptyForm = {
    sellerReference: "",
    displayName: "",
};

export default function SellerDialog({
    open,
    seller,
    onClose,
    onSaved
}: SellerDialogProps) {
    const [form, setForm] = useState(emptyForm);
    const [loading, setLoading] = useState(false);

    const isEditMode = Boolean(seller);

    useEffect(() => {
        if (seller) {
            setForm({
                sellerReference: seller.sellerReference,
                displayName: seller.displayName,
            });
        } else {
            setForm(emptyForm);
        }
    }, [seller, open]);

    const updateField = (field: keyof typeof emptyForm, value: string) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setLoading(true);

        try {
            if (seller) {
                const request: UpdateMerchantSellerAccountRequest = form;
                await updateMerchantSeller(seller.id, request);
            } else {
                const request: CreateMerchantSellerAccountRequest = form;
                await createMerchantSeller(request);
            }

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
                    onClick={onClose}
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
