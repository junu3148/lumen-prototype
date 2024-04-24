package com.lumenprototype.function.upscale;


import org.springframework.http.ResponseEntity;

public interface UpscaleService {

     ResponseEntity<?>  findAllHistory(Integer userId);
}

