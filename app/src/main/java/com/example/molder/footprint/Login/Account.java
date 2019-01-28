package com.example.molder.footprint.Login;

public class Account {
    private String account;
    private String password;
    private String nickname;
    private String birthday;
    private String constellation;
    private int integral;

    public Account() {

    }

    public Account(String account, String password, String nickname, String birthday, String constellation, int integral) {
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.birthday = birthday;
        this.constellation = constellation;
        this.integral = integral;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }
}
