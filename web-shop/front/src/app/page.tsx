"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { jwtDecode } from "jwt-decode";
import { Box, Button, Typography } from "@mui/material";
import { JwtPayload } from "@/types/auth";

type AuthState = {
  isAuthenticated: boolean;
  username: string;
  role: string;
};

const getAuthState = (): AuthState => {
  if (typeof window === "undefined") {
    return {
      isAuthenticated: false,
      username: "",
      role: "",
    };
  }

  const accessToken = localStorage.getItem("accessToken");
  const user = localStorage.getItem("user");

  if (!accessToken) {
    return {
      isAuthenticated: false,
      username: "",
      role: "",
    };
  }

  try {
    const decodedToken = jwtDecode<JwtPayload>(accessToken);

    if (decodedToken.exp && decodedToken.exp * 1000 < Date.now()) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("user");

      return {
        isAuthenticated: false,
        username: "",
        role: "",
      };
    }

    let username = decodedToken.sub ?? "";

    if (user) {
      const parsedUser = JSON.parse(user);
      username = parsedUser.username ?? username;
    }

    return {
      isAuthenticated: true,
      username,
      role: decodedToken.role ?? "Unknown role",
    };
  } catch {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("user");

    return {
      isAuthenticated: false,
      username: "",
      role: "",
    };
  }
};

export default function HomePage() {
  const router = useRouter();
  const [authState] = useState<AuthState>(() => getAuthState());

  useEffect(() => {
    if (!authState.isAuthenticated) {
      router.push("/auth");
    }
  }, [authState.isAuthenticated, router]);

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("user");
    router.push("/auth");
  };

  if (!authState.isAuthenticated) {
    return null;
  }

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "background.default", px: 2 }}>
      <Typography variant="body2" >
        Username: {authState.username}
      </Typography>

      <Typography variant="body2" >
        Role: {authState.role}
      </Typography>

      <Button variant="contained" onClick={handleLogout}>
        Logout
      </Button>
    </Box>
  );
}