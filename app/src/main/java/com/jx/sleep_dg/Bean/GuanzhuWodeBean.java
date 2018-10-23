package com.jx.sleep_dg.Bean;


public class GuanzhuWodeBean {
    private boolean isPrivacy;
    String imageUrl;
    String name;
    String state;
    boolean isGuanZhu;

    public boolean isPrivacy() {
        return isPrivacy;
    }

    public void setPrivacy(boolean privacy) {
        isPrivacy = privacy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isGuanZhu() {
        return isGuanZhu;
    }

    public void setGuanZhu(boolean guanZhu) {
        isGuanZhu = guanZhu;
    }
}
