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
    TextField,
    Typography
} from "@mui/material";
import { createExpectedPlugin } from "@/api/pluginApi";
import { SUPER_ADMIN_LABELS } from "@/const/label";
import {
    CreateExpectedPluginRequest,
    CreateExpectedPluginResponse
} from "@/types/plugin";

type CreatePluginDialogProps = {
    open: boolean;
    onClose: () => void;
    onCreated: (response: CreateExpectedPluginResponse) => void;
};

const emptyForm = {
    pluginCode: "",
    displayName: "",
};

const getErrorMessage = (error: unknown) => {
    if (axios.isAxiosError(error)) {
        return error.response?.data?.message || SUPER_ADMIN_LABELS.saveFailed;
    }

    return SUPER_ADMIN_LABELS.saveFailed;
};

export default function CreatePluginDialog({
    open,
    onClose,
    onCreated
}: CreatePluginDialogProps) {
    const [form, setForm] = useState(emptyForm);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const updateField = (field: keyof typeof emptyForm, value: string) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));
    };

    const handleClose = () => {
        if (!loading) {
            setForm(emptyForm);
            setError("");
            onClose();
        }
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        setError("");
        setLoading(true);

        try {
            const request: CreateExpectedPluginRequest = {
                pluginCode: form.pluginCode,
                displayName: form.displayName,
            };

            const response = await createExpectedPlugin(request);

            setForm(emptyForm);
            onCreated(response);
        } catch (error) {
            setError(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth="sm"
            fullWidth>
            <DialogTitle>
                {SUPER_ADMIN_LABELS.createPluginTitle}
            </DialogTitle>

            <DialogContent>
                <Stack
                    component="form"
                    id="create-plugin-form"
                    onSubmit={handleSubmit}
                    spacing={2}
                    sx={{ pt: 1 }}>
                    <Typography
                        variant="body2"
                        color="text.secondary">
                        {SUPER_ADMIN_LABELS.createPluginDescription}
                    </Typography>

                    {error && (
                        <Alert severity="error">
                            {error}
                        </Alert>
                    )}

                    <TextField
                        label={SUPER_ADMIN_LABELS.pluginCode}
                        value={form.pluginCode}
                        onChange={(event) => updateField("pluginCode", event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={SUPER_ADMIN_LABELS.displayName}
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
                    {SUPER_ADMIN_LABELS.cancel}
                </Button>

                <Button
                    type="submit"
                    form="create-plugin-form"
                    variant="contained"
                    disabled={loading}>
                    {loading ? SUPER_ADMIN_LABELS.creating : SUPER_ADMIN_LABELS.create}
                </Button>
            </DialogActions>
        </Dialog>
    );
    
}