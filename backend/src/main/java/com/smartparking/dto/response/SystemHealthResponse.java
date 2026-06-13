package com.smartparking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SystemHealthResponse {
    private String status;
    private String database;
    private String webSocket;
    private String scheduler;
    private Map<String, Object> metrics;
}
