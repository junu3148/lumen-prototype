package com.lumenprototype.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class AiService {

    @Value("${flask.server.url}")
    private String flaskServerUrl;


    // 연결 테스트
    public String testConnectionToFastApi() {
        String apiUrl = flaskServerUrl + "/"; // FastAPI 서버 URL

        // RestTemplate을 사용하여 GET 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        // 연결 성공 여부에 따라 메시지 반환
        return response.getStatusCode().is2xxSuccessful() ? "Connection successful!" : "Connection failed!";
    }

    // 업스케일된 비디오를 반환하는 메서드
    public File videoUpscale(MultipartFile multipartFile) {
        String apiUrl = flaskServerUrl + "/video"; // FastAPI 서버 URL

        // RestTemplate 설정
        RestTemplate restTemplate = new RestTemplate();

        // HttpHeaders 객체를 생성하고, Content-Type을 multipart/form-data로 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 파일 데이터와 JSON 데이터를 포함하는 MultiValueMap 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            // MultipartFile을 Resource로 변환
            Resource resource = new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename(); // 파일 이름 설정
                }
            };

            // 파일 데이터를 'video'로 변경하여 추가
            body.add("video", resource);

            // scaleOption을 문자열로 추가
            body.add("scaleOption", "1");

        } catch (IOException e) {
            e.printStackTrace();
            return null; // 오류 발생 시 null 반환
        }

        // HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // POST 요청 보내기
        ResponseEntity<byte[]> response = restTemplate.postForEntity(apiUrl, requestEntity, byte[].class);

        // 서버로부터의 응답을 파일로 저장
        try {
            if (response.getStatusCode().is2xxSuccessful()) {
                // 임시 파일 생성
                File outputFile = File.createTempFile("upscaled-video", ".mp4");
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(Objects.requireNonNull(response.getBody())); // 응답을 파일에 쓰기
                }
                return outputFile; // 파일 반환
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 오류 발생 시 null 반환
    }


}
