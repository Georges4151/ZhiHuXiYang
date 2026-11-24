package com.elderly.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeatherService {

    @Value("${qweather.api-key}")
    private String apiKey;

    @Value("${qweather.base-url}")
    private String baseUrl;

    private static final String WUHAN_LOCATION = "101200101";

    public Map<String, Object> getNowWeather(String location) {
        String loc = location != null ? location : WUHAN_LOCATION;
        String url = baseUrl + "/weather/now?location=" + loc + "&key=" + apiKey;
        
        try {
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);
            
            Map<String, Object> result = new HashMap<>();
            if ("200".equals(json.getStr("code"))) {
                JSONObject now = json.getJSONObject("now");
                result.put("temp", now.getStr("temp"));
                result.put("icon", getWeatherIcon(now.getStr("icon")));
                result.put("text", now.getStr("text"));
                result.put("windDir", now.getStr("windDir"));
                result.put("windScale", now.getStr("windScale"));
                result.put("humidity", now.getStr("humidity"));
                result.put("feelsLike", now.getStr("feelsLike"));
                result.put("visibility", now.getStr("vis"));
                result.put("pressure", now.getStr("pressure"));
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "获取天气失败");
            }
            return result;
        } catch (Exception e) {
            log.error("获取天气失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "网络错误");
            return result;
        }
    }

    public Map<String, Object> get7dForecast(String location) {
        String loc = location != null ? location : WUHAN_LOCATION;
        String url = baseUrl + "/weather/7d?location=" + loc + "&key=" + apiKey;
        
        try {
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);
            
            Map<String, Object> result = new HashMap<>();
            if ("200".equals(json.getStr("code"))) {
                result.put("daily", json.getJSONArray("daily"));
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "获取预报失败");
            }
            return result;
        } catch (Exception e) {
            log.error("获取天气预报失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "网络错误");
            return result;
        }
    }

    public Map<String, Object> getAirQuality(String location) {
        String loc = location != null ? location : WUHAN_LOCATION;
        String url = baseUrl + "/air/now?location=" + loc + "&key=" + apiKey;
        
        try {
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);
            
            Map<String, Object> result = new HashMap<>();
            if ("200".equals(json.getStr("code"))) {
                JSONObject now = json.getJSONObject("now");
                result.put("aqi", now.getStr("aqi"));
                result.put("category", now.getStr("category"));
                result.put("pm2p5", now.getStr("pm2p5"));
                result.put("pm10", now.getStr("pm10"));
                result.put("o3", now.getStr("o3"));
                result.put("no2", now.getStr("no2"));
                result.put("so2", now.getStr("so2"));
                result.put("co", now.getStr("co"));
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "获取空气质量失败");
            }
            return result;
        } catch (Exception e) {
            log.error("获取空气质量失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "网络错误");
            return result;
        }
    }

    public Map<String, Object> getIndices(String location) {
        String loc = location != null ? location : WUHAN_LOCATION;
        String url = baseUrl + "/indices/1d?type=1,2,3,5,6,8&location=" + loc + "&key=" + apiKey;
        
        try {
            String response = HttpUtil.get(url);
            JSONObject json = JSONUtil.parseObj(response);
            
            Map<String, Object> result = new HashMap<>();
            if ("200".equals(json.getStr("code"))) {
                result.put("daily", json.getJSONArray("daily"));
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "获取生活指数失败");
            }
            return result;
        } catch (Exception e) {
            log.error("获取生活指数失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "网络错误");
            return result;
        }
    }

    private String getWeatherIcon(String code) {
        Map<String, String> iconMap = new HashMap<>();
        iconMap.put("100", "☀️");
        iconMap.put("101", "🌤️");
        iconMap.put("102", "⛅");
        iconMap.put("103", "🌥️");
        iconMap.put("104", "☁️");
        iconMap.put("150", "☀️");
        iconMap.put("151", "🌤️");
        iconMap.put("300", "🌦️");
        iconMap.put("301", "🌧️");
        iconMap.put("302", "⛈️");
        iconMap.put("303", "⛈️");
        iconMap.put("304", "⛈️");
        iconMap.put("305", "🌧️");
        iconMap.put("306", "🌧️");
        iconMap.put("307", "🌧️");
        iconMap.put("308", "🌧️");
        iconMap.put("309", "🌧️");
        iconMap.put("310", "🌧️");
        iconMap.put("311", "🌧️");
        iconMap.put("312", "🌧️");
        iconMap.put("313", "🌧️");
        iconMap.put("314", "🌧️");
        iconMap.put("315", "🌧️");
        iconMap.put("316", "🌧️");
        iconMap.put("317", "🌧️");
        iconMap.put("318", "🌧️");
        iconMap.put("350", "🌧️");
        iconMap.put("351", "🌧️");
        iconMap.put("399", "🌧️");
        iconMap.put("400", "🌨️");
        iconMap.put("401", "🌨️");
        iconMap.put("402", "🌨️");
        iconMap.put("403", "🌨️");
        iconMap.put("404", "🌨️");
        iconMap.put("405", "🌨️");
        iconMap.put("406", "🌨️");
        iconMap.put("407", "🌨️");
        iconMap.put("408", "🌨️");
        iconMap.put("409", "🌨️");
        iconMap.put("410", "🌨️");
        iconMap.put("456", "🌨️");
        iconMap.put("457", "🌨️");
        iconMap.put("499", "🌨️");
        iconMap.put("500", "🌫️");
        iconMap.put("501", "🌫️");
        iconMap.put("502", "🌫️");
        iconMap.put("503", "🌫️");
        iconMap.put("504", "🌫️");
        iconMap.put("507", "🌫️");
        iconMap.put("508", "🌫️");
        iconMap.put("509", "🌫️");
        iconMap.put("510", "🌫️");
        iconMap.put("511", "🌫️");
        iconMap.put("512", "🌫️");
        iconMap.put("513", "🌫️");
        iconMap.put("514", "🌫️");
        iconMap.put("515", "🌫️");
        iconMap.put("800", "☀️");
        iconMap.put("801", "🌤️");
        iconMap.put("802", "⛅");
        iconMap.put("803", "🌥️");
        iconMap.put("804", "☁️");
        iconMap.put("805", "🌫️");
        iconMap.put("806", "🌫️");
        iconMap.put("807", "🌫️");
        iconMap.put("900", "🌡️");
        iconMap.put("901", "🌡️");
        iconMap.put("999", "🌡️");
        
        return iconMap.getOrDefault(code, "🌤️");
    }
}
