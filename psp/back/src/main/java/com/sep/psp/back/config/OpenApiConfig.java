package com.sep.psp.back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pspCoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PSP Core API")
                        .version("1.0.0")
                        .description("API documentation for the PSP Core backend."));
    }

}
