"use client";

import { useState } from "react";
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import { useNotification } from "@/components/common/NotificationProvider";
import { Severity } from "@/const/enums";
import { SUPER_ADMIN_LABELS } from "@/const/label";
import { CreateExpectedPluginResponse } from "@/types/plugin";

type PluginSecretDialogProps = {
    plugin: CreateExpectedPluginResponse | null;
    onClose: () => void;
};

export default function PluginSecretDialog({
    plugin,
    onClose
}: PluginSecretDialogProps) {
    const { showNotification } = useNotification();
    const [copied, setCopied] = useState(false);

    if (!plugin) {
        return null;
    }

    const handleCopy = async () => {
        await navigator.clipboard.writeText(plugin.pluginSecret);
        setCopied(true);
        showNotification(SUPER_ADMIN_LABELS.copied, Severity.Success);
    };

    const handleClose = () => {
        setCopied(false);
        onClose();
    };

    return (
        <Dialog
            open
            onClose={handleClose}
            maxWidth="sm"
            fullWidth>
            <DialogTitle>
                {SUPER_ADMIN_LABELS.pluginSecretTitle}
            </DialogTitle>

            <DialogContent>
                <Stack
                    spacing={2}
                    sx={{ pt: 1 }}>
                    <Typography
                        variant="body2"
                        color="text.secondary">
                        {SUPER_ADMIN_LABELS.pluginSecretDescription}
                    </Typography>

                    <TextField
                        label={SUPER_ADMIN_LABELS.pluginCode}
                        value={plugin.pluginCode}
                        fullWidth
                        slotProps={{
                            input: {
                                readOnly: true
                            }
                        }} />

                    <TextField
                        label={SUPER_ADMIN_LABELS.pluginSecret}
                        value={plugin.pluginSecret}
                        fullWidth
                        multiline
                        minRows={2}
                        slotProps={{
                            input: {
                                readOnly: true
                            }
                        }} />
                </Stack>
            </DialogContent>

            <DialogActions>
                <Button
                    type="button"
                    variant="outlined"
                    onClick={handleCopy}>
                    {copied ? SUPER_ADMIN_LABELS.copied : SUPER_ADMIN_LABELS.copy}
                </Button>

                <Button
                    type="button"
                    variant="contained"
                    onClick={handleClose}>
                    {SUPER_ADMIN_LABELS.close}
                </Button>
            </DialogActions>
        </Dialog>
    );
    
}