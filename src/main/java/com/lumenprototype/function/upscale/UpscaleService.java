package com.lumenprototype.function.upscale;


import com.lumenprototype.comm.FileInfo;
import com.lumenprototype.function.upscale.comm.HistoryRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UpscaleService {
     ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest);
}

