package io.project.MyJavaDemoBot.service;

public class User {
    long chatId;
    String nickname;
    String firstName;
    String lastName;
    long messageCounter=0;
    double gold;
    double money;


    public User(long chatId, String nickname, String firstName, String lastName, double gold, double money) {
        this.chatId = chatId;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gold = gold;
        this.money = money;
    }

    public User() {
    }



    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", nickname='" + nickname + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gold=" + gold +
                ", money=" + money +
                '}';
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getMessageCounter() {
        return messageCounter;
    }

    public void setMessageCounter(long messageCounter) {
        this.messageCounter = messageCounter;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
