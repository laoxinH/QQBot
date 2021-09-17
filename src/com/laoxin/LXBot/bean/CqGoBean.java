package com.laoxin.LXBot.bean;
import com.laoxin.LXBot.botService.httpSocket.HttpResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import com.laoxin.LXBot.utils.BotConfigUtils;
import com.laoxin.LXBot.utils.CQMsgMaker;
import com.laoxin.LXBot.utils.HttpOpts;
import com.laoxin.LXBot.utils.HttpUtils;
import com.sun.istack.internal.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CqGoBean {
    private String message; // 收到的消息
    private String msgType;  // 消息类型(私聊或群发)
    private String postType; // 上报类型(通知,消息,心跳包...)
    private SenderBean senderBean;   // 发送者对象
    private Long messageId; // 消息id
    private Long groupId;  // 群消息的群号
    private HttpResponse response; // 消息回复对象
    private Long sendTime;        // 消息发送时间
    private BotConfigUtils cqConfig = BotConfigUtils.getBotConfig();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgType() {
        return msgType;
    }
    @JsonSetter("message_type")
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getPostType() {
        return postType;
    }

    @JsonSetter("post_type")
    public void setPostType(String postType) {
        this.postType = postType;
    }

    public SenderBean getSender() {
        return senderBean;
    }

    public void setSender(SenderBean senderBean) {
        this.senderBean = senderBean;
    }

    public Long getMessageId() {
        return messageId;
    }

    @JsonSetter("message_id")
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public Long getSendTime() {
        return sendTime;
    }
    @JsonSetter("time")
    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public Long getGroupId() {
        return groupId;
    }

    @JsonSetter("group_id")
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * 快速回复消息
     * @param jsonMsg json字符串,示例{message:"回复内容"}
     */
    public void fastReply(String jsonMsg){
        response.send(jsonMsg);
    }

    /**
     * 快速回复文本消息(支持CQ码)
     * @param msg
     */
    public void fastReplyStr(String msg){
       msg =  CQMsgMaker.makeStrMsg(msg,this,false);
       fastReply(msg);
    }

    /**
     * 快速回复带@的文本消息(建议群聊启用)
     * @param msg
     */
    public void fastReplyStrAt(String msg){
        msg = CQMsgMaker.makeStrMsgAt(msg,this);
        fastReply(msg);
    }

    /**
     * 快速回复图片消息
     * @param imgUrl
     */
    public  void fastReplyImg(String imgUrl){
        String msg = CQMsgMaker.makeImgMsg(imgUrl, this, false);
        fastReply(msg);
    }

    /**
     * 快速回复图片消息(带@)
     * @param imgUrl
     */

    public  void fastReplyImgAt(String imgUrl){
        String msg = CQMsgMaker.makeImgMsgAt(imgUrl, this);
        fastReply(msg);
    }
    /**
     * 快速回复图片和文字消息（支持多行文字，自动换行）
     * @param imgUrl
     * @param msgs
     */
    public void fastReplyImgAndStr(String imgUrl,String... msgs){
        String msg = CQMsgMaker.makeStrAndImgMsg(this, false, imgUrl, msgs);
        fastReply(msg);
    }

    /**
     * 快速回复图片和文字消息（带@，支持多行文字，自动换行）
     * @param imgUrl
     * @param msgs
     */
    public void fastReplyImgAndStrAt(String imgUrl,String... msgs){
        String msg = CQMsgMaker.makeStrAndImgMsgAt(this, imgUrl, msgs);
        fastReply(msg);
    }

    /**
     * 通过MessageBean对象发送自定义消息
     * @param message message对象（包含发送的QQ号，QQ群号，消息类型,消息内容）
     */
    public String sendMessage(MessageBean message) {
        String msg = CQMsgMaker.makeMsg(message);
        return sendMessage(msg);
    }

    /**
     * 通过json字符串发送自定义消息
     * @param msgJson json字符串（参考go-cqhttp文档）
     * @return
     */
    public String sendMessage(String msgJson){
        HttpOpts post = new HttpOpts("post", "http://" +cqConfig.getCqgo().getHost()+ ":" + cqConfig.getCqgo().getPort() +"/send_msg");
        post.setHeader("Content-Type","application/json");
        post.setRequestData(msgJson);
        return HttpUtils.httpConn(post).getData();
    }

    /**
     * 发送文字信息
     * @param msg 支持CQ码
     * @param sendToID 回复的ID(qq号,或者群号)
     * @param isGroup 默认false
     * @return
     */
    public String sendStr(String msg,Long sendToID,@NotNull Boolean isGroup){

        setToID(sendToID,isGroup);

        String msgJson = CQMsgMaker.makeStrMsg(msg, this, isGroup);
        return sendMessage(msgJson);
    }

    private void setToID(Long sendToID, Boolean isGroup) {
        // isGroup 默认为false
        if (isGroup == null) {
            isGroup = false;
        }
        // 无法找到发送群号时,isGroup置为false
        if (this.getGroupId() == null && isGroup && sendToID == null){
            isGroup = false;
        }
        // 当发送对象为群聊时,默认sendToID 为 接收的群号
        //System.out.println(this.getGroupId());

        if (sendToID == null && isGroup && this.getGroupId() != null) {
            sendToID = this.getGroupId();
        }
        // 当发送对象为私聊时,默认sendToID 为 接收的QQ
        if (sendToID == null && !isGroup) {
            sendToID = this.getSender().getUserId();
        }
        if (isGroup){
            this.setGroupId(sendToID);
        } else {
            this.getSender().setUserId(sendToID);
        }
    }

    /**
     * 发送带@的文字消息
     * @param msg
     * @return
     */
    public String sendStrAt(String msg){
        if (this.getGroupId() == null){
            throw new NullPointerException("消息发送者不是来自群聊, 此方法无法使用!");
        }
        String msgJson = CQMsgMaker.makeStrMsgAt(msg, this);
        return sendMessage(msgJson);
    }

    /**
     * 发送图片消息
     * @param imgUrl
     * @param sendToID
     * @param isGroup
     * @return
     */
    public String sendImg(String imgUrl,Long sendToID,@NotNull Boolean isGroup){
        // isGroup 默认为false
        setToID(sendToID,isGroup);
        String msgJson = CQMsgMaker.makeImgMsg(imgUrl, this, isGroup);
        return sendMessage(msgJson);
    }

    /**
     * 发送图片和文字消息(支持多行文字,自动换行)
     * @param sendToID
     * @param isGroup
     * @param imgUrl
     * @param msgs
     * @return
     */

    public String sendImgAndStr(Long sendToID,@NotNull Boolean isGroup,String imgUrl,String... msgs){
        setToID(sendToID,isGroup);
        String msgJson = CQMsgMaker.makeStrAndImgMsg(this, isGroup, imgUrl, msgs);
        return sendMessage(msgJson);
    }

    /**
     * 发送图片和文字消息(带@,支持多行文字,自动换行)
     * @param imgUrl
     * @param msgs
     * @return
     */
    public String sendImgAndStrAt(String imgUrl,String... msgs){
        if (this.getGroupId() == null){
            throw new NullPointerException("消息发送者不是来自群聊, 此方法无法使用!");
        }
        String msgJson = CQMsgMaker.makeStrAndImgMsgAt(this, imgUrl, msgs);
        return sendMessage(msgJson);
    }

    /**
     * 发送图片消息(带@)
     * @param imgUrl
     * @return
     */
    public String sendImgAt(String imgUrl) {
        if (this.getGroupId() == null){
            throw new NullPointerException("消息发送者不是来自群聊, 此方法无法使用!");
        }
        String msgJson = CQMsgMaker.makeImgMsgAt(imgUrl, this);
        return sendMessage(msgJson);
    }

    public void handleHeartBeat(){
        response.send("{}");
    }

    public void doNothing(){
        response.send("{}");
    }

    @Override
    public String toString() {
        return "CqGoBean{" +
                "message='" + message + '\'' +
                ", msgType='" + msgType + '\'' +
                ", postType='" + postType + '\'' +
                ", senderBean=" + senderBean +
                ", messageId=" + messageId +
                ", groupId=" + groupId +
                ", response=" + response +
                ", sendTime=" + sendTime +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SenderBean {
        private String nickname;  // 昵称
        private Long userId;      // 发送者QQ号
        private String role;      // 群权限(管理员或者成员)

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Long getUserId() {
            return userId;
        }
        @JsonSetter("user_id")
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "Sender{" +
                    "nickname='" + nickname + '\'' +
                    ", userId=" + userId +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}
