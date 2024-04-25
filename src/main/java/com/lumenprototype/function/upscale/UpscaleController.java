package com.lumenprototype.function.upscale;

import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
//@RequiredArgsConstructor
@RequestMapping("upscale/")
public class UpscaleController {

    private final UpscaleService upscaleService;

    public UpscaleController(@Qualifier("upscaleServiceImpl") UpscaleService upscaleService) {
        this.upscaleService = upscaleService;
    }

    // 히스토리 조회
    @PostMapping("history")
    public ResponseEntity<List<FileInfo>> getHistory(@RequestBody HistoryRequest historyRequest) {
        return upscaleService.findAllHistory(historyRequest);
    }

    // 업스케일
    @PostMapping("upscaling")
    public void upscaling(MultipartFile file) {
        System.out.println("호출");

    }


}
