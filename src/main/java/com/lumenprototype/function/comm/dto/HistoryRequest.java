package com.lumenprototype.function.comm.dto;

import com.lumenprototype.function.upscale.en.FunctionName;
import lombok.Data;

@Data
public class HistoryRequest {
    private Integer userId;
    private FunctionName functionName;
    private String fileName;

}