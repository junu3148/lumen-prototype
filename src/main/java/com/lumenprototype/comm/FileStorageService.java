package com.lumenprototype.comm;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;

@Service
@Primary
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    public String storeFile(MultipartFile file, String uuid, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Empty file cannot be uploaded");
        }

        // 파일 이름이 null인지 확인하고 확장자 추출
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IllegalArgumentException("The file does not have a proper extension.");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = constructFileName(uuid, type, extension);
        String filePath = fileStorageProperties.getUploadDir() + fileName;
        File targetFile = new File(filePath);

        // 파일 저장
        file.transferTo(targetFile);

        // 생성된 파일에 대한 URL 반환
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/upload/")
                .path(fileName)
                .toUriString();
    }

    private String constructFileName(String uuid, String type, String extension) {
        String suffix = switch (type) {
            case "Before" -> "_Before";
            case "After" -> "_After";
            case "Image" -> "_img";
            default -> "";
        };
        return uuid + suffix + extension;
    }
}