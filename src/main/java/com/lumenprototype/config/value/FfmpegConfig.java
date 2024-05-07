package com.lumenprototype.config.value;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FfmpegConfig {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${java.path}")
    private String javaPath;
}