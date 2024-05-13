package com.lumenprototype.file.dto;

import com.lumenprototype.function.upscale.en.FunctionName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileInfo {
    private Long taskId;
    private String fileName;
    private String origName;
    private FunctionName functionName;
    private String parameters;
    private String date;
    private Integer userId;
    private Integer totalFrames;
    private float fps;
}