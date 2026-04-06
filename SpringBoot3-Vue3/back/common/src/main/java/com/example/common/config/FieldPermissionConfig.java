package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FieldPermissionConfig {

    @Bean
    public com.example.common.aspect.FieldPermissionAspect fieldPermissionAspect() {
        return new com.example.common.aspect.FieldPermissionAspect();
    }
}