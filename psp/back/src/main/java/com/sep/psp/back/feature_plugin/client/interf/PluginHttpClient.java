package com.sep.psp.back.feature_plugin.client.interf;

import com.sep.psp.back.feature_plugin.model.PaymentPlugin;

public interface PluginHttpClient {

    <T> T post(
            PaymentPlugin plugin,
            String path,
            Object request,
            Class<T> responseType
    );

    void postWithoutResponse(
            PaymentPlugin plugin,
            String path,
            Object request,
            boolean requireActive
    );

}