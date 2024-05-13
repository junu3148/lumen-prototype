package com.lumenprototype.function.comm;

import com.lumenprototype.file.dto.FileInfo;
import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.comm.dto.HistoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    // 히스토리 리스트 조회
    @PostMapping("history-list")
    public ResponseEntity<List<FileInfo>> getHistoryList(@RequestBody HistoryRequest historyRequest) {
        return historyService.findAllHistory(historyRequest);
    }

    // 히스토리 조회
    @PostMapping("history")
    public List<VideoInfo> getHistory(@RequestBody HistoryRequest historyRequest) {
        return historyService.findHistory(historyRequest);
    }


}
