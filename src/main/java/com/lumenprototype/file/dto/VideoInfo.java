package com.lumenprototype.file.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoInfo {

    private String fileUrl;
    private int totalFrames;
    private double  fps;

    public VideoInfo(String fileUrl, int totalFrames, double fps) {
        this.fileUrl = fileUrl;
        this.totalFrames = totalFrames;
        this.fps = fps;
    }

}
