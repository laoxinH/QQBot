package com.laoxin.LXBot.CQHandle;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.module.ModuleInterface;
import com.laoxin.LXBot.utils.BotConfigUtils;

import java.util.ServiceLoader;

public class CQHandler {
    private CQHandler() {}

    public static void handleMsg(CqGoBean cqGo) {
        // 心跳包处理
        if (cqGo.getPostType().equalsIgnoreCase("meta_event")) {
            cqGo.handleHeartBeat();
            return;
        }
        // 模块调用
        BotConfigUtils config = BotConfigUtils.getBotConfig();
        // 判断是否群聊
        boolean isGroup = (cqGo.getGroupId() != null);
        // 判断是否为监听的群聊
        boolean isListenGroup = false;
        if (isGroup) {
            for (Long groupId : config.getGroupId()) {
                if (groupId.longValue() == cqGo.getGroupId().longValue()) {
                    isGroup = true;
                    break;
                }
            }
        }
        // 获取私聊名单模式
        String listMod = config.getUser().getListmod();
        boolean isBlack = "black".equalsIgnoreCase(listMod);
        boolean isWhite = "white".equalsIgnoreCase(listMod);
        //判断私聊是否监听私聊消息
        boolean isListenUser = false;
        for (Long userId : config.getUser().getUserId()) {
            if (userId.longValue() == cqGo.getSender().getUserId().longValue()) {
                if (isBlack) {
                    isListenUser = false;
                }
                if (isWhite) {
                    isListenUser = true;
                }
                break;
            }
        }

        // 是否有模块执行的标志
        boolean whetherRun = false;
        if (isListenUser || isListenGroup){
            ServiceLoader<ModuleInterface> load = ServiceLoader.load(ModuleInterface.class);
            for (ModuleInterface mod : load) {
                if (whetherRun = mod.whetherRun(cqGo)) {
                    mod.run(cqGo);
                    break;
                }
            }
        }
        // 如果没有任何模块执行则向gocq返回空数据
        if (!whetherRun){
            cqGo.doNothing();
        }
    }
}
