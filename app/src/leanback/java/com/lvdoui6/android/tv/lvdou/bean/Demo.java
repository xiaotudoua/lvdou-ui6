package com.lvdoui6.android.tv.lvdou.bean;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.lvdou.HawkConfig;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.hawk.Hawk;

public class Demo {

    @SerializedName("app_id")
    private String app_id;
    @SerializedName("app_key")
    private String app_key;
    @SerializedName("app_api")
    private String app_api;
    @SerializedName("mtj_key")
    private String mtj_key;
    @SerializedName("app_mark")
    private String app_mark;
    @SerializedName("mtj_channel")
    private String mtj_channel;

    private static Demo instance;

    static {
        String str = Hawk.get(HawkConfig.APP_CONFIG);
        instance = App.gson().fromJson(str, Demo.class);
    }

    private Demo() {}

    public static Demo getInstance() {
        return instance;
    }

    public String getApp_id() {
        return instance.app_id;
    }

    public String getApp_key() {
        return instance.app_key;
    }

    public void setApp_api(String app_api) {
        this.app_api = app_api;
    }

    public String getApp_api() {
        return instance.app_api;
    }

    public String getApp_mark() {
        return instance.app_mark;
    }

    public String getMtj_key() {
        return instance.mtj_key;
    }

    public String getMtj_channel() {
        return instance.mtj_channel;
    }
}
