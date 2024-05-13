package com.lumenprototype.function.comm;

import com.lumenprototype.file.dto.FileInfo;
import com.lumenprototype.file.dto.VideoInfo;
import com.lumenprototype.function.comm.dto.HistoryRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface HistoryService {


    /**
     * 요청된 히스토리 정보에 기반하여 모든 파일 정보를 검색합니다.
     *
     * @param historyRequest 히스토리 정보를 요청하는 객체입니다.
     *                       검색에 필요한 필터링 조건을 포함합니다.
     * @return 검색된 파일 정보 리스트를 담은 ResponseEntity를 반환합니다.
     */
    ResponseEntity<List<FileInfo>> findAllHistory(HistoryRequest historyRequest);


    /**
     * 업스케일링 처리 이력을 조회하고 해당 비디오의 정보 리스트를 반환합니다.
     * 이 메소드는 주어진 파일 이름에 해당하는 업스케일링 작업의 기록을 데이터베이스에서 검색합니다.
     * 검색된 작업 정보를 바탕으로 저장된 비디오 파일의 URL과 프레임 정보를 리스트로 구성하여 반환합니다.
     *
     * @param historyRequest 검색할 업스케일링 작업의 파일 이름을 포함한 요청 객체입니다.
     * @return 해당 업스케일링 작업 정보를 바탕으로 구성된 비디오 정보 리스트를 반환합니다.
     *         작업이 존재하지 않는 경우, 빈 리스트를 반환합니다.
     */
    List<VideoInfo> findHistory(HistoryRequest historyRequest);

}
