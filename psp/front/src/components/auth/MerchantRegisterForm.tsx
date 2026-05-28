"use client";

import { useState } from "react";
import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import { registerMerchant } from "@/api/merchantApi";
import { AUTH_LABELS } from "@/const/label";
import { Severity } from "@/const/enums";
import { useNotification } from "@/components/common/NotificationProvider";
import { MerchantRegistrationResponse } from "@/types/merchant";

type MerchantRegisterFormProps = {
    onLoginClick: () => void;
};

const initialForm = {
    merchantName: "",
    currency: "",
    successUrl: "",
    failUrl: "",
    errorUrl: "",
    adminUsername: "",
    adminPassword: "",
    adminName: "",
};

export default function MerchantRegisterForm({ onLoginClick }: MerchantRegisterFormProps) {
    const { showNotification } = useNotification();

    const [form, setForm] = useState(initialForm);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [registrationResponse, setRegistrationResponse] = useState<MerchantRegistrationResponse | null>(null);

    const updateField = (field: keyof typeof form, value: string) => {
        setForm((current) => ({
            ...current,
            [field]: value,
        }));
    };

    const copyValue = async (value: string) => {
        await navigator.clipboard.writeText(value);
        showNotification(AUTH_LABELS.copied, Severity.Success);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await registerMerchant({
                ...form,
                currency: form.currency.toUpperCase(),
            });

            setRegistrationResponse(response);
        } catch {
            setError(AUTH_LABELS.registrationFailed);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Card sx={{ width: "100%" }}>
                <CardContent sx={{ p: { xs: 3, md: 5 } }}>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}>
                        <Typography
                            variant="h4"
                            component="h1"
                            gutterBottom>
                            {AUTH_LABELS.registerTitle}
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{ mb: 4 }}>
                            {AUTH_LABELS.registerSubtitle}
                        </Typography>

                        <Box
                            sx={{
                                display: "grid",
                                gridTemplateColumns: {
                                    xs: "1fr",
                                    md: "1fr 1fr",
                                },
                                gap: {
                                    xs: 2,
                                    md: 3,
                                },
                                mb: 2,
                            }}>
                            <Stack spacing={2}>
                                <TextField
                                    label={AUTH_LABELS.merchantName}
                                    value={form.merchantName}
                                    onChange={(event) => updateField("merchantName", event.target.value)}
                                    fullWidth
                                    required />

                                <TextField
                                    label={AUTH_LABELS.successUrl}
                                    value={form.successUrl}
                                    onChange={(event) => updateField("successUrl", event.target.value)}
                                    fullWidth
                                    required />

                                <TextField
                                    label={AUTH_LABELS.failUrl}
                                    value={form.failUrl}
                                    onChange={(event) => updateField("failUrl", event.target.value)}
                                    fullWidth
                                    required />

                                <TextField
                                    label={AUTH_LABELS.errorUrl}
                                    value={form.errorUrl}
                                    onChange={(event) => updateField("errorUrl", event.target.value)}
                                    fullWidth
                                    required />
                            </Stack>

                            <Stack spacing={2}>
                                <TextField
                                    label={AUTH_LABELS.currency}
                                    value={form.currency}
                                    onChange={(event) => updateField("currency", event.target.value)}
                                    fullWidth
                                    required
                                    slotProps={{ htmlInput: { maxLength: 3 } }} />

                                <TextField
                                    label={AUTH_LABELS.adminName}
                                    value={form.adminName}
                                    onChange={(event) => updateField("adminName", event.target.value)}
                                    fullWidth
                                    required />

                                <TextField
                                    label={AUTH_LABELS.username}
                                    value={form.adminUsername}
                                    onChange={(event) => updateField("adminUsername", event.target.value)}
                                    fullWidth
                                    required />

                                <TextField
                                    label={AUTH_LABELS.password}
                                    type="password"
                                    value={form.adminPassword}
                                    onChange={(event) => updateField("adminPassword", event.target.value)}
                                    fullWidth
                                    required />
                            </Stack>
                        </Box>

                        {error && (
                            <Alert
                                severity="error"
                                sx={{ mb: 2 }}>
                                {error}
                            </Alert>
                        )}

                        <Button
                            type="submit"
                            variant="contained"
                            fullWidth
                            size="large"
                            disabled={loading}
                            sx={{ mt: 1 }}>
                            {loading ? AUTH_LABELS.registering : AUTH_LABELS.registerButton}
                        </Button>

                        <Box sx={{ mt: 2, textAlign: "center" }}>
                            <Typography
                                variant="body2"
                                color="text.secondary">
                                {AUTH_LABELS.haveAccount}{" "}
                                <Button
                                    variant="text"
                                    onClick={onLoginClick}
                                    sx={{ p: 0, minWidth: "auto" }}>
                                    {AUTH_LABELS.loginButton}
                                </Button>
                            </Typography>
                        </Box>
                    </Box>
                </CardContent>
            </Card>

            <Dialog
                open={Boolean(registrationResponse)}
                maxWidth="sm"
                fullWidth>
                <DialogTitle>{AUTH_LABELS.apiKeyCreatedTitle}</DialogTitle>

                <DialogContent>
                    <Alert
                        severity="warning"
                        sx={{ mb: 3 }}>
                        {AUTH_LABELS.apiKeyCreatedDescription}
                    </Alert>

                    {registrationResponse && (
                        <Stack spacing={2}>
                            <TextField
                                label={AUTH_LABELS.merchantId}
                                value={registrationResponse.merchantId}
                                fullWidth
                                slotProps={{ input: { readOnly: true } }} />

                            <TextField
                                label={AUTH_LABELS.merchantPassword}
                                value={registrationResponse.merchantPassword}
                                fullWidth
                                multiline
                                slotProps={{ input: { readOnly: true } }} />

                            <Box
                                sx={{
                                    display: "flex",
                                    gap: 1,
                                    flexWrap: "wrap",
                                }}>
                                <Button
                                    type="button"
                                    variant="outlined"
                                    onClick={() => copyValue(registrationResponse.merchantId)}>
                                    {AUTH_LABELS.copy} {AUTH_LABELS.merchantId}
                                </Button>

                                <Button
                                    type="button"
                                    variant="outlined"
                                    onClick={() => copyValue(registrationResponse.merchantPassword)}>
                                    {AUTH_LABELS.copy} API key
                                </Button>
                            </Box>
                        </Stack>
                    )}
                </DialogContent>

                <DialogActions>
                    <Button
                        type="button"
                        variant="contained"
                        onClick={onLoginClick}>
                        {AUTH_LABELS.goToLogin}
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}