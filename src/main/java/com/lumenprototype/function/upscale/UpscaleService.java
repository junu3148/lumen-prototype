package com.lumenprototype.function.upscale;


import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UpscaleService {

    ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest);

    void upscale(MultipartFile file, ProcessingTask processingTask);
}

