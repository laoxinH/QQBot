package com.laoxin.LXBot.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ReplyConfigUtils {
    private String version;
    private List<Reply> replyList;
    private static ReplyConfigUtils config;
    private static Yaml yaml = new Yaml();


    static {
        String realPath = yaml.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        realPath = new File(realPath).getParent() + "\\ReplyConfig.yaml";
        try {
            config = yaml.loadAs(new FileReader(realPath),ReplyConfigUtils.class);
            //config =  yaml.loadAs(new FileInputStream("D:\\java study\\project\\QQBot\\src\\ReplyConfig.yaml"),ReplyConfigUtils.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //private ReplyConfigUtils(){}
    public static ReplyConfigUtils getReplyConfig(){
        return config;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }

    @Override
    public String toString() {
        return "ReplyConfigUtils{" +
                "version='" + version + '\'' +
                ", replyList=" + replyList +
                '}';
    }

    public static class Reply{
        private String keyword;
        private int mod;
        private String type;
        private String url;
        private String reply;

        public int getMod() {
            return mod;
        }

        public void setMod(int mod) {
            this.mod = mod;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public String toString() {
            return "Reply{" +
                    "keyword='" + keyword + '\'' +
                    ", mod=" + mod +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", reply='" + reply + '\'' +
                    '}';
        }
    }
}
