"use client";

import {
    Box,
    Button,
    Card,
    CardContent,
    CardMedia,
    Chip,
    Typography
} from "@mui/material";
import { Vehicle } from "@/types/vehicle";
import { useRouter } from "next/navigation";

type VehicleCardProps = {
    vehicle: Vehicle;
};

const VEHICLE_CARD_LABELS = {
    pricePerDay: "Price per day",
    currency: "EUR",
    detailsButton: "View Details"
};

const IMAGE_BASE_URL = process.env.NEXT_PUBLIC_IMAGE_BASE_URL ?? "";

export default function VehicleCard({ vehicle }: VehicleCardProps) {
    const imageUrl = `${IMAGE_BASE_URL}${vehicle.imagePath}`;
    const router = useRouter();

    return (
        <Card
            sx={{
                height: "100%",
                minHeight: 470,
                display: "flex",
                flexDirection: "column",
                overflow: "hidden"
            }}>
            <Box
                sx={{
                    width: "100%",
                    height: 250,
                    overflow: "hidden",
                    bgcolor: "grey.100",
                    flexShrink: 0
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
                    p: 3,
                    display: "flex",
                    flexDirection: "column",
                    flexGrow: 1
                }}>
                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "flex-start",
                        gap: 2,
                        mb: 1
                    }}>
                    <Typography
                        variant="h6"
                        sx={{
                            fontWeight: 700,
                            display: "-webkit-box",
                            WebkitLineClamp: 1,
                            WebkitBoxOrient: "vertical",
                            overflow: "hidden",
                            textOverflow: "ellipsis"
                        }}>
                        {vehicle.name}
                    </Typography>

                    <Chip
                        label={vehicle.type}
                        size="small"
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
                        mb: 3,
                        display: "-webkit-box",
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: "vertical",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        minHeight: "60px"
                    }}>
                    {vehicle.description}
                </Typography>

                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        mt: "auto"
                    }}>
                    <Box>
                        <Typography
                            variant="caption"
                            color="text.secondary">
                            {VEHICLE_CARD_LABELS.pricePerDay}
                        </Typography>

                        <Typography
                            variant="h6"
                            sx={{
                                fontWeight: 700,
                                color: "primary.main"
                            }}>
                            {vehicle.pricePerDay} {VEHICLE_CARD_LABELS.currency}
                        </Typography>
                    </Box>

                    <Button
                        variant="contained"
                        onClick={() => router.push(`/vehicles/${vehicle.id}`)}>
                        {VEHICLE_CARD_LABELS.detailsButton}
                    </Button>
                </Box>
            </CardContent>
        </Card>
    );
}