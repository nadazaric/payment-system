"use client";

import {
    Box,
    Card,
    CardContent,
    CardMedia,
    Chip,
    Divider,
    Typography
} from "@mui/material";

import { Vehicle } from "@/types/vehicle";
import { VEHICLE_DETAILS_PAGE_LABELS } from "@/const/label";

type VehicleInfoCardProps = {
    vehicle: Vehicle;
};

const IMAGE_BASE_URL = process.env.NEXT_PUBLIC_IMAGE_BASE_URL ?? "";

export default function VehicleInfoCard({ vehicle }: VehicleInfoCardProps) {
    const imageUrl = `${IMAGE_BASE_URL}${vehicle.imagePath}`;

    return (
        <Card
            sx={{
                width: "100%",
                maxWidth: "none",
                minWidth: 0,
                justifySelf: "stretch",
                overflow: "hidden",
                height: "fit-content"
            }}>
            <Box
                sx={{
                    width: "100%",
                    height: 300,
                    overflow: "hidden",
                    bgcolor: "grey.100"
                }}>
                <CardMedia
                    component="img"
                    image={imageUrl}
                    alt={vehicle.name}
                    sx={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                        objectPosition: "center"
                    }} />
            </Box>

            <CardContent
                sx={{
                    p: 3
                }}>

                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "flex-start",
                        gap: 2,
                        mb: 2
                    }}>
                    <Typography
                        variant="h5"
                        sx={{
                            fontWeight: 700
                        }}>
                        {vehicle.name}
                    </Typography>

                    <Chip
                        label={vehicle.type}
                        color="primary"
                        variant="outlined"
                        sx={{
                            flexShrink: 0
                        }} />
                </Box>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                        mb: 3
                    }}>
                    {vehicle.description}
                </Typography>

                <Divider
                    sx={{
                        mb: 3
                    }} />

                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center"
                    }}>
                    <Typography
                        color="text.secondary">
                        {VEHICLE_DETAILS_PAGE_LABELS.pricePerDay}
                    </Typography>

                    <Typography
                        variant="h5"
                        sx={{
                            fontWeight: 700,
                            color: "primary.main"
                        }}>
                        {vehicle.pricePerDay} {VEHICLE_DETAILS_PAGE_LABELS.currency}
                    </Typography>
                </Box>
            </CardContent>
        </Card>
    );
}