package com.lvdoui6.android.tv.lvdou.bean;

import com.lvdoui6.android.tv.App;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QWeather {

    private int code;
    @SerializedName("update_time")
    private String updateTime;
    @SerializedName("fx_link")
    private String fxLink;
    @SerializedName("daily")
    private List<DailyBean> daily;

    public static QWeather objectFromData(String str) {

        return App.gson().fromJson(str, QWeather.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFxLink() {
        return fxLink;
    }

    public void setFxLink(String fxLink) {
        this.fxLink = fxLink;
    }

    public List<DailyBean> getDaily() {
        return daily;
    }

    public void setDaily(List<DailyBean> daily) {
        this.daily = daily;
    }

    public static class DailyBean {
        @SerializedName("fx_date")
        private String fxDate;
        @SerializedName("temp_min")
        private String tempMin;
        @SerializedName("temp_max")
        private String tempMax;
        @SerializedName("icon_day")
        private String iconDay;
        @SerializedName("text_day")
        private String textDay;
        @SerializedName("icon_night")
        private String iconNight;
        @SerializedName("text_night")
        private String textNight;

        public static DailyBean objectFromData(String str) {

            return App.gson().fromJson(str, DailyBean.class);
        }

        public String getFxDate() {
            return fxDate;
        }

        public void setFxDate(String fxDate) {
            this.fxDate = fxDate;
        }

        public String getTempMax() {
            return tempMax;
        }

        public void setTempMax(String tempMax) {
            this.tempMax = tempMax;
        }

        public String getTempMin() {
            return tempMin;
        }

        public void setTempMin(String tempMin) {
            this.tempMin = tempMin;
        }

        public String getIconDay() {
            return iconDay;
        }

        public void setIconDay(String iconDay) {
            this.iconDay = iconDay;
        }

        public String getTextDay() {
            return textDay;
        }

        public void setTextDay(String textDay) {
            this.textDay = textDay;
        }

        public String getIconNight() {
            return iconNight;
        }

        public void setIconNight(String iconNight) {
            this.iconNight = iconNight;
        }

        public String getTextNight() {
            return textNight;
        }

        public void setTextNight(String textNight) {
            this.textNight = textNight;
        }
    }
}
