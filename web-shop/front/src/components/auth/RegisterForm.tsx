"use client";

import { useState } from "react";
import { Alert, Box, Button, Card, CardContent, TextField, Typography } from "@mui/material";
import { register } from "@/api/authApi";
import { AUTH_LABELS } from "@/const/label";
import { useNotification } from "@/components/common/NotificationProvider";
import { Severity } from "@/const/enums";

type RegisterFormProps = {
    onLoginClick: () => void;
};

export default function RegisterForm({ onLoginClick }: RegisterFormProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { showNotification } = useNotification();

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError("");

        if (password !== confirmPassword) {
            setError(AUTH_LABELS.passwordsDoNotMatch);
            return;
        }

        setLoading(true);

        try {
            await register({ username, password, email, name });
            showNotification(AUTH_LABELS.registrationSuccessful, Severity.Success);
            onLoginClick();
        } catch {
            setError(AUTH_LABELS.alreadyExists);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card>
            <CardContent sx={{ p: 4 }}>
                <Box component="form" onSubmit={handleSubmit}>
                    <Typography
                        variant="h4"
                        component="h1"
                        gutterBottom >
                        {AUTH_LABELS.registerTitle}
                    </Typography>

                    <Typography
                        variant="body2"
                        sx={{ mb: 2 }} >
                        {AUTH_LABELS.registerSubtitle}
                    </Typography>

                    <TextField
                        label={AUTH_LABELS.name}
                        type="text"
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={AUTH_LABELS.email}
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={AUTH_LABELS.username}
                        type="text"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={AUTH_LABELS.password}
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        fullWidth
                        required />

                    <TextField
                        label={AUTH_LABELS.confirmPassword}
                        type="password"
                        value={confirmPassword}
                        onChange={(event) => setConfirmPassword(event.target.value)}
                        fullWidth
                        required />

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
                        {loading ? AUTH_LABELS.registering : AUTH_LABELS.registerButton}
                    </Button>

                    <Box sx={{ mt: 2, textAlign: "center" }}>
                        <Typography variant="body2" >
                            {AUTH_LABELS.haveAccount}{" "}
                            <Button variant="text" onClick={onLoginClick} sx={{ p: 0, minWidth: "auto" }}>
                                {AUTH_LABELS.loginButton}
                            </Button>
                        </Typography>
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
}