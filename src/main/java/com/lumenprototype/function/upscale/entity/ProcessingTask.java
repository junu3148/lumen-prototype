package com.lumenprototype.function.upscale.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "processing_tasks")
public class ProcessingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", referencedColumnName = "functionId", insertable = false, updatable = false)
    private Function function;

    @Column(nullable = false)
    private String fileName;

    @Column(columnDefinition = "jsonb")
    private String parameters;

    @Column
    private Timestamp date;

    @Column
    private Integer userId;

    @Column
    private String status;

    @Column(columnDefinition = "jsonb")
    private String result;

}