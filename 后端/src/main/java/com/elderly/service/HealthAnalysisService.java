package com.elderly.service;

import java.util.Map;

public interface HealthAnalysisService {

    Map<String, Object> analyzeHealthData(Long userId);
}
