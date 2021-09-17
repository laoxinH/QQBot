package com.laoxin.LXBot.module.ql;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.module.ModuleInterface;
import com.laoxin.LXBot.module.ql.bean.QLCron;
import com.laoxin.LXBot.module.ql.bean.QLEnvs;
import com.laoxin.LXBot.module.ql.utils.QLUtils;
import com.laoxin.LXBot.utils.BotConfigUtils;

import java.util.ArrayList;
import java.util.List;

public class JD implements ModuleInterface {
    private String cqMsg;
    private boolean isAdmin;
    private String keyword;
    private BotConfigUtils config;
    private QLUtils ql;

    @Override
    public boolean whetherRun(CqGoBean cqGoBean) {
        cqMsg = cqGoBean.getMessage().toLowerCase().trim();
        if (cqMsg.startsWith("jdcx") || cqMsg.startsWith("京东查询")) {
            keyword = cqMsg.substring(4).trim();
            config = BotConfigUtils.getBotConfig();
            for (Long aLong : config.getSupper()) {
                if (aLong.equals(cqGoBean.getSender().getUserId())) {
                    isAdmin = true;
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void run(CqGoBean cqGo) {
        ql = QLUtils.getQl();
        cqGo.doNothing();
        if (isAdmin && !keyword.isEmpty() && keyword != null) {
            try {
                int index = Integer.parseInt(keyword);
                QLEnvs envs = ql.getEnvs("JD_COOKIE");
                List<QLEnvs.EnvsData> cookies = envs.getEnvs();
                if (cookies.size() < index) {
                    cqGo.sendStr("索引编号超出cookie总数（当前cookie数" + cookies.size() +"）",null,cqGo.getGroupId() != null);
                }
                QLEnvs.EnvsData cookie = cookies.get(index - 1);
                getJdInfo(cqGo,cookie);
            }catch (NumberFormatException e){
                cqGo.sendStr("请检查编号是否正确（编号只能是数字）",null,cqGo.getGroupId() != null);
            }
            return;
        } else if (!isAdmin && !keyword.isEmpty() && keyword != null){
            cqGo.sendStr("权限不够!", null, cqGo.getGroupId() != null);
            return;
        }
        getJdInfo(cqGo, null);

    }

    private void getJdInfo(CqGoBean cqGo, QLEnvs.EnvsData cookie) {
        QLEnvs.EnvsData envsData = null;

        if (cookie != null) {
            envsData = cookie;
        } else {
            QLEnvs envs = ql.getEnvs(cqGo.getSender().getUserId() + "");
            if (envs == null) {
                cqGo.sendStr("当前QQ未绑定JD，请私聊或群发浏览器获取的cookie给机器人即可绑定！",null,cqGo.getGroupId() != null);
                return;
            }
            envsData = envs.getEnvs().get(0);
        }
        cqGo.sendStr("正在为你查询，请稍等！",null,cqGo.getGroupId() != null);
        ql.addEnvs("tempck",envsData.getValue(),cqGo.getSender().getUserId() + "");
        QLCron.CronData cronData = ql.addCron("task lxjdbeanchange.js", "temp" + cqGo.getSender().getUserId() + "", "*");
        ArrayList<String> taskList = new ArrayList<>();
        taskList.add(cronData.getId());
        ql.runTask(taskList);
    }
}
