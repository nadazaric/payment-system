import { Card, CardContent, Typography } from "@mui/material";

export default function HomePage() {
    return (
        <Card>
            <CardContent sx={{ p: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Home
                </Typography>

                <Typography variant="body2">
                    Welcome to Web Shop.
                </Typography>
            </CardContent>
        </Card>
    );
}