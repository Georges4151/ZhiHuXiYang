package com.elderly.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elderly.entity.ConsultationRecord;
import com.elderly.mapper.ConsultationRecordMapper;
import com.elderly.service.ConsultationRecordService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ConsultationRecordServiceImpl extends ServiceImpl<ConsultationRecordMapper, ConsultationRecord> implements ConsultationRecordService {
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    @Value("${deepseek.base-url}")
    private String baseUrl;
    
    @Value("${deepseek.model}")
    private String model;
    
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    
    @Override
    public List<ConsultationRecord> getHistoryByUserId(Long userId, Integer limit) {
        return this.list(new LambdaQueryWrapper<ConsultationRecord>()
                .eq(ConsultationRecord::getUserId, userId)
                .orderByDesc(ConsultationRecord::getCreateTime)
                .last("LIMIT " + limit));
    }
    
    @Override
    public Page<ConsultationRecord> getPageByUserId(Long userId, Integer pageNum, Integer pageSize) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<ConsultationRecord>()
                .eq(ConsultationRecord::getUserId, userId)
                .orderByDesc(ConsultationRecord::getCreateTime));
    }
    
    @Override
    public String chatWithDeepSeek(Long userId, String question) {
        String answer = callDeepSeekApi(question);
        
        ConsultationRecord record = new ConsultationRecord();
        record.setUserId(userId);
        record.setQuestion(question);
        record.setAnswer(answer);
        record.setConsultationType(1);
        this.save(record);
        
        return answer;
    }
    
    private String callDeepSeekApi(String question) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("stream", false);
            
            JSONArray messages = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的养老服务智能助手，专门为老年人提供养老政策咨询、健康知识、生活服务等方面的帮助。请用简洁、易懂的语言回答问题，语气要亲切友善。");
            messages.add(systemMessage);
            
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            Request request = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toJSONString(), MediaType.parse("application/json")))
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = JSON.parseObject(responseBody);
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        return choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                    }
                }
                log.error("DeepSeek API调用失败: {}", response.code());
                return "抱歉，智能客服暂时无法回答，请稍后再试。";
            }
        } catch (IOException e) {
            log.error("DeepSeek API调用异常", e);
            return "抱歉，智能客服暂时无法回答，请稍后再试。";
        }
    }
}
