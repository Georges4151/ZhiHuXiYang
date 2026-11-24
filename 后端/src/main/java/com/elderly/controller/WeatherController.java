package com.elderly.controller;

import com.elderly.common.Result;
import com.elderly.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/now")
    public Result<Map<String, Object>> getNowWeather(
            @RequestParam(required = false) String location) {
        Map<String, Object> data = weatherService.getNowWeather(location);
        if (Boolean.TRUE.equals(data.get("success"))) {
            return Result.success(data);
        } else {
            return Result.error((String) data.get("message"));
        }
    }

    @GetMapping("/forecast")
    public Result<Map<String, Object>> get7dForecast(
            @RequestParam(required = false) String location) {
        Map<String, Object> data = weatherService.get7dForecast(location);
        if (Boolean.TRUE.equals(data.get("success"))) {
            return Result.success(data);
        } else {
            return Result.error((String) data.get("message"));
        }
    }

    @GetMapping("/air")
    public Result<Map<String, Object>> getAirQuality(
            @RequestParam(required = false) String location) {
        Map<String, Object> data = weatherService.getAirQuality(location);
        if (Boolean.TRUE.equals(data.get("success"))) {
            return Result.success(data);
        } else {
            return Result.error((String) data.get("message"));
        }
    }

    @GetMapping("/indices")
    public Result<Map<String, Object>> getIndices(
            @RequestParam(required = false) String location) {
        Map<String, Object> data = weatherService.getIndices(location);
        if (Boolean.TRUE.equals(data.get("success"))) {
            return Result.success(data);
        } else {
            return Result.error((String) data.get("message"));
        }
    }

    @GetMapping("/all")
    public Result<Map<String, Object>> getAllWeather(
            @RequestParam(required = false) String location) {
        Map<String, Object> now = weatherService.getNowWeather(location);
        Map<String, Object> forecast = weatherService.get7dForecast(location);
        Map<String, Object> air = weatherService.getAirQuality(location);
        Map<String, Object> indices = weatherService.getIndices(location);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("now", now);
        result.put("forecast", forecast.get("daily"));
        result.put("air", air);
        result.put("indices", indices.get("daily"));
        return Result.success(result);
    }
}
