"use client";

import { createTheme } from "@mui/material/styles";

const colors = {
    primary: "#91d219",
    primaryLight: "#b7e85c",
    primaryDark: "#5f940c",
    secondary: "#222222",
    textPrimary: "#1f1f1f",
    textSecondary: "#666666",
    background: "#f5f5f5",
    paper: "#ffffff",
};

const theme = createTheme({
    palette: {
        mode: "light",
        primary: {
            main: colors.primary,
            light: colors.primaryLight,
            dark: colors.primaryDark,
            contrastText: "#ffffff",
        },
        secondary: {
            main: colors.secondary,
        },
        background: {
            default: colors.background,
            paper: colors.paper,
        },
        text: {
            primary: colors.textPrimary,
            secondary: colors.textSecondary,
        },
    },
    typography: {
        fontFamily: "Arial, sans-serif",
        h4: {
            fontWeight: 700,
        },
        body2: {
            color: colors.textSecondary,
        },
        button: {
            fontWeight: 600,
        },
    },
    shape: {
        borderRadius: 12,
    },
    spacing: 8,
    components: {
        // ------------------------------------------------------------ Buttons
        MuiButton: {
            defaultProps: {
                disableElevation: true,
            },
            styleOverrides: {
                root: {
                    borderRadius: 6,
                    textTransform: "capitalize",
                    fontWeight: 600,
                },
            },
        },

        // ------------------------------------------------------------ Input Fields
        MuiTextField: {
            defaultProps: {
                variant: "outlined",
                size: "medium",
            },
            styleOverrides: {
                root: {
                    marginTop: 0,
                    marginBottom: 16,
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    borderRadius: 6,
                },
            },
        },

        // ------------------------------------------------------------ Cards
        MuiCard: {
            styleOverrides: {
                root: {
                    width: "100%",
                    maxWidth: 420,
                    boxShadow: "0px 8px 24px rgba(0, 0, 0, 0.12)",
                },
            },
        },
    },
});

export default theme;