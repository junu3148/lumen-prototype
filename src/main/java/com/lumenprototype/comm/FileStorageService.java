package com.lumenprototype.comm;


import com.lumenprototype.config.value.FileStorageProperties;
import com.lumenprototype.exception.FileStorageException;
import com.lumenprototype.function.upscale.entity.FileSuffixType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@Primary
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    public void storeFile(File file, String uuid, FileSuffixType fileSuffixType) {
        try {
            validateFile(file);
        } catch (IOException e) {
            throw new FileStorageException("Validation failed for the file: " + e.getMessage(), e);
        }

        String extension = getFileExtension(file);
        String fileName = constructFileName(uuid, fileSuffixType, extension);
        File targetFile = prepareTargetFile(fileName);

        try {
            Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + e.getMessage(), e);
        }

       // return constructFileDownloadUri(fileName);
    }


    private void validateFile(File file) throws IOException {
        if (!file.exists() || file.length() == 0) {
            throw new IOException("File does not exist or is empty");
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf(".");
        if (lastIndexOfDot > 0 && lastIndexOfDot < name.length() - 1) {
            return name.substring(lastIndexOfDot);
        } else {
            throw new IllegalArgumentException("The file does not have a proper extension.");
        }
    }

    private File prepareTargetFile(String fileName) {
        String filePath = fileStorageProperties.getUploadDir() + fileName;
        return new File(filePath);
    }

    private String constructFileDownloadUri(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/upload/")
                .path(fileName)
                .toUriString();
    }

    private String constructFileName(String uuid, FileSuffixType fileSuffixType, String extension) {
        String suffix = fileSuffixType.getSuffix();
        return uuid + suffix + extension;
    }

    // getFileUrl 메서드 추가
    public String getFileUrl(String fileName, FileSuffixType fileSuffixType) {
        // 파일명을 구성합니다.
        return fileStorageProperties.getBaseUrl() + constructFileName(fileName, fileSuffixType, "");
    }


}