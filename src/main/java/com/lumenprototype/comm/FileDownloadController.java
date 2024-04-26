package com.lumenprototype.comm;

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
            e.printStackTrace(); // 콘솔에 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("video/{filename:.+}")
    public ResponseEntity<Resource> getVideoFile(@PathVariable("filename") String filename) {
        try {
            String videoFileName = filename + ".mp4"; // 확장자를 .mp4로 가정
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
            // 예외 발생 시 적절한 예외 처리
            e.printStackTrace(); // 콘솔에 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}