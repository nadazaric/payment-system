"use client";

import { useMemo, useSyncExternalStore } from "react";
import { jwtDecode } from "jwt-decode";
import { JwtPayload } from "@/types/auth";

export type AuthState = {
    isLoading: boolean;
    isAuthenticated: boolean;
    username: string;
    role: string;
};

const loadingState: AuthState = {
    isLoading: true,
    isAuthenticated: false,
    username: "",
    role: "",
};

const unauthenticatedState: AuthState = {
    isLoading: false,
    isAuthenticated: false,
    username: "",
    role: "",
};

const getAuthSnapshot = () => {
    if (typeof window === "undefined") {
        return "loading";
    }

    const accessToken = localStorage.getItem("accessToken") ?? "";
    const user = localStorage.getItem("user") ?? "";

    return JSON.stringify({
        accessToken,
        user,
    });
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

    try {
        const parsedSnapshot = JSON.parse(snapshot);
        const accessToken = parsedSnapshot.accessToken;
        const user = parsedSnapshot.user;

        if (!accessToken) {
            return unauthenticatedState;
        }

        const decodedToken = jwtDecode<JwtPayload>(accessToken);

        if (decodedToken.exp && decodedToken.exp * 1000 < Date.now()) {
            return unauthenticatedState;
        }

        let username = decodedToken.sub ?? "";

        if (user) {
            const parsedUser = JSON.parse(user);
            username = parsedUser.username ?? username;
        }

        return {
            isLoading: false,
            isAuthenticated: true,
            username,
            role: decodedToken.role ?? "Unknown role",
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