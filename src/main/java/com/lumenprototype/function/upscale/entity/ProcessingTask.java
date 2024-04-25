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

    @Column(nullable = false)
    private Timestamp date;

    @Column
    private Integer userId;

    @Column
    private String status;

    @Column(columnDefinition = "jsonb")
    private String result;

    @PrePersist
    protected void onCreate() {
        date = new Timestamp(System.currentTimeMillis());
    }
}