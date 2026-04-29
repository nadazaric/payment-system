"use client";

import { useState } from "react";
import { Box } from "@mui/material";
import LoginForm from "@/components/auth/LoginForm";
import RegisterForm from "@/components/auth/RegisterForm";

export default function AuthPage() {
    const [isRegisterMode, setIsRegisterMode] = useState(false);

    return (
        <Box sx={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", bgcolor: "background.default", px: 2 }}>
            {isRegisterMode ? (
                <RegisterForm onLoginClick={() => setIsRegisterMode(false)} />
            ) : (
                <LoginForm onRegisterClick={() => setIsRegisterMode(true)} />
            )}
        </Box>
    );
}