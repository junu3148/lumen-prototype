package com.lumenprototype.function.comm;

import com.lumenprototype.file.FileStorageService;
import com.lumenprototype.file.dto.FileInfo;
import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.comm.dto.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import com.lumenprototype.utill.ffmpeg.VideoInfoUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final FileStorageService fileStorageService;


    // 히스토리 리시트 조회
    @Override
    @Transactional
    public ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 날짜 포맷 정의

        try {
            List<ProcessingTask> histories = historyRepository.findAllByUserIdAndFunctionName(
                    historyRequest.getUserId(),
                    historyRequest.getFunctionName()
            );
            if (histories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<FileInfo> fileInfos = histories.stream()
                    .map(task -> new FileInfo(
                            task.getTaskId(),
                            task.getFileName() + "_img",
                            task.getOrigName(),
                            task.getFunction().getFunctionName(),
                            task.getParameters(),
                            sdf.format(task.getDate()),  // Date 객체를 포맷된 문자열로 변환
                            task.getUserId(),
                            task.getTotalFrames(),
                            task.getFps()

                    ))
                    .toList();

            return ResponseEntity.ok(fileInfos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 히스토리 조회
    @Override
    public List<VideoInfo> findHistory(HistoryRequest historyRequest) {
        // 데이터베이스에서 파일 이름으로 ProcessingTask 검색
        ProcessingTask processingTask = historyRepository.findByFileName(historyRequest.getFileName());
        if (processingTask == null) {
            log.info("No task found for the file name: {}", historyRequest.getFileName());
            return new ArrayList<>(); // 빈 리스트 반환
        }

        // 비디오 정보 리스트 생성 및 반환
        return VideoInfoUtils.buildVideoInfoList(fileStorageService, processingTask);
    }


}
