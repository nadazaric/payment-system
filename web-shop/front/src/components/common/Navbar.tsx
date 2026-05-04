"use client";

import { useRouter, usePathname } from "next/navigation";
import { AppBar, Avatar, Box, Button, Tooltip, Toolbar, Typography } from "@mui/material";
import { notifyAuthChange } from "@/hooks/useAuthState";
import { NAVBAR_LABELS } from "@/const/label";

const navItems = [
    { label: "Home", path: "/" },
    { label: NAVBAR_LABELS.productsOption, path: "/vehicles" },
    { label: NAVBAR_LABELS.rentalHistory, path: "/rental-history" },
];

type NavbarProps = {
    username: string;
    role: string;
};

export default function Navbar({ username, role }: NavbarProps) {
    const router = useRouter();
    const pathname = usePathname();

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("user");
        notifyAuthChange();
        router.push("/auth");
    };

    return (
        <AppBar
            position="fixed"
            elevation={0}
            sx={{
                bgcolor: "background.paper",
                color: "text.primary",
                borderBottom: "1px solid",
                borderColor: "divider",
                zIndex: (theme) => theme.zIndex.drawer + 1
            }}>
            <Toolbar sx={{ minHeight: 72, display: "flex", justifyContent: "space-between" }}>
                <Box sx={{ display: "flex", alignItems: "center", gap: 4 }}>
                    <Typography variant="h5" sx={{ fontWeight: 700, color: "primary.main", cursor: "pointer" }} onClick={() => router.push("/")}>
                        {NAVBAR_LABELS.title}
                    </Typography>

                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                        {navItems.map((item) => {
                            const isActive = pathname === item.path;

                            return (
                                <Button
                                    key={item.path}
                                    onClick={() => router.push(item.path)}
                                    sx={{
                                        px: 2,
                                        py: 1,
                                        fontWeight: 600,
                                        color: isActive ? "primary.main" : "text.primary",
                                        bgcolor: isActive ? "rgba(15, 118, 110, 0.1)" : "transparent",
                                        "&:hover": {
                                            bgcolor: isActive ? "rgba(15, 118, 110, 0.14)" : "rgba(15, 118, 110, 0.06)",
                                        },
                                    }} >
                                    {item.label}
                                </Button>
                            );
                        })}
                    </Box>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <Tooltip
                        arrow
                        title={
                            <Box sx={{ textAlign: "center" }}>
                                <Typography variant="body2" sx={{ color: "#ffffff" }} >
                                    {username}
                                </Typography>

                                <Typography variant="caption">
                                    {role}
                                </Typography>
                            </Box>
                        } >
                        <Avatar sx={{ width: 36, height: 36, bgcolor: "primary.main", fontSize: 16, fontWeight: 700, cursor: "pointer" }}>
                            {username.charAt(0).toUpperCase()}
                        </Avatar>
                    </Tooltip>

                    <Button variant="outlined" onClick={handleLogout} sx={{ fontWeight: 600 }}>
                        {NAVBAR_LABELS.logoutButton}
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
}