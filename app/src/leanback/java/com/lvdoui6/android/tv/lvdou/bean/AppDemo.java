package com.lvdoui6.android.tv.lvdou.bean;

import com.google.gson.annotations.SerializedName;

public class AppDemo {

    @SerializedName("app_id")
    private String app_id;
    @SerializedName("app_key")
    private String app_key;
    @SerializedName("app_adm")
    private String app_adm;
    @SerializedName("mtj_key")
    private String mtj_key;
    @SerializedName("odd_key")
    private String odd_key;
    @SerializedName("apk_mark")
    private String apk_mark;
    @SerializedName("char_set")
    private String char_set;
    @SerializedName("app_pro")
    private String app_pro;
    @SerializedName("mtj_canal")
    private String mtj_canal;

    public String getApk_mark() {
        return apk_mark;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getApp_key() {
        return app_key;
    }

    public String getApp_adm() {
        return app_adm;
    }

    public String getMtj_key() {
        return mtj_key;
    }

    public String getOdd_Key() {
        return odd_key;
    }

    public String getChar_set() {
        return char_set;
    }

    public String getApp_pro() {
        return app_pro;
    }

    public String getMtj_canal() {
        return mtj_canal;
    }
}
