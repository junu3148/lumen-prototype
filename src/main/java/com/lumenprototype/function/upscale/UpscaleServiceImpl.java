package com.lumenprototype.function.upscale;

import com.lumenprototype.api.AiService;
import com.lumenprototype.api.PixellAPIService;
import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.comm.FileUrl;
import com.lumenprototype.config.FfmpegConfig;
import com.lumenprototype.exception.FileTransferException;
import com.lumenprototype.exception.MetadataValidationException;
import com.lumenprototype.exception.ResourceNotFoundException;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.FileSuffixType;
import com.lumenprototype.function.upscale.entity.Function;
import com.lumenprototype.function.upscale.entity.FunctionName;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;
import java.util.UUID;


@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

    private final UpscaleRepository upscaleRepository;
    private final FileStorageService fileStorageService;
    private final FfmpegConfig ffmpegConfig;
    private final PixellAPIService pixellAPIService;
    private final AiService aiService;

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
                            task.getFileName() + "_img",
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

    /*
    // 업스케일
    @Override
    @Transactional
    public FileUrl upscale(MultipartFile multipartFile, ProcessingTask processingTask) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileTransferException("파일이 전송되지 않았습니다.", null);
        }
        String fileName = String.valueOf(UUID.randomUUID());
        processingTask.setFileName(fileName);

        try {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            InputStream inputStream = multipartFile.getInputStream();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 1. 원본 저장
            fileStorageService.storeFile(file, fileName, FileSuffixType.BEFORE);

            // 2. 썸네일 저장
            captureFrameFromVideo(file, fileName);

            // 3. AI통신
            fileStorageService.storeFile(file, fileName, FileSuffixType.AFTER);


            // 4. DB 메타 데이터 저장
            String functionNameStr = processingTask.getFunctionName();
            if (functionNameStr == null || functionNameStr.isEmpty()) {
                throw new MetadataValidationException("Function name cannot be null or empty");
            }

            FunctionName functionName;
            try {
                functionName = FunctionName.valueOf(functionNameStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new MetadataValidationException("Invalid function name: " + functionNameStr);
            }

            Function function = upscaleRepository.findByFunctionName(functionName)
                    .orElseThrow(() -> new ResourceNotFoundException("Function not found: " + functionNameStr));

            processingTask.setFunction(function);
            upscaleRepository.save(processingTask);

        } catch (IOException e) {
            throw new FileTransferException("Failed to read multipart file: " + e.getMessage(), e);
        }

        // 파일 URL 구성
        String beforeUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.BEFORE);
        String afterUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.AFTER);

        // 파일 URL을 포함한 객체 반환
        return FileUrl.builder()
                .beforeUrl(beforeUrl)
                .afterUrl(afterUrl)
                .build();
    }

*/


    // 업스케일
    @Override
    @Transactional
    public FileUrl upscale(MultipartFile multipartFile, ProcessingTask processingTask) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileTransferException("파일이 전송되지 않았습니다.", null);
        }
        String fileName = String.valueOf(UUID.randomUUID());
        processingTask.setFileName(fileName);

        String afterUrl;
        try {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            InputStream inputStream = multipartFile.getInputStream();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 1. 원본 저장
            fileStorageService.storeFile(file, fileName, FileSuffixType.BEFORE);

            // 2. 썸네일 저장
            captureFrameFromVideo(file, fileName);


            afterUrl = "";
            // 3. AI통신
            if (processingTask.getModelName().startsWith("Pixell")) {
                // Pixell API통신
                File pixelFile = pixellAPIService.pixellAPI(multipartFile);
                //fileStorageService.storeFile(pixelFile, fileName, FileSuffixType.AFTER);

            } else {
                // 자체 모델 API통신

                String prompt = "happy dog";
                afterUrl = aiService.promptTextToVideo(prompt);
                System.out.println(afterUrl);

                //fileStorageService.storeFile(file, fileName, FileSuffixType.AFTER);
            }

            // 4. DB 메타 데이터 저장
            Function function = processFunctionName(processingTask);
            processingTask.setFunction(function);
            upscaleRepository.save(processingTask);

        } catch (IOException e) {
            throw new FileTransferException("Failed to read multipart file: " + e.getMessage(), e);
        }

        // 파일 URL 구성
        String beforeUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.BEFORE);
        //String afterUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.AFTER);

        // 파일 URL을 포함한 객체 반환
        return FileUrl.builder()
                .beforeUrl(beforeUrl)
                .afterUrl(afterUrl)
                .build();
    }


    private Function processFunctionName(ProcessingTask processingTask) throws MetadataValidationException, ResourceNotFoundException {
        String functionNameStr = processingTask.getFunctionName();
        if (functionNameStr == null || functionNameStr.isEmpty()) {
            throw new MetadataValidationException("Function name cannot be null or empty");
        }

        FunctionName functionName;
        try {
            functionName = FunctionName.valueOf(functionNameStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new MetadataValidationException("Invalid function name: " + functionNameStr);
        }

        return upscaleRepository.findByFunctionName(functionName)
                .orElseThrow(() -> new ResourceNotFoundException("Function not found: " + functionNameStr));
    }


    // 썸네일 추출
    public void captureFrameFromVideo(File videoFile, String fileName) {
        File outputFile = null;
        try {
            String outputFileName = fileName + ".jpg";
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

}