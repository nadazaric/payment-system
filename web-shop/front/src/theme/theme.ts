"use client";

import { createTheme } from "@mui/material/styles";

const colors = {
    primary: "#0F766E",
    primaryLight: "#5EEAD4",
    primaryDark: "#115E59",
    secondary: "#1F2937",
    textPrimary: "#111827",
    textSecondary: "#6B7280",
    background: "#F8FAFC",
    paper: "#FFFFFF",
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
        h5: {
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
        borderRadius: 6,
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
                    // maxWidth: 420,
                    boxShadow: "0px 8px 24px rgba(0, 0, 0, 0.12)",
                },
            },
        },

        // ------------------------------------------------------------ Chips
        MuiChip: {
            styleOverrides: {
                root: {
                    borderRadius: 6,
                    fontWeight: 600,
                },
            },
        },
    },
});

export default theme;