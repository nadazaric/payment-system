"use client";

import { useMemo, useSyncExternalStore } from "react";
import { jwtDecode } from "jwt-decode";
import { STORAGE_KEYS } from "@/const/storageKeys";
import { JwtPayload, UserRole } from "@/types/auth";

export type AuthState = {
    isLoading: boolean;
    isAuthenticated: boolean;
    username: string;
    role: UserRole | "";
    merchantId: string;
};

const loadingState: AuthState = {
    isLoading: true,
    isAuthenticated: false,
    username: "",
    role: "",
    merchantId: "",
};

const unauthenticatedState: AuthState = {
    isLoading: false,
    isAuthenticated: false,
    username: "",
    role: "",
    merchantId: "",
};

const getAuthSnapshot = () => {
    if (typeof window === "undefined") {
        return "loading";
    }

    return localStorage.getItem(STORAGE_KEYS.accessToken) ?? "";
};

const getServerSnapshot = () => {
    return "loading";
};

const subscribe = (callback: () => void) => {
    window.addEventListener("storage", callback);
    window.addEventListener("auth-change", callback);

    return () => {
        window.removeEventListener("storage", callback);
        window.removeEventListener("auth-change", callback);
    };
};

const getAuthStateFromSnapshot = (snapshot: string): AuthState => {
    if (snapshot === "loading") {
        return loadingState;
    }

    if (!snapshot) {
        return unauthenticatedState;
    }

    try {
        const decodedToken = jwtDecode<JwtPayload>(snapshot);

        if (decodedToken.exp && decodedToken.exp * 1000 < Date.now()) {
            return unauthenticatedState;
        }

        const storedRole = localStorage.getItem(STORAGE_KEYS.userRole) as UserRole | null;

        return {
            isLoading: false,
            isAuthenticated: true,
            username: decodedToken.sub ?? "",
            role: decodedToken.role ?? storedRole ?? "",
            merchantId: decodedToken.merchantId ?? "",
        };
    } catch {
        return unauthenticatedState;
    }
};

export const notifyAuthChange = () => {
    window.dispatchEvent(new Event("auth-change"));
};

export const useAuthState = () => {
    const snapshot = useSyncExternalStore(
        subscribe,
        getAuthSnapshot,
        getServerSnapshot
    );

    return useMemo(() => {
        return getAuthStateFromSnapshot(snapshot);
    }, [snapshot]);
};