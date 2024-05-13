package com.lumenprototype.file;

import com.lumenprototype.config.value.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("files/")
public class FileDownloadController {

    private final FileStorageProperties fileStorageProperties;

    // 주어진 mediaType과 filename을 기반으로 파일을 찾아 반환합니다.
    @GetMapping("{mediaType}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("mediaType") String mediaType, @PathVariable("filename") String filename) {
        String extension = getExtensionByMediaType(mediaType);
        if (extension == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String finalFileName = filename + extension;
            Path filePath = Paths.get(fileStorageProperties.getUploadDir()).resolve(finalFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(getMediaTypeForFile(extension))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // mediaType에 따라 파일 확장자를 결정하는 헬퍼 메서드
    private String getExtensionByMediaType(String mediaType) {
        return switch (mediaType) {
            case "img" -> ".jpg";
            case "video" -> ".mp4";
            default -> null;
        };
    }

    // 파일 확장자에 따라 적절한 MediaType을 반환하는 헬퍼 메서드
    private MediaType getMediaTypeForFile(String extension) {
        return switch (extension) {
            case ".jpg" -> MediaType.IMAGE_JPEG;
            case ".mp4" -> MediaType.parseMediaType("video/mp4");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}


