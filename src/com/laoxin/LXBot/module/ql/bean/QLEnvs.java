package com.laoxin.LXBot.module.ql.bean;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QLEnvs {
    private int code;
    private List<EnvsData> envs;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonSetter("data")
    public List<EnvsData> getEnvs() {
        return envs;
    }

    @JsonSetter("data")
    public void setEnvs(List<EnvsData> envs) {
        this.envs = envs;
    }

    @Override
    public String toString() {
        return "QLEnvs{" +
                "code=" + code +
                ", envs=" + envs +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class EnvsData {
        private String value;
        private String id;
        @JsonIgnore
        private String created;
        @JsonIgnore
        private int status;
        private String name;
        private String remarks;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @JsonGetter("_id")
        public String getId() {
            return id;
        }

        @JsonSetter("_id")
        public void setId(String id) {
            this.id = id;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        @Override
        public String toString() {
            return "EnvsData{" +
                    "value='" + value + '\'' +
                    ", id='" + id + '\'' +
                    ", created='" + created + '\'' +
                    ", status=" + status +
                    ", name='" + name + '\'' +
                    ", remarks='" + remarks + '\'' +
                    '}';
        }
    }
}
