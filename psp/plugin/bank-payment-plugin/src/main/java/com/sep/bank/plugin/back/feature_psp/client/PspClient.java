package com.sep.bank.plugin.back.feature_psp.client;

import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginSyncResponse;
import com.sep.bank.plugin.back.shared.security.SignatureHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PspClient {

    private final RestClient restClient;

    public PspClient(
            @Value("${app.psp.base-url}") String pspBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(pspBaseUrl)
                .build();
    }

    public PluginSyncResponse syncPlugin(
            String pluginCode,
            String timestamp,
            String signature,
            String requestBody
    ) {
        return restClient.post()
                .uri("/api/plugins/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .header(SignatureHeaders.PLUGIN_CODE, pluginCode)
                .header(SignatureHeaders.TIMESTAMP, timestamp)
                .header(SignatureHeaders.SIGNATURE, signature)
                .body(requestBody)
                .retrieve()
                .body(PluginSyncResponse.class);
    }

}