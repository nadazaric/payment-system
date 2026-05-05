"use client";

import {
    useRef,
    useState
} from "react";

import {
    Box,
    Button,
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField,
    Typography
} from "@mui/material";

import { createVehicle } from "@/api/vehicleApi";
import { VehicleType } from "@/types/vehicle";
import { ADD_VEHICLE_DIALOG_LABELS } from "@/const/label";

type AddVehicleDialogProps = {
    open: boolean;
    onClose: () => void;
    onVehicleCreated: () => void;
};

const VEHICLE_TYPES: VehicleType[] = [
    "ECONOMY",
    "COMPACT",
    "SUV",
    "LUXURY",
    "VAN"
];

export default function AddVehicleDialog({
    open,
    onClose,
    onVehicleCreated
}: AddVehicleDialogProps) {
    const imageInputRef = useRef<HTMLInputElement | null>(null);

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [type, setType] = useState<VehicleType>("ECONOMY");
    const [pricePerDay, setPricePerDay] = useState("");
    const [image, setImage] = useState<File | null>(null);
    const [imagePreviewUrl, setImagePreviewUrl] = useState("");
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    const clearImagePreview = () => {
        if (imagePreviewUrl) {
            URL.revokeObjectURL(imagePreviewUrl);
        }
    };

    const resetForm = () => {
        clearImagePreview();

        setName("");
        setDescription("");
        setType("ECONOMY");
        setPricePerDay("");
        setImage(null);
        setImagePreviewUrl("");
        setError("");

        if (imageInputRef.current) {
            imageInputRef.current.value = "";
        }
    };

    const handleClose = () => {
        resetForm();
        onClose();
    };

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const selectedFile = event.target.files?.[0] ?? null;

        clearImagePreview();

        setImage(selectedFile);
        setImagePreviewUrl(selectedFile ? URL.createObjectURL(selectedFile) : "");
    };

    const canSave = Boolean(
        name.trim()
        && description.trim()
        && type
        && Number(pricePerDay) > 0
        && image
    );

    const handleSave = async () => {
        if (!image) {
            return;
        }

        try {
            setSaving(true);
            setError("");

            await createVehicle({
                name,
                description,
                type,
                pricePerDay: Number(pricePerDay),
                image
            });

            resetForm();
            onVehicleCreated();
            onClose();
        } catch {
            setError(ADD_VEHICLE_DIALOG_LABELS.error);
        } finally {
            setSaving(false);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            fullWidth
            maxWidth="sm">
            <DialogTitle>
                {ADD_VEHICLE_DIALOG_LABELS.title}
            </DialogTitle>

            <DialogContent>
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 2,
                        pt: 1
                    }}>
                    <Box>
                        <Box
                            onClick={() => imageInputRef.current?.click()}
                            sx={{
                                height: 230,
                                border: "1px dashed",
                                borderColor: imagePreviewUrl ? "divider" : "primary.main",
                                borderRadius: "12px",
                                overflow: "hidden",
                                cursor: "pointer",
                                bgcolor: imagePreviewUrl ? "grey.100" : "rgba(15, 118, 110, 0.04)",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                transition: "0.15s ease",
                                "&:hover": {
                                    borderColor: "primary.main",
                                    bgcolor: imagePreviewUrl ? "grey.100" : "rgba(15, 118, 110, 0.08)"
                                }
                            }}>
                            {imagePreviewUrl ? (
                                <Box
                                    component="img"
                                    src={imagePreviewUrl}
                                    alt={image?.name ?? ADD_VEHICLE_DIALOG_LABELS.image}
                                    sx={{
                                        width: "100%",
                                        height: "100%",
                                        objectFit: "cover",
                                        objectPosition: "center"
                                    }} />
                            ) : (
                                <Box
                                    sx={{
                                        textAlign: "center",
                                        px: 3
                                    }}>
                                    <Typography
                                        sx={{
                                            fontWeight: 700,
                                            color: "primary.main",
                                            mb: 0.5
                                        }}>
                                        {ADD_VEHICLE_DIALOG_LABELS.noImage}
                                    </Typography>

                                    <Typography
                                        variant="body2"
                                        color="text.secondary">
                                        PNG or JPG
                                    </Typography>
                                </Box>
                            )}
                        </Box>

                        <input
                            ref={imageInputRef}
                            hidden
                            type="file"
                            accept="image/png,image/jpeg,image/webp"
                            onChange={handleImageChange} />
                    </Box>

                    <Box
                        sx={{
                            display: "flex",
                            flexWrap: "wrap",
                            gap: 1
                        }}>
                        {VEHICLE_TYPES.map((vehicleType) => {
                            const isSelected = type === vehicleType;

                            return (
                                <Chip
                                    key={vehicleType}
                                    label={vehicleType}
                                    clickable
                                    color={isSelected ? "primary" : "default"}
                                    variant={isSelected ? "filled" : "outlined"}
                                    onClick={() => setType(vehicleType)}
                                    sx={{
                                        fontWeight: 600
                                    }} />
                            );
                        })}
                    </Box>

                    <Box
                        sx={{
                            display: "grid",
                            gridTemplateColumns: {
                                xs: "1fr",
                                sm: "1fr 180px"
                            },
                            gap: 2,
                        }}>
                        <TextField
                            label={ADD_VEHICLE_DIALOG_LABELS.name}
                            value={name}
                            onChange={(event) => setName(event.target.value)}
                            sx={{ mb: 0 }} />

                        <TextField
                            label={ADD_VEHICLE_DIALOG_LABELS.pricePerDay}
                            type="number"
                            value={pricePerDay}
                            onChange={(event) => setPricePerDay(event.target.value)}
                            sx={{ mb: 0 }} />
                    </Box>

                    <TextField
                        label={ADD_VEHICLE_DIALOG_LABELS.description}
                        value={description}
                        multiline
                        minRows={4}
                        onChange={(event) => setDescription(event.target.value)}
                        sx={{ mb: 0 }} />

                    {error && (
                        <Typography
                            variant="body2"
                            sx={{
                                color: "error.main",
                                fontWeight: 600
                            }}>
                            {error}
                        </Typography>
                    )}
                </Box>
            </DialogContent>

            <DialogActions
                sx={{
                    px: 3,
                    pb: 3,
                    pt: 0
                }}>
                <Button
                    type="button"
                    onClick={handleClose}>
                    {ADD_VEHICLE_DIALOG_LABELS.cancel}
                </Button>

                <Button
                    type="button"
                    variant="contained"
                    disabled={!canSave || saving}
                    onClick={handleSave}>
                    {saving
                        ? ADD_VEHICLE_DIALOG_LABELS.saving
                        : ADD_VEHICLE_DIALOG_LABELS.save}
                </Button>
            </DialogActions>
        </Dialog>
    );
}