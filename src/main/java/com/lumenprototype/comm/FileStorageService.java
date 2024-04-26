package com.lumenprototype.comm;


import com.lumenprototype.function.upscale.entity.FileSuffixType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@Primary
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

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


    // 파일 반환
    public ResponseEntity<Resource> downloadFile(String fileName) {
        System.out.println("1" + fileName);
        try {
            // 파일 저장 위치를 구성하고, 요청된 파일에 대한 파일 객체를 생성
            File file = new File(fileStorageProperties.getUploadDir(), fileName);

            System.out.println("2" + file.getAbsolutePath());

            if (file.exists()) {
                // 파일의 InputStreamResource를 생성
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

                // 파일을 다운로드 할 수 있도록 ResponseEntity 구성
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .contentLength(file.length())
                        .body(resource);
            } else {
                // 파일이 존재하지 않는 경우 적절한 예외 처리
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (IOException e) {
            // 파일 읽기 오류 또는 기타 IO 관련 예외 처리
            return ResponseEntity.internalServerError().body(null);
        }
    }


}