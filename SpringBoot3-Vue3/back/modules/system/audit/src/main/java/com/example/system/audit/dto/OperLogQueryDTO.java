package com.example.system.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperLogQueryDTO {

    private String operName;

    private String title;

    private Integer businessType;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer page;

    private Integer size;
}
