"use client";

import { createContext, ReactNode, useContext, useState } from "react";
import { Alert, Snackbar } from "@mui/material";
import { Severity } from "@/const/enums";

type NotificationContextType = {
    showNotification: (message: string, severity?: Severity) => void;
};

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

type NotificationProviderProps = {
    children: ReactNode;
};

export function NotificationProvider({ children }: NotificationProviderProps) {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState("");
    const [severity, setSeverity] = useState<Severity>(Severity.Success);

    const showNotification = (message: string, severity: Severity = Severity.Success) => {
        setMessage(message);
        setSeverity(severity);
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <NotificationContext.Provider value={{ showNotification }}>
            {children}

            <Snackbar
                open={open}
                autoHideDuration={2500}
                onClose={handleClose}
                anchorOrigin={{ vertical: "bottom", horizontal: "center" }} >
                <Alert severity={severity} variant="filled" onClose={handleClose}>
                    {message}
                </Alert>
            </Snackbar>
        </NotificationContext.Provider>
    );
}

export function useNotification() {
    const context = useContext(NotificationContext);

    if (!context) {
        throw new Error("useNotification must be used inside NotificationProvider");
    }

    return context;
}