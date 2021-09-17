package com.laoxin.LXBot.module.ql;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.module.ModuleInterface;
import com.laoxin.LXBot.module.ql.bean.QLCron;
import com.laoxin.LXBot.module.ql.bean.QLEnvs;
import com.laoxin.LXBot.module.ql.utils.QLUtils;
import com.laoxin.LXBot.utils.BotConfigUtils;
import com.sun.media.jfxmediaimpl.HostUtils;

import java.util.*;

public class QL implements ModuleInterface {
    private Map<String, String> cookie;
    private String cookieStr;
    private QLUtils ql;
    private static Map<String, List<QLCron.CronData>> cronMap = new Hashtable<>();
    private static BotConfigUtils config = BotConfigUtils.getBotConfig();
    private boolean isAdmin;

    @Override
    public boolean whetherRun(CqGoBean cqGoBean) {

        for (Long aLong : config.getSupper()) {
            if (aLong.equals(cqGoBean.getSender().getUserId())){
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) return false;
        if (cqGoBean.getMessage().trim().toLowerCase().startsWith("ql")) {
            cqGoBean.doNothing();
            return true;
        }
        getCookie(cqGoBean);
        if (cookieStr != null) {
            cqGoBean.doNothing();
            return true;
        }
        return false;
    }

    @Override
    public void run(CqGoBean cqGo) {
        ql = QLUtils.getQl();
        String cqMsg = cqGo.getMessage().trim().toLowerCase();
        getCookie(cqGo);
        String msg = "";
        if (cookieStr != null) {
            msg = addCookie(cookieStr, cqGo);
        }
        if (cqMsg.startsWith("qlrun")) {
            String keyword = cqMsg.substring(5).trim();
            msg = runTask(keyword);
        }
        if (cqMsg.startsWith("qlstop")) {
            String keyword = cqMsg.substring(6).trim();
            msg = stopTask(keyword);
        }
        if (cqMsg.startsWith("qldelt")) {
            String keyword = cqMsg.substring(6).trim();
            msg = delTask(keyword);
        }
        if (cqMsg.startsWith("qldist")) {
            String keyword = cqMsg.substring(6).trim();
            msg = disableTask(keyword);
        }
        if (cqMsg.startsWith("qlent")) {
            String keyword = cqMsg.substring(6).trim();
            msg = enableTask(keyword);
        }

        cqGo.sendStr(msg, null, cqGo.getGroupId() != null);
    }


    /**
     * 终止任务
     *
     * @param keyword
     * @return
     */
    private String stopTask(String keyword) {
        // 替换中文逗号为英文逗号
        if (keyword.contains("，")) keyword = keyword.replace("，", ",");
        String result = "";
        List<QLCron.CronData> stopTasks = cronMap.get("stopTasks");

        /**
         * 关键字可能出现的情况：为空，数字，不存在
         */
        // 为数字的情况
        if (stopTasks != null) {
            boolean isRun = false;
            try {
                String[] indexStrs = keyword.split(",");
                for (String indexStr : indexStrs) {
                    int index = Integer.parseInt(indexStr) - 1;
                    if (index > stopTasks.size() - 1) {
                        result += "⚠任务编号：" + indexStr + "超出范围，请输入正确的索引编号!\r\n";
                    } else {
                        QLCron.CronData cron = stopTasks.get(index);
                        result += qlCronApi(cron, "stop");
                        isRun = true;
                    }
                }
            } catch (NumberFormatException e) {
                result += "⚠任务编号中包含不能识别的内容，已终止 ！\r\n";
            }
            if (isRun) return result + "请到青龙后台查看是否已终止！";
        }
        // 终止所有列表任务
        if (keyword.equals("all") && stopTasks != null && !stopTasks.isEmpty()) {
            result = "";
            for (QLCron.CronData stopTask : stopTasks) {
                result += qlCronApi(stopTask, "stop");
            }
            return result + "请到青龙后台查看执行成功的任务日志！";
        }
        // 存在正在执行中的任务
        List<QLCron.CronData> runCrons = ql.getRunCron();
        if (runCrons != null) {
            stopTasks = runCrons;
            //result = "未查询到相关任务, 请重试";
        } else {
            // 任务不存在
            return "当前没有正在执行的任务, 请稍后执行!";
        }
        // 存在多个任务
        if (stopTasks.size() > 1) {
            // 存在多个任务先行存放
            cronMap.put("stopTasks", stopTasks);
            result = "查询到多个任务\r\n请回复:\"qlstop任务全名\"或\"qlstop任务编号\"(支持多个编号，请用逗号分割)\r\n回复\"qlstopall\"停止该列表所有任务\r\n";
            for (int i = 0; i < stopTasks.size(); i++) {
                result += (i + 1) + "、" + stopTasks.get(i).getName() + "\r\n";
            }
            return result;
        }
        if (stopTasks.size() == 1) {
            return qlCronApi(stopTasks.get(0), "stop") + "请到青龙后台查看执行成功的任务日志！";
        }
        return null;
    }

    /**
     * 删除任务
     * @param keyword
     * @return
     */
    private String delTask(String keyword){
        return  taskControl(keyword,"del","qldelt","请到青龙后台查看任务是否删除");
    }
    /**
     * 启用任务
     * @param keyword
     * @return
     */
    private String enableTask(String keyword){
        return  taskControl(keyword,"enable","qlent","请到青龙后台查看任务是否启用");
    }

    /**
     * 禁用任务
     * @param keyword
     * @return
     */
    private String disableTask(String keyword){
        return taskControl(keyword,"disable","qldist","请到青龙后台查看任务是否禁用");
    }


    /**
     * 执行任务
     *
     * @param keyword
     * @return
     */
    private String runTask(String keyword) {
        return taskControl(keyword,"run","qlrun","请到青龙后台查看执行成功的任务日志！");
    }


    private String addCookie(String cookieStr, CqGoBean cqGo) {
        QLEnvs envs = ql.getEnvs(cookie.get("pt_pin"));
        if (envs == null) {
            boolean flag = ql.addEnvs("JD_COOKIE", cookieStr, "remark=" + cqGo.getSender().getUserId() + ":");
            if (flag) return "CK添加成功, enjoy!!!";
        } else {
            QLEnvs.EnvsData env = envs.getEnvs().get(0);
            env.setValue(cookieStr);
            boolean flag = ql.alterEnvs(env);
            if (flag) return "CK更新成功, 重新上号完成!!!";
        }
        return "CK添加失败, 请查看日志";
    }

    private void getCookie(CqGoBean cqGo) {
        cookie = new HashMap<>();
        int start = cqGo.getMessage().indexOf("pt_key");
        int end = cqGo.getMessage().indexOf(" pt_token");

        if (start != -1 && end != -1) {
            cookieStr = cqGo.getMessage().substring(start, end);
        }
        if (cookieStr != null) {
            String[] split = cookieStr.split(" ");
            cookie.put("pt_key", split[0]);
            cookie.put("pt_pin", split[1]);
        }
    }
    private String taskControl(String keyword,String api,String order,String endMsg){
        // 替换中文逗号为英文逗号
        if (keyword.contains("，")) keyword = keyword.replace("，", ",");
        String result = "";
        List<QLCron.CronData> tasks = cronMap.get(api);

        /**
         * 关键字可能出现的情况：为空，数字，不存在
         */
        // 先处理关键词为空的情况
        if (keyword.isEmpty()) {
            return "请输入\"" +order+ "\"任务关键字";
        }
        // 为数字的情况
        if (tasks != null) {
            boolean isRun = false;
            try {
                String[] indexStrs = keyword.split(",");
                for (String indexStr : indexStrs) {
                    int index = Integer.parseInt(indexStr) - 1;
                    if (index > tasks.size() - 1) {
                        result += "⚠任务编号：" + indexStr + "超出范围，请输入正确的索引编号!\r\n";
                    } else {
                        QLCron.CronData cron = tasks.get(index);
                        result += qlCronApi(cron,api);
                        isRun = true;
                    }
                }
            } catch (NumberFormatException e) {
                result += "⚠任务编号中包含不能识别的内容，已终止 ！\r\n";
            }
            if (isRun) return result + endMsg;
        }
        // 执行所有列表任务
        if (keyword.equals("all") && tasks != null && !tasks.isEmpty()) {
            result = "";
            for (QLCron.CronData task : tasks) {
                result += qlCronApi(task,api);
            }
            return result + endMsg;
        }
        // 存在相关任务
        QLCron crons = ql.getCron(keyword);
        if (crons != null) {
            tasks = crons.getCorns();
            //result = "未查询到相关任务, 请重试";
        } else {
            // 任务不存在
            return "未查询到相关任务, 请重试!";
        }
        // 存在多个任务
        if (tasks.size() > 1) {
            // 存在多个任务先行存放
            cronMap.put(api, tasks);
            result = "查询到多个任务\r\n请回复:" +order+ "任务全名或\"qlrun任务编号\"(支持多个编号，请用逗号分割)\r\n回复\"" +order +"all\"执行该列表所有任务\r\n";
            for (int i = 0; i < tasks.size(); i++) {
                result += (i + 1) + "、" + tasks.get(i).getName() + "\r\n";
            }
            return result;
        }
        if (tasks.size() == 1) {
            return qlCronApi(tasks.get(0), api) + endMsg;
        }
        return null;
    }

    private String qlCronApi(QLCron.CronData crons, String api) {
        ArrayList<String> list = new ArrayList<>();
        list.add(crons.getId());
        if (api.equals("stop")) {
            if (ql.stopTask(list)) {
                return "任务: " + crons.getName() + "--终止成功！\r\n";
            } else {
                return "任务: " + crons.getName() + "--终止失败！\r\n";
            }
        }
        if (api.equals("run")){
            if (ql.runTask(list)){
                return "任务: " + crons.getName() + "--执行成功！\r\n";
            } else {
                return "任务: " + crons.getName() + "--执行失败！\r\n";
            }
        }
        if (api.equals("disable")) {
            if (ql.disableCrons(list)){
                return "任务: " + crons.getName() + "--禁用成功！\r\n";
            } else {
                return "任务: " + crons.getName() + "--禁用失败！\r\n";
            }
        }
        if (api.equals("enable")) {
            if (ql.enableCrons(list)){
                return "任务: " + crons.getName() + "--启用成功！\r\n";
            } else {
                return "任务: " + crons.getName() + "--启用失败！\r\n";
            }
        }
        if (api.equals("del")) {
            if (ql.delCron(list)){
                return "任务: " + crons.getName() + "--删除成功！\r\n";
            } else {
                return "任务: " + crons.getName() + "--删除失败！\r\n";
            }
        }
        return "taskControl执行失败!";
    }
}
