package com.lumenprototype.function.upscale;

import com.lumenprototype.api.AiServiceImpl;
import com.lumenprototype.comm.dto.FileInfo;
import com.lumenprototype.comm.dto.VideoInfo;
import com.lumenprototype.function.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
public class UpscaleController {

    private final UpscaleService upscaleService;
    private final AiServiceImpl aiServiceImpl;


    // 히스토리 리스트 조회
    @PostMapping("history-list")
    public ResponseEntity<List<FileInfo>> getHistoryList(@RequestBody HistoryRequest historyRequest) {
        return upscaleService.findAllHistory(historyRequest);
    }

    // 업스케일
    @PostMapping("Upscaling")
    public List<VideoInfo> upscaling(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {
        processingTask.setFunctionName(processingTask.getFunctionName().toUpperCase());
        return upscaleService.upscale(file, processingTask);
    }

    // 히스토리 조회
    @PostMapping("history")
    public List<VideoInfo> getHistory(@RequestBody HistoryRequest historyRequest) {
        return upscaleService.findHistory(historyRequest);
    }

    // 테스트
    @GetMapping("test")
    public String testConnectionToFastApi() {
        return aiServiceImpl.testConnectionToFastApi();
    }

}

