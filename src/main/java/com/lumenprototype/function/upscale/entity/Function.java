package com.lumenprototype.function.upscale.entity;

import com.lumenprototype.function.upscale.en.FunctionName;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "function")
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer functionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FunctionName functionName;
}
