package com.laoxin.LXBot.bean;

import java.util.List;
import java.util.Map;

public class HttpRespBean {
    private int responseCode;
    private Map<String, List<String>> responseHeader;
    private String data;
    private byte[] byteData;

    public HttpRespBean(int responseCode, Map<String, List<String>> responseHeader, String data) {
        this.responseCode = responseCode;
        this.responseHeader = responseHeader;
        this.data = data;
    }

    public HttpRespBean() {
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    @Override
    public String toString() {
        return "HttpRespBean{" +
                "responseCode=" + responseCode +
                ", responseHeader=" + responseHeader +
                ", data='" + data + '\'' +
                '}';
    }
}
