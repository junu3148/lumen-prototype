package com.lumenprototype.config;

import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.function.upscale.UpscaleRepository;
import com.lumenprototype.function.upscale.UpscaleService;
import com.lumenprototype.function.upscale.UpscaleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("com.lumenprototype")
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    @Primary
    public UpscaleService upscaleService(UpscaleRepository upscaleRepository, FileStorageService fileStorageService, FfmpegConfig ffmpegConfig) {
        return new UpscaleServiceImpl(upscaleRepository, fileStorageService, ffmpegConfig);
    }

}
