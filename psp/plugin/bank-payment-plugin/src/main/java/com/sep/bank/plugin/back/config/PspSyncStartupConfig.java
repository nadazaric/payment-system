package com.sep.bank.plugin.back.config;

import com.sep.bank.plugin.back.feature_psp.service.interf.PspSyncService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PspSyncStartupConfig {

    @Bean
    public ApplicationRunner syncWithPspOnStartup(PspSyncService pspSyncService) {
        return args -> pspSyncService.syncWithPsp();
    }

}