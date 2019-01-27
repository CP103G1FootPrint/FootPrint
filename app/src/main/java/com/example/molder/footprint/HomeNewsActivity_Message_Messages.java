package com.example.molder.footprint;

public class HomeNewsActivity_Message_Messages {
    private int message_ci_profile＿pictureId;
    private String message_tv_news_userName;
    private String message_tv_news_Comment;

    public int getCi_profile＿pictureId() {
        return message_ci_profile＿pictureId;
    }

    public void setCi_profile＿pictureId(int ci_profile＿pictureId) {
        this.message_ci_profile＿pictureId = ci_profile＿pictureId;
    }

    public String getTv_news_userName() {
        return message_tv_news_userName;
    }

    public void setTv_news_userName(String tv_news_userName) {
        this.message_tv_news_userName = tv_news_userName;
    }

    public String getTv_news_Comment() {
        return message_tv_news_Comment;
    }

    public void setTv_news_Comment(String tv_news_Comment) {
        this.message_tv_news_Comment = tv_news_Comment;
    }

    public HomeNewsActivity_Message_Messages(int ci_profile＿pictureId, String tv_news_userName, String tv_news_Comment) {

        this.message_ci_profile＿pictureId = ci_profile＿pictureId;
        this.message_tv_news_userName = tv_news_userName;
        this.message_tv_news_Comment = tv_news_Comment;
    }
}
