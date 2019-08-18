package com.innsbluck.navisidelauncher;

import com.google.gson.JsonElement;

import java.io.Serializable;

public class Action implements Serializable {
    private String title;
    private String appPackage;

    public Action(String title, String appPackage) {
        this.title = title;
        this.appPackage = appPackage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }
}
