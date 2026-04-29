"use client";

import { Box, Button, Card, CardContent, CardMedia, Chip, Typography } from "@mui/material";
import { Vehicle } from "@/types/vehicle";

type VehicleCardProps = {
    vehicle: Vehicle;
};

const IMAGE_BASE_URL = process.env.NEXT_PUBLIC_IMAGE_BASE_URL ?? "";

export default function VehicleCard({ vehicle }: VehicleCardProps) {
    const imageUrl = `${IMAGE_BASE_URL}${vehicle.imagePath}`;

    return (
        <Card sx={{ height: "100%", display: "flex", flexDirection: "column", overflow: "hidden" }}>
            <CardMedia component="img" height="220" image={imageUrl} alt={vehicle.name} sx={{ objectFit: "cover" }} />

            <CardContent sx={{ p: 3, display: "flex", flexDirection: "column", flexGrow: 1 }}>
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: 2, mb: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 700 }}>
                        {vehicle.name}
                    </Typography>

                    <Chip label={vehicle.type} size="small" color="primary" variant="outlined" />
                </Box>

                <Typography variant="body2" color="text.secondary" sx={{ mb: 3, flexGrow: 1 }}>
                    {vehicle.description}
                </Typography>

                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mt: "auto" }}>
                    <Box>
                        <Typography variant="caption" color="text.secondary">
                            Price per day
                        </Typography>

                        <Typography variant="h6" sx={{ fontWeight: 700, color: "primary.main" }}>
                            {vehicle.pricePerDay} EUR
                        </Typography>
                    </Box>

                    <Button variant="contained">
                        View details
                    </Button>
                </Box>
            </CardContent>
        </Card>
    );
}