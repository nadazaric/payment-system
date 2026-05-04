"use client";

import { ReactNode, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Box } from "@mui/material";
import Navbar from "@/components/common/Navbar";
import { useAuthState } from "@/hooks/useAuthState";

type ProtectedLayoutProps = {
    children: ReactNode;
};

export default function ProtectedLayout({ children }: ProtectedLayoutProps) {
    const router = useRouter();
    const authState = useAuthState();

    useEffect(() => {
        if (!authState.isLoading && !authState.isAuthenticated) {
            router.push("/auth");
        }
    }, [authState.isLoading, authState.isAuthenticated, router]);

    if (authState.isLoading) {
        return null;
    }

    if (!authState.isAuthenticated) {
        return null;
    }

    return (
        <Box sx={{ minHeight: "100vh", bgcolor: "background.default" }}>
            <Navbar username={authState.username} role={authState.role} />

            <Box
                component="main"
                sx={{
                    p: 3,
                    pt: "84px"
                }}>
                {children}
            </Box>
        </Box>
    );
}