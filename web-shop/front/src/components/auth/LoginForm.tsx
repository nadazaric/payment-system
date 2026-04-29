"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Alert, Box, Button, Card, CardContent, TextField, Typography } from "@mui/material";
import { login } from "@/api/authApi";
import { AUTH_LABELS } from "@/const/label";

export default function LoginForm() {
    const router = useRouter();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await login({ username, password });
            localStorage.setItem("username", response.username);
            localStorage.setItem("accessToken", response.accessToken);
            router.push("/");
        } catch {
            setError(AUTH_LABELS.invalidCredentials);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card sx={{ maxWidth: 420 }}>
            <CardContent sx={{ p: 4 }}>
                <Box component="form" onSubmit={handleSubmit}>
                    <Typography
                        variant="h4"
                        component="h1"
                        gutterBottom >
                        {AUTH_LABELS.loginTitle}
                    </Typography>

                    <Typography
                        variant="body2"
                        sx={{ mb: 2 }} >
                        {AUTH_LABELS.loginSubtitle}
                    </Typography>

                    <TextField
                        label={AUTH_LABELS.username}
                        type="text"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        fullWidth
                        required
                        margin="normal" />

                    <TextField
                        label={AUTH_LABELS.password}
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        fullWidth
                        required
                        margin="normal" />

                    {error && (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        size="large"
                        disabled={loading} >
                        {loading ? AUTH_LABELS.signingIn : AUTH_LABELS.loginButton}
                    </Button>
                </Box>
            </CardContent>
        </Card>
    );
}