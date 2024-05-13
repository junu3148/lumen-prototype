package com.lumenprototype.function.comm;

import com.lumenprototype.function.upscale.en.FunctionName;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<ProcessingTask, Long> {

    /**
     * 사용자 ID와 함수 이름을 기준으로 모든 처리 작업(ProcessingTask)을 조회합니다.
     * 이 쿼리는 ProcessingTask와 연결된 Function 엔티티를 즉시 로딩하기 위해 JOIN FETCH를 사용합니다.
     *
     * @param userId 사용자 ID
     * @param functionName 조회할 작업의 함수 이름
     * @return 해당 조건에 맞는 ProcessingTask 리스트를 반환합니다.
     */
    @Query("SELECT pt FROM ProcessingTask pt JOIN FETCH pt.function f WHERE pt.userId = :userId AND f.functionName = :functionName")
    List<ProcessingTask> findAllByUserIdAndFunctionName(@Param("userId") Integer userId, @Param("functionName") FunctionName functionName);

    /**
     * 파일 이름을 기준으로 단일 ProcessingTask를 조회합니다.
     * 이 쿼리는 ProcessingTask와 연결된 Function 엔티티를 즉시 로딩하기 위해 JOIN FETCH를 사용합니다.
     *
     * @param fileName 조회할 파일 이름
     * @return 해당 파일 이름에 맞는 ProcessingTask를 반환합니다.
     */
    @Query("SELECT pt FROM ProcessingTask pt JOIN FETCH pt.function f WHERE pt.fileName = :fileName")
    ProcessingTask findByFileName(String fileName);


}
