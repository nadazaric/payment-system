"use client";

import { useState } from "react";
import { Box, Container } from "@mui/material";
import MerchantLoginForm from "@/components/auth/MerchantLoginForm";
import MerchantRegisterForm from "@/components/auth/MerchantRegisterForm";

export default function AuthPage() {
    const [isRegisterMode, setIsRegisterMode] = useState(false);

    return (
        <Box
            sx={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                bgcolor: "background.default",
                backgroundImage: "radial-gradient(circle at top left, rgba(249, 115, 22, 0.20), transparent 32%), radial-gradient(circle at bottom right, rgba(67, 56, 202, 0.24), transparent 34%)",
                py: 4
            }}>
            <Container maxWidth="lg">
                <Box
                    sx={{
                        display: "grid",
                        width: "100%",
                        gridTemplateColumns: "1fr",
                        alignItems: "center",
                        justifyItems: "center",
                    }}>
                    {isRegisterMode ? (
                        <Box
                            sx={{
                                width: "100%",
                            }}>
                            <MerchantRegisterForm
                                onLoginClick={() => setIsRegisterMode(false)} />
                        </Box>
                    ) : (
                        <Box
                            sx={{
                                width: "100%",
                                maxWidth: 560,
                            }}>
                            <MerchantLoginForm
                                onRegisterClick={() => setIsRegisterMode(true)} />
                        </Box>
                    )}
                </Box>
            </Container>
        </Box>
    );
}
