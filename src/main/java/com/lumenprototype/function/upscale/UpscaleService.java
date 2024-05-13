package com.lumenprototype.function.upscale;


import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UpscaleService {

    /**
     * 비디오 업스케일링을 수행하고 비디오 정보 리스트를 반환합니다.
     * 업스케일링은 동영상의 화질을 향상시키는 작업을 말합니다.
     *
     * @param file 업스케일링할 동영상을 포함한 MultipartFile 객체입니다.
     * @param processingTask 업스케일링 작업을 제어하는 작업 정보입니다.
     * @return 업스케일링된 비디오 정보를 담은 리스트를 반환합니다.
     */
    List<VideoInfo> upscaling(MultipartFile file, ProcessingTask processingTask);



}

