package com.laoxin.LXBot.bean;

import com.fasterxml.jackson.annotation.JsonGetter;

public class MessageBean {
    private String msgType;
    private Long userId;
    private Long groupId;
    private String reply;   // 回复消息内容
    private String message; // 发送的消息
    private boolean autoEscape = false;  // 是否自动转换cq码

    public MessageBean(String msgType, Long userId, Long groupId, String message, boolean autoEscape) {
        this.msgType = msgType;
        this.userId = userId;
        this.groupId = groupId;
        this.message = message;
        this.autoEscape = autoEscape;
        this.reply = message;
    }

    public MessageBean() {
    }

    @JsonGetter("message_type")
    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getReply() {
        return reply;
    }


    @JsonGetter("auto_escape")
    public boolean getAutoEscape() {
        return autoEscape;
    }

    public void setAutoEscape(boolean autoEscape) {
        this.autoEscape = autoEscape;
    }
    @JsonGetter("user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    @JsonGetter("group_id")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.reply = message;
    }

    /**
     * 是否群发消息
     * @param flag
     */
    public void sendToGroup(Boolean flag){
        if (flag) {
            this.msgType = "private";
        } else {
            this.msgType = "group";
        }
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "msgType='" + msgType + '\'' +
                ", userId=" + userId +
                ", groupId=" + groupId +
                ", reply='" + reply + '\'' +
                ", autoEscape=" + autoEscape +
                '}';
    }
}
