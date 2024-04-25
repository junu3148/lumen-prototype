package com.lumenprototype.function.upscale;

import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.FileSuffixType;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

    private final UpscaleRepository upscaleRepository;
    private final FileStorageService fileStorageService;

    // 히스토리 조회
    @Override
    @Transactional
    public ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest) {

        try {
            List<ProcessingTask> histories = upscaleRepository.findAllByUserIdAndFunctionName(historyRequest.getUserId(), historyRequest.getFunctionName());
            if (histories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<FileInfo> fileInfos = histories.stream()
                    .map(task -> new FileInfo(
                            task.getTaskId(),
                            task.getFileName(),
                            task.getFunction().getFunctionName(),
                            task.getParameters(),
                            task.getDate(),
                            task.getUserId(),
                            task.getStatus(),
                            task.getResult()
                    ))
                    .toList();
            return ResponseEntity.ok(fileInfos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 업스케일링
    @Override
    public void upscale(MultipartFile file, ProcessingTask processingTask) {

        // UUID로 파일명 생성
        String fileName = String.valueOf(UUID.randomUUID());
        processingTask.setFileName(fileName);

        // 1. 원본 파일 저장
        fileStorageService.storeFile(file, fileName, FileSuffixType.BEFORE);


        System.out.println("processing task: " + processingTask);


    }


}