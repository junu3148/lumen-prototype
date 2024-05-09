package com.lumenprototype.function.upscale;

import com.lumenprototype.function.upscale.entity.Function;
import com.lumenprototype.function.upscale.entity.FunctionName;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UpscaleRepository extends JpaRepository<ProcessingTask,Long> {

    // 히스토리 조회
    @Query("SELECT pt FROM ProcessingTask pt JOIN FETCH pt.function f WHERE pt.userId = :userId AND f.functionName = :functionName")
    List<ProcessingTask> findAllByUserIdAndFunctionName(@Param("userId") Integer userId, @Param("functionName") FunctionName functionName);

    @Query("SELECT f FROM Function f WHERE f.functionName = :functionName")
    Optional<Function> findByFunctionName(@Param("functionName") FunctionName functionName);

    @Query("SELECT pt FROM ProcessingTask pt JOIN FETCH pt.function f WHERE pt.taskId = :taskId")
    ProcessingTask findByTaskId(Long taskId);

    @Query("SELECT pt FROM ProcessingTask pt JOIN FETCH pt.function f WHERE pt.fileName = :fileName")
    ProcessingTask findByFileName(String fileName);



}
