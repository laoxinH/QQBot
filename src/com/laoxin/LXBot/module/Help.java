package com.laoxin.LXBot.module;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.utils.BotConfigUtils;
import com.laoxin.LXBot.utils.ReplyConfigUtils;

import java.util.List;

public class Help implements ModuleInterface {
    private String cqMsg;
    private boolean isUser;
    private boolean isAdmin;
    private boolean isHelp;
    private BotConfigUtils config;
    private ReplyConfigUtils replyConfig;


    @Override
    public boolean whetherRun(CqGoBean cqGoBean) {
        cqMsg = cqGoBean.getMessage().trim();
        isUser = isUser();
        isAdmin = isAdmin(cqGoBean);
        isHelp = isHelp();
        return isAdmin || isUser || isHelp;
    }

    private boolean isHelp() {
        if ((cqMsg.endsWith("help") || cqMsg.endsWith("帮助")) && !cqMsg.startsWith("help") && !cqMsg.startsWith("帮助"))
            return true;
        return false;
    }
    @Override
    public void run(CqGoBean cqGo) {
        replyConfig = ReplyConfigUtils.getReplyConfig();
        config = BotConfigUtils.getBotConfig();
        if (isUser) {
            cqGo.fastReplyStr(getUserHelpText());

        }
        if (isAdmin) {
            cqGo.fastReplyStr(getAdminHelpText());

        }
        if (isHelp) {
            cqGo.fastReplyStr(getHelp(cqGo));

        }
    }

    private String getHelp(CqGoBean cqGo) {
        String helpText = "";
        String keyword = "";
        if (cqMsg.indexOf("help") != -1) keyword = cqMsg.substring(0, cqMsg.indexOf("help"));
        if (cqMsg.indexOf("帮助") != -1) keyword = cqMsg.substring(0, cqMsg.indexOf("帮助"));
        List<BotConfigUtils.Funcmode> funcmodeList = config.getFuncmodeList();
        List<BotConfigUtils.Funcmode> suppermodeList = config.getSuppermodeList();

        for (BotConfigUtils.Funcmode funcmode : funcmodeList) {
            if (funcmode.getName().equals(keyword)) {
                return "功能简介: " + funcmode.getDescribe() + "\r\n使用说明: " + funcmode.getHelp();
            }
        }

        boolean isAdmin = false;
        for (Long aLong : config.getSupper()) {
            if (aLong.equals(cqGo.getSender().getUserId())) {
                isAdmin = true;
                break;
            }
        }
        if (isAdmin) {
            for (BotConfigUtils.Funcmode funcmode : suppermodeList) {
                if (funcmode.getName().equals(keyword)) {

                    return "功能简介: " + funcmode.getDescribe() + "\r\n使用说明: " + funcmode.getHelp();
                }
            }
        }

        return "没有你要查询的功能哦, 请仔细检查~~";
    }

    private String getAdminHelpText() {

        List<BotConfigUtils.Funcmode> funcs = config.getSuppermodeList();
        String helpText = helpTextFormat("", "", "管理员菜单", 6, "-");
        ///helpTxt += helpTextFormat("","","-",5,"+");
        for (int i = 0; i < funcs.size(); i++) {
            if ((i + 1) % 2 == 0) {
                helpText += helpTextFormat("", funcs.get(i).getName(), "", 5, " ");
            } else if ((i + 1) % 2 == 1) {
                helpText += helpTextFormat(funcs.get(i).getName(), "", "", 5, " ");
            }
        }
        helpText += "\r\n回复\"功能名称help\"或\r\n\"功能名称帮助\"\r\n查看使用说明";
        return helpText;
    }

    private String getUserHelpText() {
        List<BotConfigUtils.Funcmode> funcs = config.getFuncmodeList();
        String helpText = helpTextFormat("", "", "帮助菜单", 5, "-");
        ///helpTxt += helpTextFormat("","","-",5,"+");
        for (int i = 0; i < funcs.size(); i++) {
            if ((i + 1) % 2 == 0) {
                helpText += helpTextFormat("", funcs.get(i).getName(), "", 5, " ");
            } else if ((i + 1) % 2 == 1) {
                helpText += helpTextFormat(funcs.get(i).getName(), "", "", 5, " ");
            }
        }
        helpText += "\r\n";
        helpText += helpTextFormat("", "", "其他功能", 5, "-");
        List<ReplyConfigUtils.Reply> replies = replyConfig.getReplyList();
        for (int i = 0; i < replies.size(); i++) {
            if ((i + 1) % 2 == 0) {
                helpText += helpTextFormat("", replies.get(i).getKeyword().replace("|", ""), "", 5, " ");
            } else if ((i + 1) % 2 == 1) {
                helpText += helpTextFormat(replies.get(i).getKeyword().replace("|", ""), "", "", 5, " ");
            }
        }
        helpText += "\r\n回复\"功能名称help\"或\r\n\"功能名称帮助\"\r\n查看使用说明";

        return helpText;

    }

    private boolean isUser() {
        if (cqMsg.equals("帮助") || cqMsg.equalsIgnoreCase("help") || cqMsg.equals("菜单") || cqMsg.equals("功能"))
            return true;
        return false;
    }

    private boolean isAdmin(CqGoBean cqGoBean) {
        if (cqMsg.equals("管理员功能") || cqMsg.equals("管理员帮助") || cqMsg.equals("管理员菜单") || cqMsg.equals("超级菜单")) {
            BotConfigUtils botConfig = BotConfigUtils.getBotConfig();
            for (Long aLong : botConfig.getSupper()) {
                if (cqGoBean.getSender().getUserId().equals(aLong)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String helpTextFormat(String str1, String str2, String title, int len, String split) {
        int str1len = str1.length();
        int str2len = str2.length();
        int titleLen = title.length();
        if (len - str1len > 0) str1 += makeBlank(len - str1len, " ");
        if (len - str2len > 0) str2 = makeBlank(len - str2len, " ") + str2;
        if (len - titleLen > 0 && !title.isEmpty()) {
            String blank = makeBlank((len - titleLen) * 2, split);
            return blank + title + blank + "\r\n\r\n";
        }
        if (str1len == 0) return (" | " + str2 + "\r\n");
        if (str2len == 0) return str1;

        return str1 + " | " + str2 + "\r\n";
    }


    private String makeBlank(int len, String split) {
        len = len * 2;
        String str = "";
        for (int i = 0; i < len; i++) {
            str += split;
        }
        return str;
    }
}