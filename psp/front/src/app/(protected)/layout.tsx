"use client";

import { ReactNode, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Box, CircularProgress } from "@mui/material";
import Navbar from "@/components/common/Navbar";
import { ROUTES } from "@/const/routes";
import { useAuthState } from "@/hooks/useAuthState";

type ProtectedLayoutProps = {
    children: ReactNode;
};

export default function ProtectedLayout({ children }: ProtectedLayoutProps) {
    const router = useRouter();
    const authState = useAuthState();

    useEffect(() => {
        if (!authState.isLoading && !authState.isAuthenticated) {
            router.push(ROUTES.auth);
        }
    }, [authState.isLoading, authState.isAuthenticated, router]);

    if (authState.isLoading) {
        return (
            <Box
                sx={{
                    minHeight: "100vh",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    bgcolor: "background.default"
                }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!authState.isAuthenticated) {
        return null;
    }

    return (
        <Box sx={{ minHeight: "100vh", bgcolor: "background.default", pt: 8 }}>
            <Navbar
                merchantId={authState.merchantId} />

            <Box
                component="main"
                sx={{
                    p: { xs: 2, md: 4 },
                    pt: "104px"
                }}>
                {children}
            </Box>
        </Box>
    );
}
