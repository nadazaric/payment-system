"use client";

import {
    Box,
    Button,
    Card,
    CardContent,
    Divider,
    Stack,
    Typography
} from "@mui/material";
import { MERCHANT_LABELS } from "@/const/label";
import { MerchantProfile } from "@/types/merchant";

type ApiKeyCardProps = {
    profile: MerchantProfile;
    onRegenerateClick: () => void;
};

export default function ApiKeyCard({ profile, onRegenerateClick }: ApiKeyCardProps) {
    return (
        <Card>
            <CardContent sx={{ p: 3 }}>
                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "flex-start",
                        gap: 2,
                        mb: 2
                    }}>
                    <Box>
                        <Typography variant="h6">
                            {MERCHANT_LABELS.apiKeyTitle}
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary">
                            {MERCHANT_LABELS.apiKeyDescription}
                        </Typography>
                    </Box>

                    <Button
                        type="button"
                        color="secondary"
                        variant="contained"
                        onClick={onRegenerateClick}>
                        {MERCHANT_LABELS.regenerateApiKey}
                    </Button>
                </Box>

                <Divider sx={{ mb: 2 }} />

                <Stack spacing={2}>
                    <InfoBlock
                        label={MERCHANT_LABELS.merchantId}
                        value={profile.merchantId} />

                    <InfoBlock
                        label={MERCHANT_LABELS.adminUsername}
                        value={profile.adminUsername} />

                    <InfoBlock
                        label={MERCHANT_LABELS.adminName}
                        value={profile.adminName} />
                </Stack>
            </CardContent>
        </Card>
    );
}

type InfoBlockProps = {
    label: string;
    value: string;
};

function InfoBlock({ label, value }: InfoBlockProps) {
    return (
        <Box>
            <Typography
                variant="caption"
                color="text.secondary">
                {label}
            </Typography>

            <Typography
                variant="body2"
                sx={{ fontWeight: 700, wordBreak: "break-word" }}>
                {value}
            </Typography>
        </Box>
    );
}
