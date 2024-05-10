package com.lumenprototype.function.upscale;

import com.lumenprototype.api.AiService;
import com.lumenprototype.comm.FileStorageService;
import com.lumenprototype.comm.dto.FileInfo;
import com.lumenprototype.comm.dto.VideoInfo;
import com.lumenprototype.config.value.FfmpegConfig;
import com.lumenprototype.exception.CustomException;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

    private final UpscaleRepository upscaleRepository;
    private final FileStorageService fileStorageService;
    private final FfmpegConfig ffmpegConfig;
    private final AiService aiService;


    // 히스토리 리시트 조회
    @Override
    @Transactional
    public ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 날짜 포맷 정의

        try {
            List<ProcessingTask> histories = upscaleRepository.findAllByUserIdAndFunctionName(
                    historyRequest.getUserId(),
                    historyRequest.getFunctionName()
            );
            if (histories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<FileInfo> fileInfos = histories.stream()
                    .map(task -> new FileInfo(
                            task.getTaskId(),
                            task.getFileName() + "_img",
                            task.getOrigName(),
                            task.getFunction().getFunctionName(),
                            task.getParameters(),
                            sdf.format(task.getDate()),  // Date 객체를 포맷된 문자열로 변환
                            task.getUserId(),
                            task.getTotalFrames(),
                            task.getFps()

                    ))
                    .toList();

            return ResponseEntity.ok(fileInfos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 히스토리 조회
    @Override
    public List<VideoInfo> findHistory(HistoryRequest historyRequest) {
        // 데이터베이스에서 파일 이름으로 ProcessingTask 검색
        ProcessingTask processingTask = upscaleRepository.findByFileName(historyRequest.getFileName());
        if (processingTask == null) {
            log.info("No task found for the file name: {}", historyRequest.getFileName());
            return new ArrayList<>(); // 빈 리스트 반환
        }

        // 비디오 정보 리스트 생성 및 반환
        return buildVideoInfoList(processingTask);
    }

    // Upscale
    @Override
    @Transactional
    public List<VideoInfo> upscale(MultipartFile multipartFile, ProcessingTask processingTask) {

        processFile(multipartFile, processingTask);

        validateFile(multipartFile);
        String fileName = UUID.randomUUID().toString();
        processingTask.setFileName(fileName);

        File file = convertToFile(multipartFile);
        try {
            storeOriginalAndProcessedFiles(file, fileName);

            if (isPixellModel(processingTask)) {
                handlePixellProcessing(multipartFile, fileName);
            } else {
                handleCustomModelProcessing(multipartFile, fileName);
            }

            updateProcessingTaskWithFrameInfo(processingTask, file);
            saveMetadata(processingTask);


            return buildVideoInfoList(processingTask);
        } catch (IOException e) {
            throw new FileTransferException("Failed to process file: " + e.getMessage(), e);
        }
    }

    // ProcessingTask 속성 추가
    public void processFile(MultipartFile multipartFile, ProcessingTask processingTask) {
        // 함수 이름을 대문자로 설정
        processingTask.setFunctionName(processingTask.getFunctionName().toUpperCase());

        // 파일 이름에서 확장자를 제거
        Optional.ofNullable(multipartFile.getOriginalFilename())
                .map(f -> {
                    int lastDotIndex = f.lastIndexOf('.');
                    return lastDotIndex == -1 ? f : f.substring(0, lastDotIndex);
                })
                .ifPresent(processingTask::setOrigName);
    }

    // 파일 유효성 검증
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileTransferException("No file uploaded.", null);
        }
    }

    // MultipartFile을 File 객체로 변환
    private File convertToFile(MultipartFile multipartFile) {
        try {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            Files.copy(multipartFile.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            throw new FileTransferException("Error converting multipart file.", e);
        }
    }

    // 원본 및 처리된 파일 저장
    private void storeOriginalAndProcessedFiles(File file, String fileName) {
        fileStorageService.storeFile(file, fileName, FileSuffixType.BEFORE);
    }

    // 썸네일 저장
    private void storeThumbnail(File file, String fileName) {
        captureFrameFromVideo(file, fileName);
    }

    // 모델이 Pixell인지 확인
    private boolean isPixellModel(ProcessingTask task) {
        return task.getModelName().startsWith("Pixell");
    }

    // Pixell 모델 처리
    private void handlePixellProcessing(MultipartFile file, String fileName) {
        log.error("Pixell API processing is not implemented.");
    }

    // 사용자 정의 모델 처리
    private void handleCustomModelProcessing(MultipartFile file, String fileName) {
        /*
        File upscaleFile = aiService.videoUpscale(file);
        fileStorageService.storeFile(upscaleFile, fileName, FileSuffixType.AFTER);
        storeThumbnail(upscaleFile, fileName);
        */
        log.error("Custom model API processing is not implemented.");
    }

    // 프레임 정보를 가지고 처리 작업 업데이트
    private void updateProcessingTaskWithFrameInfo(ProcessingTask task, File file) throws IOException {
        ProcessingTask result = extractVideoFrameInfo(file);
        task.setTotalFrames(result.getTotalFrames());
        task.setFps(result.getFps());
    }

    // 메타 데이터 저장
    private void saveMetadata(ProcessingTask task) {
        Function function = processFunctionName(task);
        task.setFunction(function);
        upscaleRepository.save(task);
    }

    // 비디오 정보 리스트 생성 및 추가
    private List<VideoInfo> buildVideoInfoList(ProcessingTask task) {
        String fileName = task.getFileName();
        String beforeUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.BEFORE);
        String afterUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.AFTER);

        return Arrays.asList(
                new VideoInfo(beforeUrl, task.getTotalFrames(), task.getFps()),
                new VideoInfo(afterUrl, task.getTotalFrames(), task.getFps())
        );
    }

    // 비디오의 총 프레임 수와 FPS를 반환하는 메소드 (추후 분리)
    public ProcessingTask extractVideoFrameInfo(File file) throws IOException {
        // ffprobe 실행 경로를 가져옵니다.
        String ffprobePath = ffmpegConfig.getFfmpegPath() + "\\ffprobe.exe";

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
                    float duration = Float.parseFloat(values[1]);
                    // 프레임 속도 정보를 분리합니다.
                    String[] frameRateParts = values[0].split("/");

                    if (frameRateParts.length == 2) {
                        try {
                            // 분자와 분모를 파싱하고 계산합니다.
                            float numerator = Float.parseFloat(frameRateParts[0]);
                            float denominator = Float.parseFloat(frameRateParts[1]);
                            float fps = numerator / denominator; // 프레임 속도 계산

                            // 총 프레임 수를 계산합니다.
                            int totalFrames = (int) (fps * duration);

                            // 결과를 반환합니다.
                            return new ProcessingTask(totalFrames, fps);
                        } catch (NumberFormatException e) {
                            log.error("Error parsing frame rate: {}, error: {}", values[0], e.getMessage());
                        }
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

    // 썸네일 추출 (추후 분리)
    public void captureFrameFromVideo(File videoFile, String fileName) {
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

    // 함수 이름으로 함수 조회
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





/*

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

    // 프레임 추출 리턴 객체 반환
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

            // 프레임 추출
            ProcessingTask result = extractVideoFrameInfo(file);
            processingTask.setTotalFrames(result.getTotalFrames());
            processingTask.setFps(result.getFps());

            // 4. DB 메타 데이터 저장
            Function function = processFunctionName(processingTask);
            processingTask.setFunction(function);
            upscaleRepository.save(processingTask);

            // 파일 URL 구성
            String beforeUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.BEFORE);
            String afterUrl = fileStorageService.getFileUrl(fileName, FileSuffixType.AFTER);

            videoInfoList.add(new VideoInfo(beforeUrl, result.getTotalFrames(), result.getFps()));
            videoInfoList.add(new VideoInfo(afterUrl, result.getTotalFrames(), result.getFps()));


        } catch (IOException e) {
            throw new FileTransferException("Failed to read multipart file: " + e.getMessage(), e);
        }


        // 파일 URL을 포함한 객체 반환
        return videoInfoList;
    }
    */

}
