package com.lumenprototype.api;

import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class ApiController {

    private final AiService aiService;

    // Fast API 테스트
    @GetMapping("test")
    public String testConnectionToFastApi() {
        return aiService.testConnectionToFastApi();
    }





    @PostMapping("{type}")
    public void test(@PathVariable("type") String type, @RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {

        System.out.println(type);

        if (type == "upscaling") {


        }
    }

    // 업스케일
    @PostMapping("Upscaling")
    public List<VideoInfo> upscaling(@RequestParam("file") MultipartFile file, @ModelAttribute ProcessingTask processingTask) {
        processingTask.setFunctionName(processingTask.getFunctionName().toUpperCase());

        System.out.println(processingTask);
        return null;
    }

    // 업스케일
    @PostMapping("Denoising")
    public List<VideoInfo> denoising(@ModelAttribute ProcessingTask processingTask) {
        processingTask.setFunctionName(processingTask.getFunctionName().toUpperCase());

        System.out.println(processingTask);
        return null;
    }


}
