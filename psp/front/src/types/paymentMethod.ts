export type PaymentMethodConfigField = {
    fieldName: string;
    fieldType: string;
};

export type PaymentMethod = {
    code: string;
    displayName: string;
    active: boolean;
    pluginCode: string;
    configSchemaJson: string;
};