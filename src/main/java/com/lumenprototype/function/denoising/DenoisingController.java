package com.lumenprototype.function.denoising;

import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/")
public class DenoisingController {


    // 업스케일
    @PostMapping("Denoising")
    public List<VideoInfo> Denoising(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {
        return null;
    }


}
