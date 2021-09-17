package com.laoxin.LXBot.utils;


import com.laoxin.LXBot.test.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;

public class BotConfigUtils {
    private String version;
    private int port;
    private int maxConcurrent;
    private String indexFileName;
    private String staticPath;
    private Cqgo cqgo;
    private List<Long> supper;
    private List<Long> groupId;
    private User user;
    private List<Funcmode> funcmodeList;
    private List<Funcmode> suppermodeList;
    private static Yaml yaml = new Yaml();
    private static BotConfigUtils config;
    static {

        String realPath = yaml.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        realPath = new File(realPath).getParent() + "\\BotConfig.yaml";
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
            config = yaml.loadAs(new FileReader(realPath), BotConfigUtils.class);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private BotConfigUtils(){}

   /* public static CQConfigUtils getConfig(){
        return config;
    }*/

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Cqgo getCqgo() {
        return cqgo;
    }

    public void setCqgo(Cqgo cqgo) {
        this.cqgo = cqgo;
    }

    public List<Long> getSupper() {
        return supper;
    }

    public void setSupper(List<Long> supper) {
        this.supper = supper;
    }

    public List<Long> getGroupId() {
        return groupId;
    }

    public void setGroupId(List<Long> groupId) {
        this.groupId = groupId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Funcmode> getFuncmodeList() {
        return funcmodeList;
    }

    public void setFuncmodeList(List<Funcmode> funcmodeList) {
        this.funcmodeList = funcmodeList;
    }

    public String getIndexFileName() {
        return indexFileName;
    }

    public void setIndexFileName(String indexFileName) {
        this.indexFileName = indexFileName;
    }

    public String getStaticPath() {
        if (staticPath.startsWith("/")) {
            return staticPath;
        }
        String realPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        realPath = new File(realPath).getParent();
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return realPath + "\\"+ staticPath;
    }

    public void setStaticPath(String staticPath) {
        this.staticPath = staticPath;
    }

    public static BotConfigUtils getBotConfig() {

        return config;
    }

    public List<Funcmode> getSuppermodeList() {
        return suppermodeList;
    }

    public void setSuppermodeList(List<Funcmode> suppermodeList) {
        this.suppermodeList = suppermodeList;
    }

    @Override
    public String toString() {
        return "CQConfigUtils{" +
                "version='" + version + '\'' +
                ", cqgo=" + cqgo +
                ", supper=" + supper +
                ", groupId=" + groupId +
                ", userId=" + user +
                ", funcmodeList=" + funcmodeList +
                '}';
    }


    public static class Cqgo {
        private String host;
        private String port;
        private String token;
        private Long botQQ;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getBotQQ() {
            return botQQ;
        }

        public void setBotQQ(Long botQQ) {
            this.botQQ = botQQ;
        }

        @Override
        public String toString() {
            return "Cqgo{" +
                    "host='" + host + '\'' +
                    ", port='" + port + '\'' +
                    ", token='" + token + '\'' +
                    ", botQQ=" + botQQ +
                    '}';
        }
    }

    public static class Funcmode {
        private String name;
        private String describe;
        private String help;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getHelp() {
            return help;
        }

        public void setHelp(String help) {
            this.help = help;
        }

        @Override
        public String toString() {
            return "Funcmode{" +
                    "name='" + name + '\'' +
                    ", describe='" + describe + '\'' +
                    ", help='" + help + '\'' +
                    '}';
        }
    }

    public static class User {
        private String listmod;
        private List<Long> userId;

        public String getListmod() {
            return listmod;
        }

        public void setListmod(String listmod) {
            this.listmod = listmod;
        }

        public List<Long> getUserId() {
            return userId;
        }

        public void setUserId(List<Long> userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "UserId{" +
                    "listmod='" + listmod + '\'' +
                    ", list=" + userId +
                    '}';
        }
    }
}
