package com.lumenprototype.comm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUrl {

    private String beforeUrl;
    private String afterUrl;
}
