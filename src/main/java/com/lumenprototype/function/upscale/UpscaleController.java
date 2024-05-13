package com.lumenprototype.function.upscale;

import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
public class UpscaleController {

    private final UpscaleService upscaleService;

    // 업스케일
    @PostMapping("Upscaling")
    public List<VideoInfo> upscaling(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {
        return upscaleService.upscaling(file, processingTask);
    }


}

