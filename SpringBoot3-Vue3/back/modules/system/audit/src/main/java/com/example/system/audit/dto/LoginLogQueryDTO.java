package com.example.system.audit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginLogQueryDTO {

    private String username;

    private Integer status;

    private String ipaddr;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer page;

    private Integer size;
}
