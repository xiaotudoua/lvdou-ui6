package com.lvdoui6.android.tv.lvdou;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.api.Decoder;
import com.lvdoui6.android.tv.lvdou.bean.Demo;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lvdou
 * &#064;date  :2023/4/6
 * &#064;description:  扩展
 */
public class Utils {

    private static final String TAG = "App";

    public static void init(Context context, String config) {
        Setting.putSize(0);
        Setting.putHomeButtons("");
        Hawk.init(context).build();
        Hawk.put(HawkConfig.APP_E, "true");
        Hawk.put(HawkConfig.APP_CONFIG, config);
        StatService.init(context, Demo.getInstance().getMtj_key(), Demo.getInstance().getMtj_channel());
        StatService.setAuthorizedState(context, true);
        StatService.setForTv(context, true);
        StatService.start(context);
    }

    public static String getApi(String api) {
        String url = Hawk.get(HawkAdm.ADM_URL, "");
        if (isWifiProxy() || isVpnUsed()) return null;
        if (TextUtils.isEmpty(url)) return null;
        if (api.startsWith("http")) return api;
        return url + "/" + api;
    }

    public static String getAdminUrl(String url) {
        if (TextUtils.isEmpty(url)) { //链接为空直接返回
            return url;
        } else if (url.startsWith("/")) { //以/开头拼接拼接域名返回
            url = Hawk.get(HawkAdm.ADM_URL, "") + url;
        } else if (url.startsWith("//")) { //以//开头拼接http:返回
            url = "http:" + url;
        } else if (!url.startsWith("http") && !url.startsWith("/")) { //不是http开头也不是/开头
            url = Hawk.get(HawkAdm.ADM_URL, "") + "/" + url;
        }
        return url;
    }

    /**
     * 数据解密
     *
     * @param response 待解密数据
     * @param memo 备注
     * @param force 是否需要强制解密，false 如果是明文不解密，true，如果是明文制空数据
     */
    public static String dataDecryption(String response, String memo, boolean force) {
        Log.d(TAG, "dataDecryption: " + response);
        if (TextUtils.isEmpty(response)) return response;
        Log.d(TAG, memo + "-" + force + ": " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.getString("msg");
            int code = jsonObject.getInt("code");
            if (code != 1 || msg.equals("退出成功") || msg.equals("热搜词库")) return response;
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        response = response.replaceAll("\"", "");
        if (response.length() > 16){
            String iKey = response.substring(0, 16);
            response = response.substring(16);
            response = Decoder.cbc(response, iKey, iKey);
        }
        Log.d(TAG, memo + "-解密结果:" + response);
        return response;
    }

    //视频地址解密
    public static String videoAddressDecrypt(String url) {
        Log.d("原视频播放地址: ", url);
        if (url.contains(HawkConfig.ENCRYPTION_FEATURES)) { //如果包含干扰字符
            url = url.replace(HawkConfig.ENCRYPTION_FEATURES, "");
            url = Utils.dataDecryption(url, "新视频播放地址: ", false);
        }
        return url;
    }

    //视频URL解密
    public static String vodUrlDecrypt(String url) {
        if (url.startsWith("lvDou+")) {
            url = url.replace("lvDou+", "");
            url = Utils.dataDecryption(url, "新视频播放地址: ", false);
        } else if (url.startsWith("lvdou+")) {
            String vodUrlKey = HawkAdm.getSiteConfig().getMaccms_key();
            if (vodUrlKey.length() >= 32) {
                url = url.replace("lvdou+", "");
                if (!vodUrlKey.contains("|")) {
                    vodUrlKey = vodUrlKey.substring(0, 16) + "|" + vodUrlKey.substring(vodUrlKey.length() - 16);
                }
                String[] values = vodUrlKey.split("\\|");
                url = Decoder.cbc(url, values[0], values[1]);
                Log.d(TAG, "新视频播放地址: " + url);
            }
        }
        return url;
    }

    //线路重命名
    public static String setFlagName(String flag) {
        String nameTable = HawkAdm.getSiteConfig().getResource_renaming();
        if (nameTable != null && !nameTable.isEmpty()) {
            if (nameTable.contains("|")) { // 存在多个
                String[] nameTableArr = nameTable.split("\\|");
                for (String s : nameTableArr) {
                    String[] newFlag = s.split("=>");
                    if (flag.contains(newFlag[0])) return newFlag[1];
                }
            } else { // 只有一个
                String[] a = nameTable.split("=>");
                if (flag.contains(a[0])) return a[1];
            }
        }
        return flag;
    }

    /**
     * 屏蔽视频
     *
     * @param name 名称
     */
    public static boolean isBlockVideo(String name) {
        Pattern pattern = Pattern.compile("\\[(\\d+(\\.\\d+)?)MB\\]");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String sizeStr = matcher.group(1);
            if (sizeStr != null) {
                double size = Double.parseDouble(sizeStr);
                return size < 10;
            }
        }
        return false;
    }

