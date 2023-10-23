package com.abdullahumer.i200528;

public class Chat {

    String chatId;
    String firstMemberId;
    String secondMemberId;
    String lastMessage;

    public Chat() {}

    public Chat(String chatId, String firstMemberId, String secondMemberId, String lastMessage) {

        this.chatId = chatId;
        this.firstMemberId = firstMemberId;
        this.secondMemberId = secondMemberId;
        this.lastMessage = lastMessage;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getFirstMemberId() {
        return firstMemberId;
    }

    public void setFirstMemberId(String firstMemberId) {
        this.firstMemberId = firstMemberId;
    }

    public String getSecondMemberId() {
        return secondMemberId;
    }

    public void setSecondMemberId(String secondMemberId) {
        this.secondMemberId = secondMemberId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
