package com.laoxin.LXBot.utils;

import java.util.HashMap;
import java.util.Map;

public class HttpOpts {

    private String method;
    private String requestURL;
    private Map<String, String> requestHeader;
    private Map<String, Object> requestParams;
    private String requestData;

    /**
     * opts构造方法
     *
     * @param method        请求方法
     * @param requestHeader 请求头
     * @param requestParams 请求体
     */
    public HttpOpts(String method, String requestURL, Map<String, String> requestHeader, Map<String, Object> requestParams) {
        method = method.toUpperCase();
        this.requestURL = requestURL;
        this.method = method;
        this.requestHeader = requestHeader;
        this.requestParams = requestParams;
    }

    public HttpOpts(String method, String requestURL, Map<String, String> requestHeader, String requestData) {
        method = method.toUpperCase();
        this.requestURL = requestURL;
        this.method = method;
        this.requestHeader = requestHeader;
        this.requestData = requestData;
    }

    /**
     * opts构造方法
     *
     * @param method
     */
    public HttpOpts(String method, String requestURL) {
        method = method.toUpperCase();
        this.requestURL = requestURL;
        this.method = method;
        this.requestHeader = new HashMap<String, String>();
        this.requestParams = new HashMap<String, Object>();
    }


    public void setMethod(String method) {
        this.method = method;
    }

    public void setHeader(String key, String value) {
        requestHeader.put(key, value);
    }

    public void setParams(String key, Object value) {
        requestParams.put(key, value);
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getRequestHeader() {
        if (requestHeader.isEmpty()) {
            return null;
        }
        return requestHeader;
    }

    public Map<String, Object> getRequestParams() {
        if (this.requestParams.isEmpty()) {
            return null;
        }
        return requestParams;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
}

