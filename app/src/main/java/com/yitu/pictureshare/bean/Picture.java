package com.yitu.pictureshare.bean;

import android.graphics.Bitmap;

public class Picture {

    String id;

    String userId;

    Bitmap pictureData;

    Integer likeNum;

    String title;

    String author;

    public Picture() {
    }

    public Picture(String id, String userId, Bitmap pictureData, Integer likeNum, String title, String author) {
        this.id = id;
        this.userId = userId;
        this.pictureData = pictureData;
        this.likeNum = likeNum;
        this.title = title;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Bitmap getPictureData() {
        return pictureData;
    }

    public void setPictureData(Bitmap pictureData) {
        this.pictureData = pictureData;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
