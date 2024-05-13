package com.lumenprototype.function.upscale;

import com.lumenprototype.function.upscale.en.FunctionName;
import com.lumenprototype.function.upscale.entity.Function;
import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpscaleRepository extends JpaRepository<ProcessingTask,Long> {

    /**
     * 함수 이름을 기준으로 Function 엔티티를 조회합니다.
     * 쿼리는 해당 함수 이름에 해당하는 Function 엔티티가 존재할 경우 Optional 객체로 반환합니다.
     *
     * @param functionName 조회할 함수 이름
     * @return 해당 함수 이름에 해당하는 Function 엔티티를 Optional 객체로 포함하여 반환합니다.
     */
    @Query("SELECT f FROM Function f WHERE f.functionName = :functionName")
    Optional<Function> findByFunctionName(@Param("functionName") FunctionName functionName);


}
