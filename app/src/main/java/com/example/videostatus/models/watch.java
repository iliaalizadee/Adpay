package com.example.videostatus.models;

public class watch {
    String title;
    String imageURI;
    String ButtonNum;
    String tag;
    public watch(){

    }
    public watch(String imageuri,String title,String buttonnum,String tag){
        this.ButtonNum=buttonnum;
        this.imageURI=imageuri;
        this.title=title;
        this.tag=tag;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtonNum() {
        return ButtonNum;
    }

    public void setButtonNum(String buttonNum) {
        ButtonNum = buttonNum;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
