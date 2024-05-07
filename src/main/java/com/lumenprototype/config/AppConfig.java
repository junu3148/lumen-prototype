package com.lumenprototype.config;

import com.lumenprototype.api.AiService;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.config.value.FfmpegConfig;
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

    // RestTemplate 빈을 정의합니다.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // UpscaleService 빈을 정의합니다.
    // 필요한 다른 빈들을 생성자로 주입받습니다.
    @Bean
    public UpscaleService upscaleService(UpscaleRepository upscaleRepository,
                                         FileStorageService fileStorageService,
                                         FfmpegConfig ffmpegConfig,
                                         AiService aiService) {
        return new UpscaleServiceImpl(upscaleRepository, fileStorageService, ffmpegConfig, aiService);
    }
}