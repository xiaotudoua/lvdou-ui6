package com.lvdoui6.android.tv.lvdou;

import com.orhanobut.hawk.Hawk;

/**
 * @author jkpan
 * @date :2024/03/01
 * @description: Hawk
 */
public class HawkConfig {

    //基础配置
    public static final String APP_CONFIG = "app_config";
    public static final String APP_B = "app_b"; // app_id
    public static final String APP_C = "app_c"; // app_key
    public static final String APP_D = "app_d"; // app_mark
    public static final String APP_E = "app_e"; // 授权级别
    public static final String APP_F = "app_s"; // abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-:./
    public static final String APP_G = "app_g"; // 首次启动
    public static final String APP_H = "app_h"; // 是否自动换背景
    public static final String COUNTY_CITY= "city";
    public static final String COUNTY_COUNTY = "county";
    public static final String COUNTY_PROVINCE = "province";
    public static final String COUNTY_KEY = "county_key";
    public static final String COUNTY_NAME = "county_name";
    public static final String COUNTY_CODE = "county_code";
    public static final String ENCRYPTION_FEATURES = "https://baidu.con/"; //加密特征
    public static final String PICTURE_LOGO_IMG = "picture_logo_img"; //全局logo
    public static final String API_BACKGROUND  = "api_background"; //全局背景接口
    public static final String HOME_VOD_IMG_M = "home_vod_img_m"; //首页数据第一张图片
    // 开关
    public static final String AUTO_BACKGROUND  = "auto_background"; //自动背景
    public static final String HISTORY_TIP  = "history_tip"; //提示

    // API接口
    public static final String API_MAIN = "api/main/init"; //基础配置
    public static final String API_USER_REG = "api/users/register"; //用户注册
    public static final String API_USER_LOGIN = "api/users/login"; //用户登录
    public static final String API_USER_INDEX = "api/users/index"; //用户信息
    public static final String API_USER_LOGOUT = "api/users/logout"; //退出登录
    public static final String API_TOKEN_CHECK = "api/token/check"; //检测Token是否过期
    public static final String API_TOKEN_REFRESH = "api/token/refresh"; //刷新token
    public static final String API_USER_IS_SIGN = "api/Sign/isSign"; //是否签到
    public static final String API_USER_SIGN_IN = "api/Sign/init"; //立即签到
    public static final String API_INVITE_COUNT = "api/invite/index"; //邀请人数
    public static final String API_GROUP_INDEX = "api/group/index"; //获取会员组列表
    public static final String API_BALANCE_UP_MEMBER_GROUP = "api/group/upgradeGroup"; //余额升级用户组
    public static final String API_SCORE_UP_MEMBER_GROUP = "api/group/scoreUpgradeGroup"; //积分升级用户组
    public static final String API_CAMI_UP_MEMBER_GROUP = "api/Card/index"; //卡密升级会员
    public static final String WEB_CREATE_ORDER = "index/Recharge/submit"; //通过网页创建订单并支付
    public static String JSON_API_WEATHER_DEV = "https://devapi.qweather.com/v7/weather/3d"; //和风天气Api免费接口
    public static String JSON_API_WEATHER = "https://api.qweather.com/v7/weather/3d"; //和风天气Api标准接口
    public static String JSON_API_LOOKUP = "https://geoapi.qweather.com/v2/city/lookup"; //和风天气、获取地理位置
    public static String WEATHER_CACHE = "weather_cache"; //和风天气缓存

    public static boolean wallAuto() {
        if (!Hawk.contains(API_BACKGROUND)) return false;
        if (!Hawk.get(API_BACKGROUND, "").endsWith("auto=true")) return false;
        return Hawk.get(APP_H, false);
    }
}