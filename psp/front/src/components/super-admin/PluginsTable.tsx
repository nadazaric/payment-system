"use client";

import {
    Box,
    Button,
    Card,
    CardContent,
    Chip,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from "@mui/material";
import { SUPER_ADMIN_LABELS } from "@/const/label";
import { PaymentPlugin } from "@/types/plugin";

type PluginsTableProps = {
    plugins: PaymentPlugin[];
    actionLoadingCode: string;
    onCreateClick: () => void;
    onToggleStatus: (plugin: PaymentPlugin) => void;
};

export default function PluginsTable({
    plugins,
    actionLoadingCode,
    onCreateClick,
    onToggleStatus
}: PluginsTableProps) {
    return (
        <Card>
            <CardContent sx={{ p: 3 }}>
                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: { xs: "flex-start", sm: "center" },
                        flexDirection: { xs: "column", sm: "row" },
                        gap: 2,
                        mb: 3
                    }}>
                    <Box>
                        <Typography variant="h6">
                            {SUPER_ADMIN_LABELS.pluginsTitle}
                        </Typography>
                    </Box>

                    <Button
                        type="button"
                        variant="contained"
                        onClick={onCreateClick}>
                        {SUPER_ADMIN_LABELS.createPlugin}
                    </Button>
                </Box>

                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>{SUPER_ADMIN_LABELS.pluginCode}</TableCell>
                                <TableCell>{SUPER_ADMIN_LABELS.displayName}</TableCell>
                                <TableCell>{SUPER_ADMIN_LABELS.baseUrl}</TableCell>
                                <TableCell>{SUPER_ADMIN_LABELS.adminStatus}</TableCell>
                                <TableCell>{SUPER_ADMIN_LABELS.operationalStatus}</TableCell>
                                <TableCell align="right">{SUPER_ADMIN_LABELS.actions}</TableCell>
                            </TableRow>
                        </TableHead>

                        <TableBody>
                            {plugins.map((plugin) => (
                                <TableRow
                                    key={plugin.pluginCode}
                                    hover>
                                    <TableCell sx={{ fontWeight: 700 }}>
                                        {plugin.pluginCode}
                                    </TableCell>

                                    <TableCell>
                                        {plugin.displayName}
                                    </TableCell>

                                    <TableCell>
                                        {plugin.baseUrl || SUPER_ADMIN_LABELS.noBaseUrl}
                                    </TableCell>

                                    <TableCell>
                                        <Chip
                                            label={
                                                plugin.activeByAdmin
                                                    ? SUPER_ADMIN_LABELS.enabledByAdmin
                                                    : SUPER_ADMIN_LABELS.disabledByAdmin
                                            }
                                            color={plugin.activeByAdmin ? "success" : "warning"}
                                            variant="outlined" />
                                    </TableCell>

                                    <TableCell>
                                        <Chip
                                            label={plugin.active ? SUPER_ADMIN_LABELS.active : SUPER_ADMIN_LABELS.inactive}
                                            color={plugin.active ? "success" : "warning"}
                                            variant="outlined" />
                                    </TableCell>

                                    <TableCell align="right">
                                        <Stack
                                            direction="row"
                                            spacing={1}
                                            sx={{ justifyContent: "flex-end" }}>
                                            <Button
                                                type="button"
                                                variant={plugin.activeByAdmin ? "outlined" : "contained"}
                                                color={plugin.activeByAdmin ? "error" : "primary"}
                                                size="small"
                                                disabled={actionLoadingCode === plugin.pluginCode}
                                                onClick={() => onToggleStatus(plugin)}>
                                                {plugin.activeByAdmin
                                                    ? SUPER_ADMIN_LABELS.disable
                                                    : SUPER_ADMIN_LABELS.enable}
                                            </Button>
                                        </Stack>
                                    </TableCell>
                                </TableRow>
                            ))}

                            {plugins.length === 0 && (
                                <TableRow>
                                    <TableCell colSpan={6}>
                                        <Typography
                                            variant="body2"
                                            color="text.secondary"
                                            sx={{ py: 3, textAlign: "center" }}>
                                            {SUPER_ADMIN_LABELS.noPlugins}
                                        </Typography>
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
        </Card>
    );

}