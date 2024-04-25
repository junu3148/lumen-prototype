package com.lumenprototype.comm;


import com.lumenprototype.function.upscale.entity.FileSuffixType;
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

    public String storeFile(MultipartFile file, String uuid, FileSuffixType fileSuffixType) {
        String fileDownloadUri = null;

        try {
            if (file.isEmpty()) {
                throw new IOException("Empty file cannot be uploaded");
            }

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new IllegalArgumentException("The file does not have a proper extension.");
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = constructFileName(uuid, fileSuffixType, extension);
            String filePath = fileStorageProperties.getUploadDir() + fileName;
            File targetFile = new File(filePath);

            file.transferTo(targetFile);

            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/upload/")
                    .path(fileName)
                    .toUriString();

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return fileDownloadUri;
    }


    // 이전에 String type을 받던 부분을 FileSuffixType fileSuffixType으로 받도록 변경
    private String constructFileName(String uuid, FileSuffixType fileSuffixType, String extension) {
        String suffix = fileSuffixType.getSuffix();
        return uuid + suffix + extension;
    }

}