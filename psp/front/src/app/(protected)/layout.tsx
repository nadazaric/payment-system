"use client";

import { ReactNode, useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";
import { Box, CircularProgress } from "@mui/material";
import Navbar from "@/components/common/Navbar";
import { ROUTES } from "@/const/routes";
import { useAuthState } from "@/hooks/useAuthState";
import { UserRole } from "@/types/auth";

type ProtectedLayoutProps = {
    children: ReactNode;
};

const getAllowedRouteForRole = (role: UserRole | "") => {
    if (role === UserRole.SuperAdmin) {
        return ROUTES.superAdmin;
    }

    if (role === UserRole.MerchantAdmin) {
        return ROUTES.merchant;
    }

    return ROUTES.auth;
};

export default function ProtectedLayout({ children }: ProtectedLayoutProps) {
    const router = useRouter();
    const pathname = usePathname();
    const authState = useAuthState();

    useEffect(() => {
        if (authState.isLoading) {
            return;
        }

        if (!authState.isAuthenticated) {
            router.push(ROUTES.auth);
            return;
        }

        const allowedRoute = getAllowedRouteForRole(authState.role);

        if (!pathname.startsWith(allowedRoute)) {
            router.push(allowedRoute);
        }
    }, [
        authState.isLoading,
        authState.isAuthenticated,
        authState.role,
        pathname,
        router
    ]);

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
                merchantId={authState.merchantId} 
                role={authState.role} />

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