package com.lumenprototype.function.upscale;


import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.VideoInfo;
import com.lumenprototype.function.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UpscaleService {

    /**
     * 요청된 히스토리 정보에 기반하여 모든 파일 정보를 검색합니다.
     *
     * @param historyRequest 히스토리 정보를 요청하는 객체입니다.
     *                       검색에 필요한 필터링 조건을 포함합니다.
     * @return 검색된 파일 정보 리스트를 담은 ResponseEntity를 반환합니다.
     */
    ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest);

    /**
     * 비디오 업스케일링을 수행하고 비디오 정보 리스트를 반환합니다.
     * 업스케일링은 동영상의 화질을 향상시키는 작업을 말합니다.
     *
     * @param file 업스케일링할 동영상을 포함한 MultipartFile 객체입니다.
     * @param processingTask 업스케일링 작업을 제어하는 작업 정보입니다.
     * @return 업스케일링된 비디오 정보를 담은 리스트를 반환합니다.
     */
    List<VideoInfo> upscale(MultipartFile file, ProcessingTask processingTask);



    List<VideoInfo> findHistory(HistoryRequest historyRequest);


}

