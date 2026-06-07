export type PaymentPlugin = {
    pluginCode: string;
    displayName: string;
    baseUrl: string | null;
    activeByAdmin: boolean;
    active: boolean;
};

export type CreateExpectedPluginRequest = {
    pluginCode: string;
    displayName: string;
};

export type CreateExpectedPluginResponse = {
    pluginCode: string;
    displayName: string;
    pluginSecret: string;
    activeByAdmin: boolean;
    active: boolean;
    message: string;
};

export type UpdatePluginStatusRequest = {
    pluginCode: string;
    activeByAdmin: boolean;
};