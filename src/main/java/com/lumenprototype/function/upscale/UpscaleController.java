package com.lumenprototype.function.upscale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@RequiredArgsConstructor
@RequestMapping("upscale/")
public class UpscaleController {


    private final ObjectMapper objectMapper;
    private final UpscaleService upscaleService;

    public UpscaleController(ObjectMapper objectMapper, @Qualifier("upscaleServiceImpl") UpscaleService upscaleService) {
        this.objectMapper = objectMapper;
        this.upscaleService = upscaleService;
    }

    // 히스토리 조회
    @PostMapping("history")
    public ResponseEntity<List<FileInfo>> getHistory(@RequestBody HistoryRequest historyRequest) {
        return upscaleService.findAllHistory(historyRequest);
    }

    // 업스케일
    @PostMapping("upscaling")
    public void upscaling(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {

        upscaleService.upscale(file, processingTask);

    }


}

