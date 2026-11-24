package com.elderly.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Pattern;

public class XssRequestWrapper extends HttpServletRequestWrapper {
    
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern IMG_ONERROR_PATTERN = Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVAL_PATTERN = Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE);
    
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return cleanXss(value);
    }
    
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        String[] cleanedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanedValues[i] = cleanXss(values[i]);
        }
        return cleanedValues;
    }
    
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXss(value);
    }
    
    private String cleanXss(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        
        String cleaned = value;
        
        cleaned = SCRIPT_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = IMG_ONERROR_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = JAVASCRIPT_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = EVAL_PATTERN.matcher(cleaned).replaceAll("");
        
        cleaned = cleaned.replaceAll("<", "&lt;");
        cleaned = cleaned.replaceAll(">", "&gt;");
        cleaned = cleaned.replaceAll("\\(", "&#40;");
        cleaned = cleaned.replaceAll("\\)", "&#41;");
        cleaned = cleaned.replaceAll("\"", "&quot;");
        cleaned = cleaned.replaceAll("'", "&#x27;");
        cleaned = cleaned.replaceAll("/", "&#x2F;");
        
        return cleaned;
    }
}
