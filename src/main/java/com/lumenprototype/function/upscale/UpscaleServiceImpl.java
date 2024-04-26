package com.lumenprototype.function.upscale;

import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.config.FfmpegConfig;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.FileSuffixType;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

    private final UpscaleRepository upscaleRepository;
    private final FileStorageService fileStorageService;
    private final FfmpegConfig ffmpegConfig;

    // 히스토리 조회
    @Override
    @Transactional
    public ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest) {

        try {
            List<ProcessingTask> histories = upscaleRepository.findAllByUserIdAndFunctionName(historyRequest.getUserId(), historyRequest.getFunctionName());
            if (histories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<FileInfo> fileInfos = histories.stream()
                    .map(task -> new FileInfo(
                            task.getTaskId(),
                            task.getFileName(),
                            task.getFunction().getFunctionName(),
                            task.getParameters(),
                            task.getDate(),
                            task.getUserId(),
                            task.getStatus(),
                            task.getResult()
                    ))
                    .toList();
            return ResponseEntity.ok(fileInfos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public void upscale(MultipartFile multipartFile, ProcessingTask processingTask) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new RuntimeException("파일이 전송되지 않았습니다.");
        }
        String fileName = String.valueOf(UUID.randomUUID());
        processingTask.setFileName(fileName);

        try {
            File file = new File(multipartFile.getOriginalFilename());
            // 파일 스트림을 이용하여 파일 객체 생성
            InputStream inputStream = multipartFile.getInputStream();
            // 파일 객체 생성 시 스트림을 사용하여 파일을 생성합니다.
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 원본 저장
            fileStorageService.storeFile(file, fileName, FileSuffixType.BEFORE);

            // 썸네일 저장
            captureFrameFromVideo(file, fileName);


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read multipart file: " + e.getMessage());
        }




        System.out.println("processing task: " + processingTask);
    }


    public void captureFrameFromVideo(File videoFile, String fileName) {
        File outputFile = null;
        try {
            String outputFileName = fileName + ".jpg";
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            System.out.println("이건 뭔가 " +
                    ""+tempDir);
            outputFile = new File(tempDir.toFile(), outputFileName);

            List<String> command = Arrays.asList(
                    ffmpegConfig.getFfmpegPath() + "\\ffmpeg",
                    "-i", videoFile.getAbsolutePath(),
                    "-ss", "00:00:02",
                    "-frames:v", "1",
                    outputFile.getAbsolutePath()
            );



            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            process.waitFor();

            if (process.exitValue() != 0) {
                throw new IllegalStateException("ffmpeg failed to process video");
            }

            fileStorageService.storeFile(outputFile, fileName, FileSuffixType.IMAGE);

        } catch (IOException | InterruptedException e) {
            // 예외 처리 로직
            e.printStackTrace();
        }
    }



}