package com.lumenprototype.function.upscale;

import com.lumenprototype.api.AiService;
import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.comm.VideoInfo;
import com.lumenprototype.config.value.FfmpegConfig;
import com.lumenprototype.exception.FileTransferException;
import com.lumenprototype.exception.MetadataValidationException;
import com.lumenprototype.exception.ResourceNotFoundException;
import com.lumenprototype.function.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.FileSuffixType;
import com.lumenprototype.function.upscale.entity.Function;
import com.lumenprototype.function.upscale.entity.FunctionName;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

    private final UpscaleRepository upscaleRepository;
    private final FileStorageService fileStorageService;
    private final FfmpegConfig ffmpegConfig;
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

    // 업스케일
    @Override
    @Transactional
    public List<VideoInfo> upscale(MultipartFile multipartFile, ProcessingTask processingTask) {

        List<VideoInfo> videoInfoList = new ArrayList<>();

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
            fileStorageService.storeFile(file, fileName, FileSuffixType.AFTER);

            // 2. 썸네일 저장
            captureFrameFromVideo(file, fileName);


            // 3. AI통신
            if (processingTask.getModelName().startsWith("Pixell")) {
                // Pixell API통신
                //File pixelFile = pixellAPIService.pixellAPI(multipartFile);
                //fileStorageService.storeFile(pixelFile, fileName, FileSuffixType.AFTER);
                log.error("pixell");

            } else {
                // 자체 모델 API통신
                //File upscaleFile =aiService.videoUpscale(multipartFile);
                //fileStorageService.storeFile(upscaleFile, fileName, FileSuffixType.AFTER);
                log.error("API");
            }

            // 4. DB 메타 데이터 저장
            Function function = processFunctionName(processingTask);
            processingTask.setFunction(function);
            upscaleRepository.save(processingTask);

            // 파일 URL 구성
            String beforeUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.BEFORE);
            String afterUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.AFTER);

            videoInfoList.add(extractVideoInfo(file,beforeUrl));
            videoInfoList.add(extractVideoInfo(file,afterUrl));



        } catch (IOException e) {
            throw new FileTransferException("Failed to read multipart file: " + e.getMessage(), e);
        }


        // 파일 URL을 포함한 객체 반환
        return videoInfoList;
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


    // 프레임 추출
    public VideoInfo extractVideoInfo(File file, String beforeUrl) throws IOException {
        // ffprobe 실행 경로를 가져옵니다.
        String ffprobePath = ffmpegConfig.getFfmpegPath() + "\\ffprobe.exe"; // .exe 확장자를 포함한 ffprobe 경로

        // ffprobe 명령을 실행하기 위한 ProcessBuilder를 설정합니다.
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
                // ffprobe 출력 값을 ','로 분리합니다.
                String[] values = line.split(",");
                if (values.length >= 2) {
                    // 비디오 길이를 가져옵니다.
                    double duration = Double.parseDouble(values[1]);
                    // 프레임 속도 정보를 분리합니다.
                    String[] frameRateParts = values[0].split("/");

                    if (frameRateParts.length == 2) {
                        try {
                            // 분자와 분모를 파싱하고 계산합니다.
                            double numerator = Double.parseDouble(frameRateParts[0]);
                            double denominator = Double.parseDouble(frameRateParts[1]);
                            double fps = numerator / denominator; // 프레임 속도 계산

                            // 총 프레임 수를 계산합니다.
                            int totalFrames = (int) (fps * duration);

                            // 결과를 반환합니다.
                            return new VideoInfo(beforeUrl, totalFrames, fps);
                        } catch (NumberFormatException e) {
                            log.error("Error parsing frame rate: {}, error: {}", values[1], e.getMessage());
                        }
                    } else {
                        log.error("Unexpected frame rate format: {}", values[1]);
                    }
                } else {
                    log.error("Unexpected ffprobe output: {}", line);
                }
            }
        }

        return null;
    }


}
