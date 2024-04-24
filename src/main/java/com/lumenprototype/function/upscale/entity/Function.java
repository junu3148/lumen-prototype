package com.lumenprototype.function.upscale.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "functions")
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer functionId;

    @Column(nullable = false)
    private String functionName;


}