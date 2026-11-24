package com.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elderly.entity.HealthData;
import com.elderly.mapper.HealthDataMapper;
import com.elderly.service.HealthAnalysisService;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HealthAnalysisServiceImpl implements HealthAnalysisService {

    @Autowired
    private HealthDataMapper healthDataMapper;

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    @Override
    public Map<String, Object> analyzeHealthData(Long userId) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<HealthData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthData::getUserId, userId)
               .orderByDesc(HealthData::getMeasureTime)
               .last("LIMIT 30");
        List<HealthData> healthDataList = healthDataMapper.selectList(wrapper);

        if (healthDataList == null || healthDataList.isEmpty()) {
            result.put("success", false);
            result.put("message", "暂无健康数据，请先添加健康记录");
            return result;
        }

        String healthInfo = formatHealthData(healthDataList);

        String prompt = buildPrompt(healthInfo);

        String aiResponse = callDeepSeek(prompt);

        result.put("success", true);
        result.put("healthData", healthDataList);
        result.put("analysis", aiResponse);
        result.put("dataCount", healthDataList.size());

        return result;
    }

    private String formatHealthData(List<HealthData> dataList) {
        StringBuilder sb = new StringBuilder();
        sb.append("最近").append(dataList.size()).append("条健康数据：\n\n");

        for (HealthData data : dataList) {
            sb.append("时间：").append(data.getMeasureTime()).append("\n");
            if (data.getBloodPressureHigh() != null) {
                sb.append("血压：").append(data.getBloodPressureHigh());
                if (data.getBloodPressureLow() != null) {
                    sb.append("/").append(data.getBloodPressureLow());
                }
                sb.append(" mmHg\n");
            }
            if (data.getHeartRate() != null) {
                sb.append("心率：").append(data.getHeartRate()).append(" 次/分\n");
            }
            if (data.getBloodSugar() != null) {
                sb.append("血糖：").append(data.getBloodSugar()).append(" mmol/L\n");
            }
            if (data.getTemperature() != null) {
                sb.append("体温：").append(data.getTemperature()).append(" ℃\n");
            }
            if (data.getWeight() != null) {
                sb.append("体重：").append(data.getWeight()).append(" kg\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String buildPrompt(String healthInfo) {
        return "你是一位专业的健康管理医生。请分析以下用户的健康数据，并给出：\n" +
               "1. 总体健康状况评估\n" +
               "2. 异常指标提醒（如有）\n" +
               "3. 生活建议\n" +
               "4. 注意事项\n\n" +
               "请用简洁易懂的语言回复，适合老年人理解。\n\n" +
               "健康数据：\n" + healthInfo;
    }

    private String callDeepSeek(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);

            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + apiKey);

            String response = HttpUtil.createPost(baseUrl + "/chat/completions")
                    .headerMap(headers, false)
                    .body(JSONUtil.toJsonStr(requestBody))
                    .timeout(60000)
                    .execute()
                    .body();

            log.info("DeepSeek API 返回: {}", response);

            JSONObject jsonResponse = JSONUtil.parseObj(response);

            if (jsonResponse.containsKey("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                return "AI分析失败：" + error.getStr("message");
            }

            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject msg = choices.getJSONObject(0).getJSONObject("message");
                return msg.getStr("content");
            }

            return "AI分析结果为空";

        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return "调用AI服务失败：" + e.getMessage();
        }
    }
}
