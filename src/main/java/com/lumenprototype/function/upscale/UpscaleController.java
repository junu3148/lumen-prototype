package com.lumenprototype.function.upscale;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("upscale")
public class UpscaleController {

    private final UpscaleService upscaleService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer userId) {
        return upscaleService.findAllHistory(userId);
    }



}
