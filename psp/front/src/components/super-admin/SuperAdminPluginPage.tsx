"use client";

import { useEffect, useState } from "react";
import {
    Alert,
    Box,
    CircularProgress,
    Typography
} from "@mui/material";
import {
    getPlugins,
    updatePluginStatus
} from "@/api/pluginApi";
import { useNotification } from "@/components/common/NotificationProvider";
import CreatePluginDialog from "@/components/super-admin/CreatePluginDialog";
import PluginSecretDialog from "@/components/super-admin/PluginSecretDialog";
import PluginsTable from "@/components/super-admin/PluginsTable";
import { Severity } from "@/const/enums";
import { SUPER_ADMIN_LABELS } from "@/const/label";
import {
    CreateExpectedPluginResponse,
    PaymentPlugin
} from "@/types/plugin";

export default function SuperAdminPluginPage() {
    const { showNotification } = useNotification();

    const [plugins, setPlugins] = useState<PaymentPlugin[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [createDialogOpen, setCreateDialogOpen] = useState(false);
    const [createdPlugin, setCreatedPlugin] = useState<CreateExpectedPluginResponse | null>(null);
    const [actionLoadingCode, setActionLoadingCode] = useState("");

    useEffect(() => {
        let ignore = false;

        const fetchPlugins = async () => {
            try {
                const data = await getPlugins();

                if (!ignore) {
                    setPlugins(data);
                }
            } catch {
                if (!ignore) {
                    setError(SUPER_ADMIN_LABELS.loadingError);
                }
            } finally {
                if (!ignore) {
                    setLoading(false);
                }
            }
        };

        void fetchPlugins();

        return () => {
            ignore = true;
        };
    }, []);

    const refreshPlugins = async () => {
        try {
            const data = await getPlugins();

            setPlugins(data);
            setError("");
        } catch {
            showNotification(SUPER_ADMIN_LABELS.loadingError, Severity.Error);
        }
    };

    const handleCreated = async (response: CreateExpectedPluginResponse) => {
        setCreateDialogOpen(false);
        setCreatedPlugin(response);
        await refreshPlugins();
        showNotification(SUPER_ADMIN_LABELS.pluginCreated, Severity.Success);
    };

    const handleToggleStatus = async (plugin: PaymentPlugin) => {
        setActionLoadingCode(plugin.pluginCode);

        try {
            await updatePluginStatus({
                pluginCode: plugin.pluginCode,
                activeByAdmin: !plugin.activeByAdmin
            });

            await refreshPlugins();

            showNotification(SUPER_ADMIN_LABELS.pluginStatusUpdated, Severity.Success);
        } catch {
            showNotification(SUPER_ADMIN_LABELS.saveFailed, Severity.Error);
        } finally {
            setActionLoadingCode("");
        }
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

    if (error) {
        return (
            <Alert severity="error">
                {error}
            </Alert>
        );
    }

    return (
        <Box>
            <PluginsTable
                plugins={plugins}
                actionLoadingCode={actionLoadingCode}
                onCreateClick={() => setCreateDialogOpen(true)}
                onToggleStatus={handleToggleStatus} />

            <CreatePluginDialog
                open={createDialogOpen}
                onClose={() => setCreateDialogOpen(false)}
                onCreated={handleCreated} />

            <PluginSecretDialog
                plugin={createdPlugin}
                onClose={() => setCreatedPlugin(null)} />
        </Box>
    );

}