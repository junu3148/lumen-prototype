package com.lumenprototype.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class PixellAPIService {

    @Value("${pixell.api.key}")
    private String apiKey;

    @Value("${pixell.api.url}")
    private String baseUrl;

    private String contentType = "videl";


    private final RestTemplate restTemplate;

    public PixellAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public File pixellAPI(MultipartFile multipartFile) {

        System.out.println(multipartFile.getOriginalFilename());

        System.out.println("dd");


        try {
            // 1. 파일 업로드
            String fileId = uploadFile(multipartFile, contentType);

            // 2. 업스케일링
            requestProcessing(fileId,contentType,"dd");

        } catch (Exception e) {

        }

        return null;
    }

    // 1. 파일업로드
    public String uploadFile(MultipartFile file, String contentType) throws Exception {
        String uploadUrl = baseUrl + "/api/upload/" + contentType;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", apiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<HashMap> response = restTemplate.postForEntity(uploadUrl, requestEntity, HashMap.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().get("fileId").toString();
        } else {
            throw new Exception("Failed to upload file: " + response.getBody().get("resMsg"));
        }
    }

    // 2. 업스케일링
    public void requestProcessing(String fileId, String contentType, String model) {
        String requestUrl = baseUrl + "/api/work/request/" + contentType;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        Map<String, String> body = new HashMap<>();
        body.put("fileId", fileId);
        body.put("model", model);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<HashMap> response = restTemplate.postForEntity(requestUrl, requestEntity, HashMap.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to request processing: " + response.getBody().get("resMsg"));
        }
    }

    public String getDownloadLink(String fileId) {
        String requestUrl = baseUrl + "/api/download/link?fileId=" + fileId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        ResponseEntity<HashMap> response = restTemplate.getForEntity(requestUrl, HashMap.class, headers);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().get("url").toString();
        } else {
            throw new RuntimeException("Failed to get download link: " + response.getBody().get("resMsg"));
        }
    }
}

