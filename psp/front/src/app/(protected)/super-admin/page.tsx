import {
    Box,
    Card,
    CardContent,
    Typography
} from "@mui/material";

export default function SuperAdminPage() {
    return (
        <Box>
            <Card>
                <CardContent sx={{ p: 3 }}>
                    <Typography
                        variant="h5"
                        sx={{ fontWeight: 800 }}>
                        Superadmin plugin management
                    </Typography>
                </CardContent>
            </Card>
        </Box>
    );
}