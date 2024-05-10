package com.lumenprototype.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface AiService {

    /**
     * Fast API 서버와의 연결을 테스트합니다.
     * 이 메소드는 연결의 성공 여부를 문자열로 반환하여 연결 상태를 확인할 수 있게 합니다.
     *
     * @return 서버 연결 테스트 결과를 문자열로 반환합니다.
     */
    String testConnectionToFastApi();

    /**
     * 제공된 MultipartFile 형식의 비디오 파일을 업스케일링합니다.
     * 업스케일링은 동영상의 해상도를 향상시키는 과정을 말하며, 이 과정을 통해 비디오의 품질을 개선합니다.
     *
     * @param multipartFile 업스케일링할 비디오 파일이 포함된 MultipartFile 객체입니다.
     * @return 업스케일링된 비디오 파일을 File 객체로 반환합니다.
     */
    File videoUpscale(MultipartFile multipartFile);

}
