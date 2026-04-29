"use client";

import { useEffect, useState } from "react";
import { Alert, Box, CircularProgress, Typography } from "@mui/material";
import Grid from "@mui/material/Grid";
import { getVehicles } from "@/api/vehicleApi";
import VehicleCard from "@/components/vehicles/VehicleCard";
import { Vehicle } from "@/types/vehicle";

export default function VehiclesPage() {
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchVehicles = async () => {
            try {
                const data = await getVehicles();
                setVehicles(data);
            } catch {
                setError("Failed to load vehicles.");
            } finally {
                setLoading(false);
            }
        };

        fetchVehicles();
    }, []);

    if (loading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 8 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Box sx={{ mb: 3 }}>
                <Typography variant="h4" component="h1" sx={{ fontWeight: 700 }} gutterBottom>
                    Available vehicles
                </Typography>

                <Typography variant="body2" color="text.secondary">
                    Choose a vehicle for your next rental.
                </Typography>
            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            {!error && vehicles.length === 0 && (
                <Alert severity="info">
                    No vehicles are currently available.
                </Alert>
            )}

            <Grid container spacing={3}>
                {vehicles.map((vehicle) => (
                    <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }} key={vehicle.id}>
                        <VehicleCard vehicle={vehicle} />
                    </Grid>
                ))}
            </Grid>
        </Box>
    );
}