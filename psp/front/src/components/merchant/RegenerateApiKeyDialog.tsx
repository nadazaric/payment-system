"use client";

import { useState } from "react";
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
import { regenerateMerchantPassword } from "@/api/merchantApi";
import { useNotification } from "@/components/common/NotificationProvider";
import { Severity } from "@/const/enums";
import { AUTH_LABELS, MERCHANT_LABELS } from "@/const/label";

type RegenerateApiKeyDialogProps = {
    open: boolean;
    onClose: () => void;
};

export default function RegenerateApiKeyDialog({ open, onClose }: RegenerateApiKeyDialogProps) {
    const { showNotification } = useNotification();
    const [newApiKey, setNewApiKey] = useState("");
    const [loading, setLoading] = useState(false);

    const handleRegenerate = async () => {
        setLoading(true);

        try {
            const response = await regenerateMerchantPassword();
            setNewApiKey(response.merchantPassword);
            showNotification(MERCHANT_LABELS.apiKeyRegenerated, Severity.Success);
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        setNewApiKey("");
        onClose();
    };

    const handleCopy = async () => {
        await navigator.clipboard.writeText(newApiKey);
        showNotification(AUTH_LABELS.copied, Severity.Success);
    };

    return (
        <Dialog
            open={open}
            onClose={loading ? undefined : handleClose}
            maxWidth="sm"
            fullWidth>
            <DialogTitle>{MERCHANT_LABELS.regenerateApiKey}</DialogTitle>
            <DialogContent>
                {!newApiKey && (
                    <Alert severity="warning">
                        {MERCHANT_LABELS.regenerateWarning}
                    </Alert>
                )}

                {newApiKey && (
                    <Stack spacing={2}>
                        <Alert severity="warning">
                            {AUTH_LABELS.apiKeyCreatedDescription}
                        </Alert>

                        <TextField
                            label={MERCHANT_LABELS.newApiKeyTitle}
                            value={newApiKey}
                            fullWidth
                            multiline
                            slotProps={{
                                htmlInput: {
                                    readOnly: true
                                }
                            }} />

                        <Button
                            type="button"
                            variant="outlined"
                            onClick={handleCopy}>
                            {AUTH_LABELS.copy}
                        </Button>
                    </Stack>
                )}
            </DialogContent>
            <DialogActions>
                <Button
                    type="button"
                    onClick={handleClose}
                    disabled={loading}>
                    {MERCHANT_LABELS.close}
                </Button>

                {!newApiKey && (
                    <Button
                        type="button"
                        variant="contained"
                        color="secondary"
                        onClick={handleRegenerate}
                        disabled={loading}>
                        {loading ? MERCHANT_LABELS.saving : MERCHANT_LABELS.regenerateApiKey}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
}
