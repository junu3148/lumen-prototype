package com.lumenprototype.utill.ffmpeg;

import com.lumenprototype.file.FileStorageService;
import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.upscale.en.FileSuffixType;
import com.lumenprototype.function.upscale.entity.ProcessingTask;

import java.util.Arrays;
import java.util.List;

public final class VideoInfoUtils {

    private VideoInfoUtils() {
        // Private constructor to prevent instantiation
    }

    // 비디오 정보 리스트 생성 및 추가
    public static List<VideoInfo> buildVideoInfoList(FileStorageService fileStorageService, ProcessingTask task) {
        String fileName = task.getFileName();
        String beforeUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.BEFORE);
        String afterUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.AFTER);

        return Arrays.asList(
                new VideoInfo(beforeUrl, task.getTotalFrames(), task.getFps()),
                new VideoInfo(afterUrl, task.getTotalFrames(), task.getFps())
        );
    }
}
