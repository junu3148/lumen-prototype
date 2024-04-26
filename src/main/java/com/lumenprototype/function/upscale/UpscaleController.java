package com.lumenprototype.function.upscale;

import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.FileUrl;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
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


    // 히스토리 조회
    @PostMapping("history")
    public ResponseEntity<List<FileInfo>> getHistory(@RequestBody HistoryRequest historyRequest) {
        return upscaleService.findAllHistory(historyRequest);
    }

    // 업스케일
    @PostMapping("upscaling")
    public FileUrl upscaling(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {

        return upscaleService.upscale(file, processingTask);

    }


}

