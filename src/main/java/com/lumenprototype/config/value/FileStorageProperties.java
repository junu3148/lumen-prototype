package com.lumenprototype.config.value;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FileStorageProperties {
    // uploadDir에 대한 getter 메소드
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.baseUrl}")
    private String baseUrl;


}