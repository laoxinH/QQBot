package com.laoxin.LXBot.module;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.bean.HttpRespBean;
import com.laoxin.LXBot.utils.HttpOpts;
import com.laoxin.LXBot.utils.HttpUtils;
import com.laoxin.LXBot.utils.HttpsUtils;
import com.laoxin.LXBot.utils.ReplyConfigUtils;

public class CustomReply implements ModuleInterface {

    @Override
    public boolean whetherRun(CqGoBean cqGoBean) {
        if (getReply(cqGoBean) != null) {
            return true;
        }
        return false;
    }

    @Override
    public void run(CqGoBean cqGo) {
        ReplyConfigUtils.Reply reply = getReply(cqGo);
        if (reply != null) {
            if (reply.getType().equalsIgnoreCase("text")) {
                cqGo.fastReplyStr(reply.getReply());
            } else if (reply.getType().equalsIgnoreCase("url-text")) {
                HttpOpts opts = new HttpOpts("get", reply.getUrl());
                opts.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
                opts.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"93\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"93\"");
                opts.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                HttpRespBean httpRespBean = null;
                if (reply.getUrl().startsWith("https"))
                    httpRespBean = HttpsUtils.httpsConn(opts);
                if (reply.getUrl().startsWith("http://"))
                    httpRespBean = HttpUtils.httpConn(opts);
                cqGo.fastReplyStr(httpRespBean.getData().replaceAll("<br>", "\r\n"));
            } else if (reply.getType().equalsIgnoreCase("url-img")) {
                cqGo.fastReplyImg(reply.getUrl());
            } else if (reply.getType().equalsIgnoreCase("mix")) {
                cqGo.fastReplyImgAndStr(reply.getUrl(), reply.getReply());
            }
        } else {
            cqGo.doNothing();
        }
    }

    private ReplyConfigUtils.Reply getReply(CqGoBean cqGoBean) {
        String message = cqGoBean.getMessage();
        ReplyConfigUtils replyConfig = ReplyConfigUtils.getReplyConfig();
        for (ReplyConfigUtils.Reply reply : replyConfig.getReplyList()) {
            for (String word : reply.getKeyword().split("\\|")) {
                if (reply.getMod() == 1) {
                    if (message.equalsIgnoreCase(word)) {
                        return reply;
                    }
                } else if (reply.getMod() == 2) {
                    if (message.contains(word)) {
                        return reply;
                    }
                }
            }
        }
        return null;
    }
}
