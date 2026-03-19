package com.lvdoui6.android.tv.lvdou;

import static org.greenrobot.eventbus.EventBus.TAG;

import android.util.Log;

import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.lvdou.bean.AdmUser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

public class HawkCustom {

    private static final String CUSTOM_CONFIG = "custom_config";
    public static final String HAS_CONFIG = "has_config";

    private static class Loader {
        static volatile HawkCustom INSTANCE = new HawkCustom();
    }

    public static HawkCustom get() {
        return Loader.INSTANCE;
    }

    public void load(Callback callback) {
        OkGo.<String>get(Utils.getApi("uploads/tvbox/config/" + Utils.getAppId() + ".json"))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "配置请求成功: " + body);
                        Hawk.put(CUSTOM_CONFIG, body);
                        callback.success();
                    }

                    @Override
                    public void onError(Response<String> error) {
                        Log.d(TAG, "配置请求失败: " + error.body());
                        callback.error(error.body());
                    }
                });
    }

    public void addConfig(String body) {
        Hawk.put(CUSTOM_CONFIG, body);
        Hawk.put(HAS_CONFIG, true);
    }

    public boolean hasConfig() {
        return Hawk.get(HAS_CONFIG, false);
    }

    public String getConfig(String fieldName, String defaultValue) {
        String customConfig = Hawk.get(CUSTOM_CONFIG, "{}");
        try {
            if (customConfig.equals("{}")) return defaultValue;
            JSONObject jsonObject = new JSONObject(customConfig);
            return jsonObject.optString(fieldName, defaultValue);
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public int getConfig(String fieldName, int defaultValue) {
        String customConfig = Hawk.get(CUSTOM_CONFIG, "{}");
        Log.d(TAG, "customConfig: " + customConfig);
        try {
            if (customConfig.equals("{}")) return defaultValue;
            JSONObject jsonObject = new JSONObject(customConfig);
            return jsonObject.optInt(fieldName, defaultValue);
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public boolean getConfig(String fieldName, boolean defaultValue) {
        String customConfig = Hawk.get(CUSTOM_CONFIG, "{}");
        try {
            if (customConfig.equals("{}")) return defaultValue;
            JSONObject jsonObject = new JSONObject(customConfig);
            String value = jsonObject.optString(fieldName);
            if (value == null || value.isEmpty()) return defaultValue;
            if ("custom_depot".equals(fieldName)) {
                if ("自动".equals(value)) {
                    try {
                        AdmUser.DataBean.UserinfoBean userInfo = HawkUser.userInfo();
                        if (userInfo != null) {
                            long vipTime = userInfo.getVipendtime();
                            if (vipTime == 88888888) return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }
            if ("开启".equals(value)) {
                return true;
            } else if ("关闭".equals(value)) {
                return false;
            }
            return defaultValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
