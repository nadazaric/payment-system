import axios from "axios";
import { ROUTES } from "@/const/routes";
import { STORAGE_KEYS } from "@/const/storageKeys";

const axiosInstance = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL,
});

axiosInstance.interceptors.request.use(
    (config) => {
        if (typeof window !== "undefined") {
            const accessToken = localStorage.getItem(STORAGE_KEYS.accessToken);

            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (typeof window !== "undefined" && error.response?.status === 401) {
            localStorage.removeItem(STORAGE_KEYS.accessToken);
            window.dispatchEvent(new Event("auth-change"));
            window.location.href = ROUTES.auth;
        }

        return Promise.reject(error);
    }
);

export default axiosInstance;
