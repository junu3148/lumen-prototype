package com.lumenprototype.function.upscale;

import com.lumenprototype.comm.FileStorageProperties;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import lombok.RequiredArgsConstructor;
import com.lumenprototype.comm.FileInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService{

    private final UpscaleRepository upscaleRepository;
    private final FileStorageProperties fileStorageProperties;

    public ResponseEntity<?> findAllHistory(Integer userId) {
        try {
            List<ProcessingTask> histories = upscaleRepository.findAllByUserId(userId);
            if (histories.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<FileInfo> fileInfos = histories.stream()
                    .map(task -> {
                        String fullPath = fileStorageProperties.getUploadDir() + task.getFileName() + ".jpg";
                        return new FileInfo(task.getFileName(), fullPath);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileInfos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving data: " + e.getMessage());
        }
    }
}
