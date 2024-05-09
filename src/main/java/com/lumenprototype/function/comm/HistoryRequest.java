package com.lumenprototype.function.comm;

import com.lumenprototype.function.upscale.entity.FunctionName;
import lombok.Data;

@Data
public class HistoryRequest {
    private Integer userId;
    private FunctionName functionName;
    private String fileName;

}