package com.laoxin.LXBot.module.ql.bean;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class QLCron {
    private int code;
    private List<CronData> corns;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonGetter("data")
    public List<CronData> getCorns() {
        return corns;
    }

    @JsonSetter("data")
    public void setCorns(List<CronData> corns) {
        this.corns = corns;
    }

    @Override
    public String toString() {
        return "QLCron{" +
                "code='" + code + '\'' +
                ", corns=" + corns +
                '}';
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CronData {
        private String name;
        private String command;
        private String schedule;
        private boolean saved;
        private String id;
        private int status;
        private int isDisabled;
        private String logPath;
        private long created;



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public boolean getSaved() {
            return saved;
        }

        public void setSaved(boolean saved) {
            this.saved = saved;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }

        @JsonGetter("_id")
        public String getId() {
            return id;
        }

        @JsonSetter("_id")
        public void setId(String id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIsDisabled() {
            return isDisabled;
        }

        public void setIsDisabled(int isDisabled) {
            this.isDisabled = isDisabled;
        }

        @JsonGetter("log_path")
        public String getLogPath() {
            return logPath;
        }

        @JsonSetter("log_path")
        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }

        @Override
        public String toString() {
            return "CronData{" +
                    "name='" + name + '\'' +
                    ", command='" + command + '\'' +
                    ", schedule='" + schedule + '\'' +
                    ", saved=" + saved +
                    ", id='" + id + '\'' +
                    ", status='" + status + '\'' +
                    ", isDisabled=" + isDisabled +
                    ", logPath='" + logPath + '\'' +
                    '}';
        }
    }
}
