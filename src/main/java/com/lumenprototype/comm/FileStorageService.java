package com.lumenprototype.comm;


import com.lumenprototype.config.FileStorageProperties;
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
/*
    public String storeFile(File file, String uuid, FileSuffixType fileSuffixType) {
        String fileDownloadUri = null;

        try {
            if (!file.exists() || file.length() == 0) {
                throw new IOException("File does not exist or is empty");
            }

            String extension = "";
            String name = file.getName();
            int lastIndexOfDot = name.lastIndexOf(".");
            if (lastIndexOfDot > 0) {
                extension = name.substring(lastIndexOfDot);
            }
            if (extension.isEmpty()) {
                throw new IllegalArgumentException("The file does not have a proper extension.");
            }

            // 파일명 구성 (예: "uuid_Before.jpg")
            String fileName = constructFileName(uuid, fileSuffixType, extension);
            String filePath = fileStorageProperties.getUploadDir() + fileName;

            // 대상 파일 객체 생성
            File targetFile = new File(filePath);

            // 파일 복사
            Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 파일 다운로드 URI 생성
            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/upload/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException | IllegalArgumentException e) {
            // 예외 발생 시 로그를 출력
            e.printStackTrace();
            // 예외를 호출자에게 전달하거나 적절히 처리할 수 있도록 예외를 다시 던집니다.
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }

        return fileDownloadUri;
    }


    // 이전에 String type을 받던 부분을 FileSuffixType fileSuffixType으로 받도록 변경
    private String constructFileName(String uuid, FileSuffixType fileSuffixType, String extension) {
        String suffix = fileSuffixType.getSuffix();
        return uuid + suffix + extension;
    }

    // getFileUrl 메서드 추가
    public String getFileUrl(String fileName, FileSuffixType fileSuffixType) {
        // 파일명을 구성합니다.
        return fileStorageProperties.getBaseUrl() + constructFileName(fileName, fileSuffixType, "");
    }
*/

    public String storeFile(File file, String uuid, FileSuffixType fileSuffixType) {
        try {
            validateFile(file);
        }catch (IOException ignored){

        }
        String extension = getFileExtension(file);

        String fileName = constructFileName(uuid, fileSuffixType, extension);
        File targetFile = prepareTargetFile(fileName);

        try {
            Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }

        return constructFileDownloadUri(fileName);
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