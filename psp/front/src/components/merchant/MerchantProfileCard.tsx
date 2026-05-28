"use client";

import {
    Box,
    Button,
    Card,
    CardContent,
    Chip,
    Divider,
    Stack,
    Typography
} from "@mui/material";
import { MERCHANT_LABELS } from "@/const/label";
import { MerchantProfile } from "@/types/merchant";

type MerchantProfileCardProps = {
    profile: MerchantProfile;
    onEditClick: () => void;
};

export default function MerchantProfileCard({ profile, onEditClick }: MerchantProfileCardProps) {
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
                            {MERCHANT_LABELS.profileTitle}
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary">
                            {MERCHANT_LABELS.profileDescription}
                        </Typography>
                    </Box>

                    <Button
                        type="button"
                        variant="outlined"
                        onClick={onEditClick}>
                        {MERCHANT_LABELS.edit}
                    </Button>
                </Box>

                <Divider sx={{ mb: 2 }} />

                <Stack spacing={1.5}>
                    <InfoRow
                        label={MERCHANT_LABELS.merchantName}
                        value={profile.merchantName} />

                    <InfoRow
                        label={MERCHANT_LABELS.currency}
                        value={profile.currency} />

                    <InfoRow
                        label={MERCHANT_LABELS.successUrl}
                        value={profile.successUrl} />

                    <InfoRow
                        label={MERCHANT_LABELS.failUrl}
                        value={profile.failUrl} />

                    <InfoRow
                        label={MERCHANT_LABELS.errorUrl}
                        value={profile.errorUrl} />

                    <Box
                        sx={{
                            display: "grid",
                            gridTemplateColumns: { xs: "1fr", sm: "170px 1fr" },
                            gap: 1
                        }}>
                        <Typography
                            variant="body2"
                            color="text.secondary" >
                            {MERCHANT_LABELS.status}
                        </Typography>

                        <Chip
                            label={profile.merchantActive ? MERCHANT_LABELS.active : MERCHANT_LABELS.setupRequired}
                            color={profile.merchantActive ? "success" : "warning"}
                            variant="outlined"
                            sx={{ justifySelf: "start" }} />
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    );
}

type InfoRowProps = {
    label: string;
    value: string;
};

function InfoRow({ label, value }: InfoRowProps) {
    return (
        <Box
            sx={{
                display: "grid",
                gridTemplateColumns: { xs: "1fr", sm: "170px 1fr" },
                gap: 1
            }}>
            <Typography
                variant="body2"
                color="text.secondary">
                {label}
            </Typography>

            <Typography
                variant="body2"
                sx={{ wordBreak: "break-word" }}>
                {value}
            </Typography>
        </Box>
    );
}
