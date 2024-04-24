package com.lumenprototype.function.upscale;

import com.lumenprototype.function.upscale.entity.ProcessingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpscaleRepository extends JpaRepository<ProcessingTask,Long> {

   List<ProcessingTask> findAllByUserId(Integer userId);

   @Query("SELECT pt FROM ProcessingTask pt JOIN FETCH pt.function f WHERE pt.taskId = :taskId")
   ProcessingTask findByTaskId(Long taskId);

}
