package com.zxjk.moneyspace.bean;

public class BitMapBean {
    private String image;
    private ConfigBean configure;

    public static class ConfigBean {
        String side;

        public String getSide() {
            return side;
        }

        public void setSide(String side) {
            this.side = side;
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ConfigBean getConfigure() {
        return configure;
    }

    public void setConfigure(ConfigBean configure) {
        this.configure = configure;
    }
}
