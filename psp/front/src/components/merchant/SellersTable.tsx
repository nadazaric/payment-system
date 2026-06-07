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
import { MERCHANT_LABELS } from "@/const/label";
import { MerchantSellerAccount } from "@/types/merchant";

type SellersTableProps = {
    sellers: MerchantSellerAccount[];
    onAddClick: () => void;
    onEditClick: (seller: MerchantSellerAccount) => void;
    onConfigureClick: (seller: MerchantSellerAccount) => void;
};

const getSellerPaymentMethods = (seller: MerchantSellerAccount) => {
    return seller.paymentMethods ?? [];
};

export default function SellersTable({
    sellers,
    onAddClick,
    onEditClick,
    onConfigureClick
}: SellersTableProps) {
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
                        <Typography
                            variant="h5"
                            sx={{ fontWeight: 800 }}>
                            {MERCHANT_LABELS.sellersTitle}
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary">
                            {MERCHANT_LABELS.sellersDescription}
                        </Typography>
                    </Box>

                    <Button
                        type="button"
                        variant="contained"
                        onClick={onAddClick}>
                        {MERCHANT_LABELS.addSeller}
                    </Button>
                </Box>

                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>
                                    {MERCHANT_LABELS.sellerReference}
                                </TableCell>

                                <TableCell>
                                    {MERCHANT_LABELS.displayName}
                                </TableCell>

                                <TableCell>
                                    {MERCHANT_LABELS.status}
                                </TableCell>

                                <TableCell>
                                    {MERCHANT_LABELS.paymentMethods}
                                </TableCell>

                                <TableCell align="right">
                                    {MERCHANT_LABELS.actions}
                                </TableCell>
                            </TableRow>
                        </TableHead>

                        <TableBody>
                            {sellers.map((seller) => {
                                const sellerPaymentMethods = getSellerPaymentMethods(seller);

                                return (
                                    <TableRow
                                        key={seller.id}
                                        hover>
                                        <TableCell sx={{ fontWeight: 700 }}>
                                            {seller.sellerReference}
                                        </TableCell>

                                        <TableCell>
                                            {seller.displayName}
                                        </TableCell>

                                        <TableCell>
                                            <Chip
                                                label={
                                                    seller.active
                                                        ? MERCHANT_LABELS.active
                                                        : MERCHANT_LABELS.inactive
                                                }
                                                color={seller.active ? "success" : "warning"}
                                                variant="outlined" />
                                        </TableCell>

                                        <TableCell>
                                            {sellerPaymentMethods.length === 0 ? (
                                                <Typography
                                                    variant="body2"
                                                    color="text.secondary">
                                                    {MERCHANT_LABELS.noPaymentMethodsConfigured}
                                                </Typography>
                                            ) : (
                                                <Stack
                                                    direction="row"
                                                    spacing={1}
                                                    useFlexGap
                                                    sx={{ flexWrap: "wrap" }}>
                                                    {sellerPaymentMethods.map((paymentMethod) => (
                                                        <Chip
                                                            key={paymentMethod.code}
                                                            label={paymentMethod.displayName}
                                                            color={paymentMethod.configurationRequired ? "warning" : "success"}
                                                            variant="outlined" />
                                                    ))}
                                                </Stack>
                                            )}
                                        </TableCell>

                                        <TableCell align="right">
                                            <Stack
                                                direction="row"
                                                spacing={1}
                                                sx={{ justifyContent: "flex-end" }}>
                                                <Button
                                                    type="button"
                                                    variant="outlined"
                                                    size="small"
                                                    onClick={() => onEditClick(seller)}>
                                                    {MERCHANT_LABELS.edit}
                                                </Button>

                                                <Button
                                                    type="button"
                                                    variant="contained"
                                                    size="small"
                                                    onClick={() => onConfigureClick(seller)}>
                                                    {MERCHANT_LABELS.configure}
                                                </Button>
                                            </Stack>
                                        </TableCell>
                                    </TableRow>
                                );
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
        </Card>
    );

}