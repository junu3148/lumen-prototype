package com.lumenprototype.comm;


import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ImageUploader {

    private final FileStorageProperties fileStorageProperties;

    public ModelAndView uploadImage(MultipartHttpServletRequest request) {
        ModelAndView mav = new ModelAndView("jsonView");  // ModelAndView 객체 생성, JSON 응답을 위해 "jsonView" 사용

        try {
            MultipartFile uploadFile = request.getFile("upload");  // 요청에서 'upload' 이름으로 전송된 파일을 가져옴
            String originalFileName = Objects.requireNonNull(uploadFile).getOriginalFilename();  // 업로드된 파일의 원본 이름을 가져옴
            String ext = Objects.requireNonNull(originalFileName).substring(originalFileName.indexOf("."));  // 파일 확장자 추출
            String newFileName = UUID.randomUUID() + ext;  // 고유한 파일 이름 생성을 위해 UUID와 확장자 결합
            String savePath = fileStorageProperties.getUploadDir() + newFileName;  // 파일 저장 경로 설정

            File file = new File(savePath);  // 저장 경로를 사용하여 파일 객체 생성
            uploadFile.transferTo(file);  // 파일을 지정된 경로에 저장

            mav.addObject("uploaded", true);  // 업로드 상태를 true로 설정하여 ModelAndView에 추가
            mav.addObject("url", "../upload/" + newFileName);  // 업로드된 파일의 접근 경로를 ModelAndView에 추가
        } catch (IOException e) {
            mav.addObject("uploaded", false);  // IOException 발생 시, 업로드 상태를 false로 설정
            mav.addObject("error", "File upload failed: " + e.getMessage());  // 오류 메시지를 ModelAndView에 추가
        }

        return mav;  // ModelAndView 반환
    }
}