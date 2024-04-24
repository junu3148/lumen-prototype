package com.lumenprototype.config;

import com.lumenprototype.function.upscale.UpscaleService;
import com.lumenprototype.function.upscale.UpscaleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.lumenprototype")
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    public UpscaleService upscaleService(){
        return new UpscaleServiceImpl();
    }


}
