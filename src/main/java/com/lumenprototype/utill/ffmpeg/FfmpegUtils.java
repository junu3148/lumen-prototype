package com.lumenprototype.utill.ffmpeg;


import com.lumenprototype.config.value.FfmpegConfig;
import com.lumenprototype.exception.CustomException;
import com.lumenprototype.file.FileStorageService;
import com.lumenprototype.function.upscale.en.FileSuffixType;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
public final class FfmpegUtils {

    private FfmpegUtils() {
        // Private constructor to prevent instantiation
    }

    // 비디오의 총 프레임 수와 FPS를 반환하는 메서드
    public static ProcessingTask extractVideoFrameInfo(FfmpegConfig ffmpegConfig, File file) throws IOException {
        String ffprobePath = ffmpegConfig.getFfmpegPath() + "\\ffprobe.exe";
        ProcessBuilder processBuilder = new ProcessBuilder(
                ffprobePath, "-v", "error",
                "-show_entries", "stream=duration,r_frame_rate",
                "-of", "csv=p=0",
                file.getAbsolutePath()
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line != null) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    float duration = Float.parseFloat(values[1]);
                    String[] frameRateParts = values[0].split("/");
                    if (frameRateParts.length == 2) {
                        float numerator = Float.parseFloat(frameRateParts[0]);
                        float denominator = Float.parseFloat(frameRateParts[1]);
                        float fps = numerator / denominator;
                        int totalFrames = (int) (fps * duration);
                        return new ProcessingTask(totalFrames, fps);
                    } else {
                        log.error("Unexpected frame rate format: {}", values[0]);
                    }
                } else {
                    log.error("Unexpected ffprobe output: {}", line);
                }
            }
        }
        return null;
    }

    // 비디오에서 썸네일을 추출하는 메서드
    public static void captureFrameFromVideo(FfmpegConfig ffmpegConfig, FileStorageService fileStorageService, File videoFile, String fileName) {
        if (videoFile == null || !videoFile.exists()) {
            throw new IllegalArgumentException("The video file must exist.");
        }

        String outputFileName = fileName + ".jpg";
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        File outputFile = new File(tempDir.toFile(), outputFileName);

        List<String> command = Arrays.asList(
                ffmpegConfig.getFfmpegPath() + "\\ffmpeg",
                "-i", videoFile.getAbsolutePath(),
                "-ss", "00:00:02",
                "-frames:v", "1",
                outputFile.getAbsolutePath()
        );

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("ffmpeg failed to process video with exit code " + exitCode);
            }
            fileStorageService.storeFile(outputFile, fileName, FileSuffixType.IMAGE);
        } catch (IOException e) {
            throw new CustomException("Failed to start ffmpeg process", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("ffmpeg process was interrupted", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}

