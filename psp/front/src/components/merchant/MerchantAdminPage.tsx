"use client";

import { useEffect, useState } from "react";
import {
    Alert,
    Box,
    CircularProgress,
    Chip,
    Stack,
    Typography
} from "@mui/material";
import { getMerchantProfile, getMerchantSellers } from "@/api/merchantApi";
import { getPaymentMethods } from "@/api/paymentMethodApi";
import { useNotification } from "@/components/common/NotificationProvider";
import ApiKeyCard from "@/components/merchant/ApiKeyCard";
import EditMerchantProfileDialog from "@/components/merchant/EditMerchantProfileDialog";
import MerchantProfileCard from "@/components/merchant/MerchantProfileCard";
import PaymentMethodsDialog from "@/components/merchant/PaymentMethodsDialog";
import RegenerateApiKeyDialog from "@/components/merchant/RegenerateApiKeyDialog";
import SellerDialog from "@/components/merchant/SellerDialog";
import SellersTable from "@/components/merchant/SellersTable";
import { Severity } from "@/const/enums";
import { MERCHANT_LABELS } from "@/const/label";
import { MerchantProfile, MerchantSellerAccount } from "@/types/merchant";
import { PaymentMethod } from "@/types/paymentMethod";

export default function MerchantAdminPage() {
    const { showNotification } = useNotification();

    const [profile, setProfile] = useState<MerchantProfile | null>(null);
    const [sellers, setSellers] = useState<MerchantSellerAccount[]>([]);
    const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [profileDialogOpen, setProfileDialogOpen] = useState(false);
    const [sellerDialogOpen, setSellerDialogOpen] = useState(false);
    const [selectedSeller, setSelectedSeller] = useState<MerchantSellerAccount | null>(null);
    const [paymentMethodsDialogOpen, setPaymentMethodsDialogOpen] = useState(false);
    const [apiKeyDialogOpen, setApiKeyDialogOpen] = useState(false);

    const loadData = async () => {
        setError("");

        const [profileData, sellersData, paymentMethodsData] = await Promise.all([
            getMerchantProfile(),
            getMerchantSellers(),
            getPaymentMethods(),
        ]);

        setProfile(profileData);
        setSellers(sellersData);
        setPaymentMethods(paymentMethodsData);
    };

    useEffect(() => {
        let ignore = false;

        loadData()
            .catch(() => {
                if (!ignore) {
                    setError(MERCHANT_LABELS.loadingError);
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

    const refreshData = async () => {
        try {
            await loadData();
        } catch {
            showNotification(MERCHANT_LABELS.loadingError, Severity.Error);
        }
    };

    const handleProfileSaved = async () => {
        setProfileDialogOpen(false);
        await refreshData();
        showNotification(MERCHANT_LABELS.profileUpdated, Severity.Success);
    };

    const handleSellerSaved = async () => {
        setSellerDialogOpen(false);
        setSelectedSeller(null);
        await refreshData();
        showNotification(
            selectedSeller ? MERCHANT_LABELS.sellerUpdated : MERCHANT_LABELS.sellerCreated,
            Severity.Success
        );
    };

    const handlePaymentMethodsSaved = async () => {
        setPaymentMethodsDialogOpen(false);
        setSelectedSeller(null);
        await refreshData();
        showNotification(MERCHANT_LABELS.paymentMethodsUpdated, Severity.Success);
    };

    const openAddSellerDialog = () => {
        setSelectedSeller(null);
        setSellerDialogOpen(true);
    };

    const openEditSellerDialog = (seller: MerchantSellerAccount) => {
        setSelectedSeller(seller);
        setSellerDialogOpen(true);
    };

    const openPaymentMethodsDialog = (seller: MerchantSellerAccount) => {
        setSelectedSeller(seller);
        setPaymentMethodsDialogOpen(true);
    };

    if (loading) {
        return (
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    mt: 10
                }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error || !profile) {
        return (
            <Alert severity="error">
                {error || MERCHANT_LABELS.loadingError}
            </Alert>
        );
    }

    return (
        <Box>
            <Box
                sx={{
                    display: "grid",
                    gridTemplateColumns: { xs: "1fr", lg: "1.1fr 0.9fr" },
                    gap: 3,
                    mb: 3
                }}>
                <MerchantProfileCard
                    profile={profile}
                    onEditClick={() => setProfileDialogOpen(true)} />

                <ApiKeyCard
                    profile={profile}
                    onRegenerateClick={() => setApiKeyDialogOpen(true)} />
            </Box>

            <SellersTable
                sellers={sellers}
                onAddClick={openAddSellerDialog}
                onEditClick={openEditSellerDialog}
                onConfigureClick={openPaymentMethodsDialog} />

            <EditMerchantProfileDialog
                open={profileDialogOpen}
                profile={profile}
                onClose={() => setProfileDialogOpen(false)}
                onSaved={handleProfileSaved} />

            <SellerDialog
                open={sellerDialogOpen}
                seller={selectedSeller}
                onClose={() => {
                    setSellerDialogOpen(false);
                    setSelectedSeller(null);
                }}
                onSaved={handleSellerSaved} />

            <PaymentMethodsDialog
                open={paymentMethodsDialogOpen}
                seller={selectedSeller}
                paymentMethods={paymentMethods}
                onClose={() => {
                    setPaymentMethodsDialogOpen(false);
                    setSelectedSeller(null);
                }}
                onSaved={handlePaymentMethodsSaved} />

            <RegenerateApiKeyDialog
                open={apiKeyDialogOpen}
                onClose={() => setApiKeyDialogOpen(false)} />
        </Box>
    );
}
