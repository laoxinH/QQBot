package com.laoxin.LXBot.module.ql.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laoxin.LXBot.bean.HttpRespBean;
import com.laoxin.LXBot.module.ql.bean.QLCron;
import com.laoxin.LXBot.module.ql.bean.QLEnvs;
import com.laoxin.LXBot.utils.HttpOpts;
import com.laoxin.LXBot.utils.HttpUtils;
import com.laoxin.LXBot.utils.ReplyConfigUtils;
import com.sun.istack.internal.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QLUtils {
    private QLUtils() {
    }

    private static String username;
    private static String password;
    private static String host;
    private static String port;
    private static QLUtils ql;
    private static Yaml yaml = new Yaml();
    private static String token;
    private static long time;
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        String realPath = yaml.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        realPath = new File(realPath).getParent() + "\\QLConfig.yaml";

        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
            ql = yaml.loadAs(new InputStreamReader(new FileInputStream(realPath),"utf-8"), QLUtils.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static QLUtils getQl() {
        time = System.currentTimeMillis();
        login();
        return ql;
    }


    /**
     * 获取环境变量
     * @param keyword 关键词
     * @return
     */
    public QLEnvs getEnvs(String keyword) {
        HttpOpts opts = getUrlApi("get", "/api/envs", "searchValue", keyword);
        HttpRespBean respBean = HttpUtils.httpConn(opts);

        try {
            QLEnvs qlEnvs = mapper.readValue(respBean.getData(), QLEnvs.class);
            if (qlEnvs.getEnvs().isEmpty()) {
                return null;
            } else {
                return qlEnvs;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取所有环境变量
     * @return
     */
    public QLEnvs getEnvs() {
        return getEnvs("");
    }
    /**
     * 添加环境变量
     * @param value   变量值
     * @param name    名称
     * @param remarks 备注
     * @return
     */
    public boolean addEnvs(String name,String value,String remarks) {
        List<Map<String, String>> envs = new ArrayList<>();
        HashMap<String, String> env = new HashMap<>();
        env.put("name", name);
        env.put("value", value);
        env.put("remarks", remarks);
        envs.add(env);
        HttpOpts opts = getUrlApi("post", "/api/envs", "", "");
        String envsStr = null;
        try {
            envsStr = mapper.writeValueAsString(envs);
            opts.setRequestData(envsStr);
            HttpRespBean respBean = HttpUtils.httpConn(opts);
            JsonNode jsonNode = mapper.readTree(respBean.getData());
            if (jsonNode.get("code").asInt() == 200) {
                return true;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 修改环境变量
     * @param env
     * @return
     */
    public boolean alterEnvs(QLEnvs.EnvsData env){
        if (env == null) throw new NullPointerException("env对象不能为null");
        try {
            String envStr = mapper.writeValueAsString(env);
            HttpOpts opts = getUrlApi("put", "/api/envs", "", "");
            //System.out.println(envStr);
            opts.setRequestData(envStr);
            HttpRespBean respBean = HttpUtils.httpConn(opts);
            //System.out.println(respBean.getData());
            JsonNode jsonNode = mapper.readTree(respBean.getData());
            if (jsonNode.get("code").asInt() == 200) return true;
            return false;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除环境变量
     * @param envIds
     * @return
     */
    public boolean delEnvs(List<String> envIds) {
        return delete("/api/envs",envIds);
    }
    /**
     * 启用环境变量
     * @param envIds 关键词
     * @return
     */
    public boolean enableEnvs(List<String> envIds){
        return able("/api/envs/enable",envIds);
    }

    /**
     * 禁用环境变量
     * @param envIds 关键词
     * @return
     */
    public boolean disableEnvs(List<String> envIds){
        return able("/api/envs/disable",envIds);
    }

    /**
     * 添加定时任务
     * @param command
     * @param name
     * @param schedule
     * @return
     */
    public QLCron.CronData addCron(String command,String name ,String schedule){
        HttpOpts opts = getUrlApi("POST", "/api/crons", "", "");
        opts.setParams("command",command);
        opts.setParams("name",name);
        opts.setParams("schedule",schedule);
        HttpRespBean respBean = HttpUtils.httpConn(opts);
        //System.out.println("添加定时任务失败: " + respBean.getData());

        try {
            String data = mapper.readTree(respBean.getData()).get("data").toPrettyString();
            //System.out.println(data);
            QLCron.CronData cron = mapper.readValue(data, QLCron.CronData.class);
            return cron;
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("添加定时任务失败: " + respBean.getData());
        }
        return null;
    }

    /**
     * 获取定时任务
     * @param keyword 关键词
     * @return
     */
    public QLCron getCron(String keyword) {
        HttpOpts opts = getUrlApi("get", "/api/crons", "searchValue", keyword);
        HttpRespBean respBean = HttpUtils.httpConn(opts);
        //System.out.println(respBean.getData());
        try {
            QLCron qlCron = mapper.readValue(respBean.getData(), QLCron.class);
            if (qlCron.getCorns().isEmpty()) {
                return null;
            } else {
                return qlCron;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取所有定时任务
     * @return
     */
    public QLCron getCron() {
        return getCron("");
    }

    /**
     * 获取正在执行的任务列表
     * @return
     */
    public List<QLCron.CronData> getRunCron() {
        ArrayList<QLCron.CronData> runCron = new ArrayList<>();
        QLCron cron = getCron();
        for (QLCron.CronData corn : cron.getCorns()) {
            if (corn.getStatus() == 0) {
                runCron.add(corn);
            }
        }
        return runCron.isEmpty() ? null : runCron;
    }
    /**
     * 删除定时任务
     * @param cronIds 关键词
     * @return
     */
    public boolean delCron(List<String> cronIds){
        return delete("/api/crons",cronIds);
    }

    /**
     * 禁用定时任务
     * @param cronIds
     * @return
     */
    public boolean disableCrons(List<String> cronIds){
        return able("/api/crons/disable",cronIds);
    }

    /**
     * 启用定时任务
     * @param cronIds
     * @return
     */
    public boolean enableCrons(List<String> cronIds){
        return able("/api/crons/enable",cronIds);
    }


    /**
     * 执行定时任务
     * @param cronIds
     * @return
     */
    public boolean runTask(List<String> cronIds){
        return taskControl("/api/crons/run",cronIds);
    }

    public boolean stopTask(List<String> cronIds){
        return taskControl("/api/crons/stop",cronIds);
    }
    /**
     * 定时任务控制
     * @param api
     * @param cronIds
     * @return
     */
    private boolean taskControl(String api,List<String> cronIds){
        HttpOpts opts = getUrlApi("put", api, "", "");

        try {
            String taskStr = mapper.writeValueAsString(cronIds);
            opts.setRequestData(taskStr);
            HttpRespBean respBean = HttpUtils.httpConn(opts);
            JsonNode jsonNode = mapper.readTree(respBean.getData());
            if (jsonNode.get("code").asInt() == 200) return true;
            return false;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除api接口
     * @param api
     * @param cronOrEnvIds
     * @return
     */
    private boolean delete(String api,List<String> cronOrEnvIds){
        HttpOpts opts = getUrlApi("DELETE", api, "", "");
        try {
            String delStr = mapper.writeValueAsString(cronOrEnvIds);
            opts.setRequestData(delStr);
            HttpRespBean respBean = HttpUtils.httpConn(opts);
            JsonNode jsonNode = mapper.readTree(respBean.getData());
            if (jsonNode.get("code").asInt() == 200) return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 启用禁用接口
     * @param api
     * @param cronOrEnvIds
     * @return
     */
    private boolean able(String api, List<String> cronOrEnvIds) {
        HttpOpts opts = getUrlApi("put", api, "", "");
        try {
            String ableStr = mapper.writeValueAsString(cronOrEnvIds);
            opts.setRequestData(ableStr);
            HttpRespBean respBean = HttpUtils.httpConn(opts);
            JsonNode jsonNode = mapper.readTree(respBean.getData());
            if (jsonNode.get("code").asInt() == 200) return true;
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 青龙登录获取token
     */
    private static void login() {
        time = System.currentTimeMillis();
        HttpOpts opts = new HttpOpts("post", "http://" + host + ":" + port + "/api/login?t=" + time);

        // 设置Content-Type,否则失败
        opts.setHeader("Content-Type", "application/json");
        opts.setParams("username", username);
        opts.setParams("password", password);
        HttpRespBean respBean = HttpUtils.httpConn(opts);
        JsonNode node = null;
        //System.out.println(respBean.getData());
        try {
            node = mapper.readTree(respBean.getData());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        token = node.get("data").get("token").asText();
    }

    /**
     * 生成青龙api请求参数
     * @param method
     * @param api
     * @param prams
     * @param query
     * @return
     */
    private HttpOpts getUrlApi(String method, String api, String prams, @NotNull String query) {
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://" + host + ":" + port + api + "?t=" + time;
        if (prams != null && !prams.isEmpty()) {
            url += "&" + prams + "=" + query;
        }
        HttpOpts opts = new HttpOpts(method, url);
        opts.setHeader("Authorization", "Bearer " + token);
        opts.setHeader("Content-Type", "application/json");
        return opts;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "QLUtils{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
