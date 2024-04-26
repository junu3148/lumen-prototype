package com.lumenprototype.comm;

import com.lumenprototype.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
    private final FileStorageService fileStorageService;
    private final ResourceLoader resourceLoader;

    /*
    // 히스토리 썸네일 반환
    @GetMapping("img/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
        try {
            String imageFileName = filename + "_img.jpg";
            Path file = Paths.get(fileStorageProperties.getUploadDir()).resolve(imageFileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {  // 일반 Exception을 캐치하여 에러 로깅
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("video/{filename:.+}")
    public ResponseEntity<Resource> getVideoFile(@PathVariable("filename") String filename) {
        try {
            String videoFileName = filename + ".mp4";
            Path file = Paths.get(fileStorageProperties.getUploadDir()).resolve(videoFileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            // 비디오 파일이 존재하고 읽을 수 있는 경우
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("video/mp4")) // 비디오의 MIME 타입으로 설정
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
*/

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


