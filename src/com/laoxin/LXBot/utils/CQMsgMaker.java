package com.laoxin.LXBot.utils;

import com.laoxin.LXBot.bean.CqGoBean;
import com.laoxin.LXBot.bean.MessageBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CQMsgMaker {

    private static ObjectMapper mapper = new ObjectMapper();

    private CQMsgMaker() {
    }


    /**
     * 创建群聊文本消息(带@)
     *
     * @param msg  消息类容
     * @param cqGo cqGo对象
     * @return
     */
    public static String makeStrMsgAt(String msg, CqGoBean cqGo) {
        msg = "[CQ:at,qq=" + cqGo.getSender().getUserId() + "]" + msg;
        return makeStrMsg(msg, cqGo, true);
    }

    /**
     * 创建文本消息(支持CQ码)
     *
     * @param msg     消息内容
     * @param cqGo    cqGo对象
     * @param isGroup 是否群发
     * @return
     */
    public static String makeStrMsg(String msg, CqGoBean cqGo, Boolean isGroup) {
        // 创建消息对象
        MessageBean messageBean = new MessageBean();
        // 设置消息内容
        messageBean.setMessage(msg);
        // 设置消息类型(群聊,私聊)
        if (isGroup) {
            messageBean.setMsgType("group");
        } else {
            messageBean.setMsgType("private");
        }
        // 设置接收qq群号
        messageBean.setGroupId(cqGo.getGroupId());
        // 设置接收的qq号
        messageBean.setUserId(cqGo.getSender().getUserId());
        String reMsg = "";
        try {
            reMsg = mapper.writeValueAsString(messageBean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return reMsg;
    }

    private static String handleUrl(String url){
        if (url.matches("\\?.*=.*")){
            url+="t=" + System.currentTimeMillis();
        } else {
            url += "?t=" + System.currentTimeMillis();
        }
        return url;
    }
    /**
     * 创建图片消息
     *
     * @param imgUrl
     * @param cqGo
     * @param isGroup
     * @return
     */
    public static String makeImgMsg(String imgUrl, CqGoBean cqGo, Boolean isGroup) {
        imgUrl = handleUrl(imgUrl);
        String cqCode = "[CQ:image,file=" + imgUrl + ",c=2]";
        return makeStrMsg(cqCode, cqGo, isGroup);
    }

    /**
     * 创建图片消息(带@)
     *
     * @param imgUrl
     * @param cqGo
     * @return
     */
    public static String makeImgMsgAt(String imgUrl, CqGoBean cqGo) {
        imgUrl = handleUrl(imgUrl);
        String cqCode = "[CQ:image,file=" + imgUrl + ",c=2]";
        return makeStrMsgAt(cqCode, cqGo);
    }

    /**
     * 生成图片文本混合消息
     * @param cqGo
     * @param isGroup
     * @param imgUrl
     * @param msgs 可传入多行消息
     * @return
     */
    public static String makeStrAndImgMsg(CqGoBean cqGo, Boolean isGroup, String imgUrl, String... msgs) {
        imgUrl = handleUrl(imgUrl);
        String cqCode = "";
        for (String msg : msgs) {
            cqCode += msg + "\r\n";
        }
        cqCode+="[CQ:image,file=" + imgUrl + ",c=2]";
        return makeStrMsg(cqCode,cqGo,isGroup);
    }

    /**
     *
     * @param cqGo
     * @param imgUrl
     * @param msgs
     * @return
     */
    public static String makeStrAndImgMsgAt(CqGoBean cqGo, String imgUrl, String... msgs) {
        imgUrl = handleUrl(imgUrl);
        String cqCode = "";
        for (String msg : msgs) {
            cqCode += msg + "\r\n";
        }
        cqCode+="[CQ:image,file=" + imgUrl + ",c=2]";
        return makeStrMsgAt(cqCode,cqGo);
    }

    /**
     * 通过message对象返回消息
     * @param message
     * @return
     */
    public static String makeMsg(MessageBean message){
        String json = "";
        try {
            json =  mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
