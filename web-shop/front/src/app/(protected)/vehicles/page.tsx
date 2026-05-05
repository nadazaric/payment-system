"use client";

import { useEffect, useState } from "react";

import {
    Alert,
    Box,
    Button,
    CircularProgress,
    Typography
} from "@mui/material";

import Grid from "@mui/material/Grid";

import { getVehicles } from "@/api/vehicleApi";
import AddVehicleDialog from "@/components/vehicles/AddVehicleDialog";
import VehicleCard from "@/components/vehicles/VehicleCard";
import { useAuthState } from "@/hooks/useAuthState";
import { Vehicle } from "@/types/vehicle";

const VEHICLES_PAGE_LABELS = {
    title: "Available vehicles",
    description: "Choose a vehicle for your next rental.",
    addVehicleButton: "Add vehicle",
    loadingError: "Failed to load vehicles.",
    noVehicles: "No vehicles are currently available."
};

export default function VehiclesPage() {
    const authState = useAuthState();

    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [addVehicleDialogOpen, setAddVehicleDialogOpen] = useState(false);

    const isAdmin = authState.role === "ADMIN";

    useEffect(() => {
        let ignore = false;

        getVehicles()
            .then((data) => {
                if (!ignore) {
                    setVehicles(data);
                }
            })
            .catch(() => {
                if (!ignore) {
                    setError(VEHICLES_PAGE_LABELS.loadingError);
                }
            })
            .finally(() => {
                if (!ignore) {
                    setLoading(false);
                }
            });

        return () => {
            ignore = true;
        };
    }, []);

    const refreshVehicles = async () => {
        try {
            setError("");

            const data = await getVehicles();

            setVehicles(data);
        } catch {
            setError(VEHICLES_PAGE_LABELS.loadingError);
        }
    };

    if (loading || authState.isLoading) {
        return (
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    mt: 8
                }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: {
                        xs: "flex-start",
                        sm: "center"
                    },
                    flexDirection: {
                        xs: "column",
                        sm: "row"
                    },
                    gap: 2,
                    mb: 3
                }}>
                <Box>
                    <Typography
                        variant="h4"
                        component="h1"
                        gutterBottom
                        sx={{
                            fontWeight: 700
                        }}>
                        {VEHICLES_PAGE_LABELS.title}
                    </Typography>

                    <Typography
                        variant="body2"
                        color="text.secondary">
                        {VEHICLES_PAGE_LABELS.description}
                    </Typography>
                </Box>

                {isAdmin && (
                    <Button
                        type="button"
                        variant="contained"
                        onClick={() => setAddVehicleDialogOpen(true)}>
                        {VEHICLES_PAGE_LABELS.addVehicleButton}
                    </Button>
                )}
            </Box>

            {error && (
                <Alert
                    severity="error"
                    sx={{
                        mb: 3
                    }}>
                    {error}
                </Alert>
            )}

            {!error && vehicles.length === 0 && (
                <Alert
                    severity="info">
                    {VEHICLES_PAGE_LABELS.noVehicles}
                </Alert>
            )}

            <Grid
                container
                spacing={3}>
                {vehicles.map((vehicle) => (
                    <Grid
                        size={{
                            xs: 12,
                            sm: 6,
                            md: 4,
                            lg: 3
                        }}
                        key={vehicle.id}>
                        <VehicleCard
                            vehicle={vehicle} />
                    </Grid>
                ))}
            </Grid>

            <AddVehicleDialog
                open={addVehicleDialogOpen}
                onClose={() => setAddVehicleDialogOpen(false)}
                onVehicleCreated={refreshVehicles} />
        </Box>
    );
}