    /**
     * 移除匹配站点/解析,匹配到返回true
     *
     * @param resource   待匹配文本
     * @param FilterList 用于匹配的列表
     */
    public static boolean isRemove(String resource, String FilterList) {
        if (Objects.equals(FilterList, "All")
                || Objects.equals(FilterList, "Demo")
                || Objects.equals(FilterList, "Web")
                || Objects.equals(FilterList, "Sequence")
                || Objects.equals(FilterList, "Parallel")) return true;
        if (FilterList.contains("|")) {
            String[] FilterArr = FilterList.split("\\|");
            for (String s : FilterArr) {
                if (resource.contains(s)) {
                    //匹配到排除规则
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 移除匹配站点/解析,匹配到返回true
     *
     * @param flag   待匹配文本
     */
    public static boolean isParse(String flag) {
        String flagList = HawkCustom.get().getConfig("flag", "lvdoui.net");
        if (flagList.contains(",")) {
            String[] FlagArr = flagList.split(",");
            for (String s : FlagArr) {
                if (flag.contains(s)) {
                    return true;
                }
            }
        }
        return flagList.contains(flag);
    }

    /**
     * 检测是否正在使用代理
     */
    public static boolean isWifiProxy() {
        try {
            int proxyPort;
            String proxyAddress;
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检测是否正在使用VPN，如果在使用返回true,反之返回flase
     */
    public static boolean isVpnUsed() {
        try {
            Enumeration<?> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (Object f : Collections.list(niList)) {
                    NetworkInterface intf = (NetworkInterface) f;
                    if (!intf.isUp() || intf.getInterfaceAddresses().isEmpty()) {
                        continue;
                    }
                    Log.d("-----", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String dataSign(String data) {
        data = getPaiXu(Demo.getInstance().getApp_key() + data);
        data = SHA256Hash(data);
        return data;
    }

    /**
     * 数字签名
     *
     * @param data 待加密数据
     **/
    public static String SHA256Hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexHash.append('0');
                hexHash.append(hex);
            }
            return hexHash.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对字符串排序
     *
     * @param string 待解排序字符串
     */
    private static String getPaiXu(String string) {
        char[] chars = string.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    /**
     * 对字符串排序(特定规则)
     *
     * @param inputKey 待解排序字符串
     */
    public static String reordering(String inputKey) {
        StringBuilder encryptedKey = new StringBuilder();
        int inputKeyLength = inputKey.length();
        int characterSetLength = Hawk.get(HawkConfig.APP_F, "").length();
        Map<Character, Integer> charIndexMap = new HashMap<>();
        for (int i = 0; i < characterSetLength; i++) {
            charIndexMap.put(Hawk.get(HawkConfig.APP_F, "").charAt(i), i);
        }
        for (int i = 0; i < inputKeyLength; i++) {
            char c = inputKey.charAt(i);
            Integer charIndex = charIndexMap.get(c);
            if (charIndex != null) {
                encryptedKey.append(charIndex);
            }
        }
        return encryptedKey.toString();
    }

    //安卓ID
    public static String getAndroidId() {
        String androidId = Settings.System.getString(App.activity().getContentResolver(), Settings.System.ANDROID_ID);
        if (androidId == null || androidId.isEmpty()) androidId = "test";
        return androidId;
    }

    //品牌
    public static String getAndroidBrand() {
        return Build.BRAND;
    }

    //型号
    public static String getAndroidModel() {
        return Build.MODEL;
    }

    public static String setRandom (int num) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int ascii = 97 + random.nextInt(26);
            char randomChar = (char) ascii;
            sb.append(randomChar);
        }
        return sb.toString();
    }



    public static String decodeBase64(String encodedData) {
        try {
            return new String(Base64.decode(encodedData, Base64.DEFAULT));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null; // 或者抛出异常，取决于你的需求
        }
    }

    public static String stampToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static String getAppId() {
        return Demo.getInstance().getApp_id().replace("demo", "");
    }

    public static String getAppMark() {
        return Demo.getInstance().getApp_mark().replace("demo", "");
    }
}