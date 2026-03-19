package com.lvdoui6.android.tv.lvdou;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.lvdou.bean.AdmUser;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;

/**
 * @author jkpan
 * @date :2024/03/01
 * @description: Hawk
 */
public class HawkUser {

    public static final String MARK_CODE = "mark_code"; //设备标识
    public static final String USER_DATA = "user_data"; //用户数据
    public static final String USER_TOKEN = "user_token"; //token
    public static final String USER_ACCOUNT = "user_account"; //user

    public static void saveUser(@Nullable AdmUser value) {
        if (value == null) {
            Hawk.delete(USER_DATA); //删除用户数据
            Hawk.delete(USER_TOKEN); //删除用户token
        } else {
            try {
                Hawk.put(USER_DATA, App.gson().toJson(value)); //保存用户数据
                Hawk.put(USER_TOKEN, value.getData().getUserinfo().getToken()); //保存用户token
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 获取用户本地数据
    public static AdmUser loadUser(@Nullable String defaultValue) {
        Intrinsics.checkParameterIsNotNull(defaultValue, "defaultValue");
        String msg = Hawk.get(USER_DATA, defaultValue);
        return msg == null || ((CharSequence) msg).length() == 0 ? null : AdmUser.objectFromData(msg);
    }

    // 获取用户本地信息
    public static AdmUser.DataBean.UserinfoBean userInfo() {
        AdmUser userBean = loadUser("");
        if (userBean != null && userBean.getCode() == 1 && userBean.getData() != null){
            return userBean.getData().getUserinfo();
        }
        return null;
    }

    public static boolean checkLogin() {
        return Hawk.contains(USER_DATA) && Hawk.contains(USER_TOKEN);
    }

    /**
     * @author jkpan
     * @date :2024/03/01
     * @description: mode等于0时免费。mode等于3时全收费。mode等于type时收费。ture免费，false收费
     */
    public static boolean checkFree(int type) { //是否免费: 1、点播 2、直播
        Adm.DataBean.SiteConfigBean siteConfig = HawkAdm.getSiteConfig();
        if (siteConfig == null) return false; //默认全收费
        String operationMode = siteConfig.getApp_config().getOperationmode();
        int mode = Integer.parseInt(operationMode);
        if (mode == 3) return false; //全收费
        if (mode == 0) return true; //全免费
        return !(mode == type); //相等为收费
    }

    public static String token() {
        return Hawk.get(USER_TOKEN, "0000");
    }

    public static String mark() {
        return Hawk.get(MARK_CODE, "");
    }
}