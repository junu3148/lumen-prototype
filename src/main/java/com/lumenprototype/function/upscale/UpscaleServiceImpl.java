package com.lumenprototype.function.upscale;

import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService{

    private final UpscaleRepository upscaleRepository;

    // 히스토리 조회
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
}