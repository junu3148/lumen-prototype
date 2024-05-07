package com.lumenprototype.function.upscale;

import com.lumenprototype.api.AiService;
import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.VideoInfo;
import com.lumenprototype.function.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("upscale/")
public class UpscaleController {

    private final UpscaleService upscaleService;
    private final AiService aiService;


    // 히스토리 조회
    @PostMapping("history")
    public ResponseEntity<List<FileInfo>> getHistory(@RequestBody HistoryRequest historyRequest) {
        return upscaleService.findAllHistory(historyRequest);
    }

    // 업스케일
    @PostMapping("upscaling")
    public List<VideoInfo> upscaling(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {
        return upscaleService.upscale(file, processingTask);
    }

    // 테스트
    @GetMapping("test")
    public String testConnectionToFastApi() {
        return aiService.testConnectionToFastApi();
    }

}

