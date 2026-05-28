"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
    Alert,
    Box,
    Button,
    Card,
    CardContent,
    TextField,
    Typography
} from "@mui/material";
import { loginMerchantAdmin } from "@/api/merchantApi";
import { AUTH_LABELS } from "@/const/label";
import { ROUTES } from "@/const/routes";
import { STORAGE_KEYS } from "@/const/storageKeys";
import { notifyAuthChange } from "@/hooks/useAuthState";

type MerchantLoginFormProps = {
    onRegisterClick: () => void;
};

export default function MerchantLoginForm({ onRegisterClick }: MerchantLoginFormProps) {
    const router = useRouter();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const response = await loginMerchantAdmin({ username, password });

            localStorage.setItem(STORAGE_KEYS.accessToken, response.token);
            notifyAuthChange();
            router.push(ROUTES.merchant);
        } catch {
            setError(AUTH_LABELS.invalidCredentials);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card sx={{ width: "100%" }}>
            <CardContent sx={{ p: { xs: 3, md: 4 } }}>
                <Box component="form" onSubmit={handleSubmit}>
                    <Typography
                        variant="h4"
                        component="h1"
                        gutterBottom>
                        {AUTH_LABELS.loginTitle}
                    </Typography>

                    <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ mb: 3 }}>
                        {AUTH_LABELS.loginSubtitle}
                    </Typography>

                    <TextField
                        label={AUTH_LABELS.username}
                        type="text"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        fullWidth
                        required
                        sx={{ mb: 2 }} />

                    <TextField
                        label={AUTH_LABELS.password}
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        fullWidth
                        required
                        sx={{ mb: 2 }} />

                    <Box sx={{ minHeight: 0 }}>
                        {error && (
                            <Alert
                                severity="error"
                                sx={{
                                    borderRadius: 2,
                                    mb: 2
                                }}>
                                {error}
                            </Alert>
                        )}
                    </Box>

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        size="large"
                        disabled={loading}>
                        {loading ? AUTH_LABELS.signingIn : AUTH_LABELS.loginButton}
                    </Button>

                    <Box sx={{ mt: 2, textAlign: "center" }}>
                        <Typography
                            variant="body2"
                            color="text.secondary">
                            {AUTH_LABELS.noAccount}{" "}
                            <Button
                                variant="text"
                                onClick={onRegisterClick}
                                sx={{ p: 0 }}>
                                {AUTH_LABELS.registerButton}
                            </Button>
                        </Typography>
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
}
