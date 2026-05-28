"use client";

import { useRouter } from "next/navigation";
import {
    AppBar,
    Box,
    Button,
    Chip,
    Toolbar,
    Typography
} from "@mui/material";
import { NAVBAR_LABELS } from "@/const/label";
import { ROUTES } from "@/const/routes";
import { STORAGE_KEYS } from "@/const/storageKeys";
import { notifyAuthChange } from "@/hooks/useAuthState";

type NavbarProps = {
    merchantId: string;
};

export default function Navbar({ merchantId }: NavbarProps) {
    const router = useRouter();

    const handleLogout = () => {
        localStorage.removeItem(STORAGE_KEYS.accessToken);
        notifyAuthChange();
        router.push(ROUTES.auth);
    };

    return (
        <AppBar
            position="fixed"
            color="transparent"
            elevation={0}
            sx={{
                backdropFilter: "blur(18px)",
                bgcolor: "rgba(255, 255, 255, 0.86)",
                borderBottom: "1px solid rgba(229, 231, 235, 0.9)"
            }}>
            <Toolbar sx={{ minHeight: 72, px: { xs: 2, md: 4 } }}>
                <Box sx={{ flexGrow: 1 }}>
                    <Typography
                        variant="h6"
                        color="text.primary"
                        sx={{ lineHeight: 1.1 }}>
                        {NAVBAR_LABELS.title}
                    </Typography>

                    <Typography
                        variant="caption"
                        color="text.secondary">
                        {NAVBAR_LABELS.subtitle}
                    </Typography>
                </Box>

                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 1.5
                    }}>
                    {merchantId && (
                        <Chip
                            label={merchantId}
                            color="primary"
                            variant="outlined" />
                    )}

                    <Button
                        type="button"
                        variant="contained"
                        color="secondary"
                        onClick={handleLogout}>
                        {NAVBAR_LABELS.logout}
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
}
