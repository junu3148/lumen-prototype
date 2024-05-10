package com.lumenprototype.function.upscale.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processing_tasks")
public class ProcessingTask {

    public ProcessingTask(Integer totalFrames, float fps) {
        this.totalFrames = totalFrames;
        this.fps = fps;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", referencedColumnName = "functionId")
    private Function function;

    @Transient
    @Column(nullable = false)
    private String functionName;

    @Column(nullable = false)
    private String origName;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String parameters;

    @Column
    private Timestamp date;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer totalFrames;

    @Column
    private float fps;

    @Column
    private String format;

    @Transient
    private String modelName;

    @PrePersist
    protected void onCreate() {
        date = new Timestamp(System.currentTimeMillis());
    }

}