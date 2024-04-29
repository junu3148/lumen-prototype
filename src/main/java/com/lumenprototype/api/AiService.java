package com.lumenprototype.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiService {

    @Value("${flask.server.url}")
    private String flaskServerUrl;

    public String promptTextToVideo(String prompt) {

        System.out.println(prompt);
        // 파이썬 AI 서버 URL
        String aiServerUrl = flaskServerUrl + "/video"; // Flask 서버 URL 예시

        // 프롬프트를 JSON 형식으로 포장
        ObjectMapper mapper = new ObjectMapper();
        String jsonPrompt;
        try {
            jsonPrompt = mapper.writeValueAsString(Map.of("prompt", prompt));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        // HttpHeaders 객체를 생성하고, Content-Type을 application/json으로 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 객체를 생성하여 요청 본문과 헤더를 포함
        HttpEntity<String> entity = new HttpEntity<>(jsonPrompt, headers);

        // RestTemplate을 사용하여 POST 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(aiServerUrl, entity, String.class);

        // 서버로부터의 응답 출력
        System.out.println("Response from Python server: " + response.getBody());

        // 비디오 URL 반환
        return response.getBody();
    }

    public String promptTextToImage(String prompt) {
        // 파이썬 AI 서버 URL
        String aiServerUrl = "http://127.0.0.1:5000/image"; // 로컬에서 실행 중인 경우


        // 프롬프트를 JSON 형식으로 포장
        ObjectMapper mapper = new ObjectMapper();
        String jsonPrompt;
        try {
            jsonPrompt = mapper.writeValueAsString(Map.of("prompt", prompt));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        // HttpHeaders 객체를 생성하고, Content-Type을 application/json으로 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 객체를 생성하여 요청 본문과 헤더를 포함
        HttpEntity<String> entity = new HttpEntity<>(jsonPrompt, headers);

        // RestTemplate을 사용하여 POST 요청을 보냄
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(aiServerUrl, entity, String.class);

        // 서버로부터의 응답 출력
        System.out.println("Response from Python server: " + response.getBody());

        // 비디오 URL 반환
        return response.getBody();
    }

}
