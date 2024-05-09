package com.lumenprototype.comm;

import com.lumenprototype.function.upscale.entity.FunctionName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileInfo {
    private Long taskId;
    private String fileName;
    private FunctionName functionName;
    private String parameters;
    private String date;
    private Integer userId;
    private String status;
    private String result;
}