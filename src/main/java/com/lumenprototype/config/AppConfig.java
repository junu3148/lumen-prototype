package com.lumenprototype.config;

import com.lumenprototype.api.AiService;
import com.lumenprototype.api.AiServiceImpl;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.comm.FileStorageServiceImpl;
import com.lumenprototype.config.value.FfmpegConfig;
import com.lumenprototype.config.value.FileStorageProperties;
import com.lumenprototype.function.upscale.UpscaleRepository;
import com.lumenprototype.function.upscale.UpscaleService;
import com.lumenprototype.function.upscale.UpscaleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan("com.lumenprototype")
@RequiredArgsConstructor
public class AppConfig {

    // RestTemplate 빈 정의
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // FileStorageService 빈 정의
    @Bean
    public FileStorageService fileStorageService(FileStorageProperties fileStorageProperties) {
        return new FileStorageServiceImpl(fileStorageProperties);
    }

    // AiService 빈 정의
    @Bean
    public AiService aiService() {
        return new AiServiceImpl();
    }

    // UpscaleService 빈 정의 (의존성 주입 방식 변경)
    @Bean
    public UpscaleService upscaleService(UpscaleRepository upscaleRepository, FfmpegConfig ffmpegConfig, FileStorageService fileStorageService, AiService aiService) {
        return new UpscaleServiceImpl(upscaleRepository, fileStorageService, ffmpegConfig, aiService);
    }

}