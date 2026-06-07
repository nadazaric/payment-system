"use client";

import { createTheme } from "@mui/material/styles";

export const portalColors = {
    primary: "#4338CA",
    primaryLight: "#E0E7FF",
    primaryDark: "#312E81",
    accent: "#F97316",
    accentLight: "#FFEDD5",
    background: "#F5F3FF",
    paper: "#FFFFFF",
    textPrimary: "#111827",
    textSecondary: "#6B7280",
    border: "#E5E7EB",
    success: "#16A34A",
    warning: "#F59E0B",
    danger: "#DC2626",
};

const theme = createTheme({
    palette: {
        mode: "light",
        primary: {
            main: portalColors.primary,
            light: portalColors.primaryLight,
            dark: portalColors.primaryDark,
            contrastText: "#FFFFFF",
        },
        secondary: {
            main: portalColors.accent,
            light: portalColors.accentLight,
            contrastText: "#FFFFFF",
        },
        success: {
            main: portalColors.success,
        },
        warning: {
            main: portalColors.warning,
        },
        error: {
            main: portalColors.danger,
        },
        background: {
            default: portalColors.background,
            paper: portalColors.paper,
        },
        text: {
            primary: portalColors.textPrimary,
            secondary: portalColors.textSecondary,
        },
    },
    typography: {
        fontFamily: "Inter, Arial, sans-serif",
        h4: {
            fontWeight: 800,
            letterSpacing: "-0.03em",
        },
        h5: {
            fontWeight: 800,
            letterSpacing: "-0.02em",
        },
        h6: {
            fontWeight: 800,
        },
        button: {
            fontWeight: 700,
            textTransform: "none",
        },
    },
    shape: {
        borderRadius: 18,
    },
    components: {
        MuiButton: {
            defaultProps: {
                disableElevation: true,
            },
            styleOverrides: {
                root: {
                    borderRadius: 14,
                    paddingLeft: 18,
                    paddingRight: 18,
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: 22,
                    border: `1px solid ${portalColors.border}`,
                    boxShadow: "0 18px 45px rgba(67, 56, 202, 0.10)",
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    borderRadius: 24,
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    borderRadius: 14,
                    backgroundColor: "#FAFAFF",
                },
            },
        },
        MuiTextField: {
            defaultProps: {
                variant: "outlined",
                size: "medium",
            },
        },
        MuiChip: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                    fontWeight: 700,
                },
            },
        },
        MuiTableCell: {
            styleOverrides: {
                head: {
                    fontWeight: 800,
                    color: portalColors.textPrimary,
                    backgroundColor: "#F8FAFC",
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    fontWeight: 600,
                    padding: "24px 32px 8px"
                }
            }
        },
        MuiDialogContent: {
            styleOverrides: {
                root: {
                    padding: "8px 32px 16px"
                }
            }
        },
        MuiDialogActions: {
            styleOverrides: {
                root: {
                    padding: "8px 32px 24px"
                }
            }
        }
    },
});

export default theme;
