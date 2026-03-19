package com.lvdoui6.android.tv.lvdou;

import android.util.Base64;
import android.util.Log;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.BuildConfig;
import com.lvdoui6.android.tv.Constant;
import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.api.config.LiveConfig;
import com.lvdoui6.android.tv.bean.Config;
import com.lvdoui6.android.tv.bean.Depot;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.lvdou.bean.Demo;
import com.lvdoui6.android.tv.lvdou.impl.AdmCallback;
import com.lvdoui6.android.tv.utils.FileUtil;
import com.lvdoui6.android.tv.utils.Notify;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Path;
import com.github.catvod.utils.Prefers;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;

import org.eclipse.jetty.util.ajax.JSON;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.jvm.internal.Intrinsics;

public class HawkAdm {

    private AdmCallback callback;
    private final String TAG = "HawkAdm";
    public static final String APP_URL = "app_url"; //APP对接地址
    public static final String ADM_URL = "adm_url"; //缓存的后台地址
    public static final String ADM_URLS = "app_urls"; //缓存的后台地址集
    public static final String ADM_CONFIG = "adm_config"; //缓存的后台配置

    private static class Loader {
        static volatile HawkAdm INSTANCE = new HawkAdm();
    }

    public static HawkAdm get() {
        return Loader.INSTANCE;
    }

    public void load(AdmCallback callback) {
        this.callback = callback;
        if (!Hawk.contains(ADM_URL)) loadAppUrl(); //没有缓存的后台地址，通过APP_URL请求
        else checkAdmUrl(Hawk.get(ADM_URL, ""), -1, 10); //有后台地址，使用后台地址请求
    }

    private void loadAppUrl() { //通过APP_URL获得配置
        String url = Demo.getInstance().getApp_api();
        OkGo.<String>get(url.endsWith(".json") || url.contains("gitee.com") ? url : url  + "/" + HawkConfig.API_MAIN)
                .tag(this)
                .params(getParams())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        if (body == null || body.isEmpty()) {
                            body = getUrls(); //使用缓存的urls
                            if (body.equals("{}")) {
                                callback.error("400-对接地址返回空数据");
                                return;
                            }
                        }
                        checkConfig(body, url);
                    }

                    @Override
                    public void onError(Response<String> error) {
                        String body = getUrls(); //使用缓存的urls
                        if (body.equals("{}")) {
                            callback.error("401-请求对接地址失败:" + error.body());
                            return;
                        }
                        checkConfig(body, url);
                    }
                });
    }

    private void checkAdmUrl(String url, int index, int size) { //后台地址、当前位置、数据长度
        OkGo.<String>get(url + "/" + HawkConfig.API_MAIN)
                .tag(this)
                .params(getParams())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        if (parseAdmConfig(body, url)) {
                            Notify.show(getUrlName(index));
                        } else if (index != -1) {
                            nextRoute(index, size, body);
                        }
                    }

                    @Override
                    public void onError(Response<String> error) {
                        nextRoute(index, size, error.body());
                    }
                });
    }

    private void checkConfig(String body, String url) {
        if (Json.valid(body)) { //如果是json格式的urls列表
            callback.message("正在解析json配置");
            setUrls(body);
            checkUrls(0);
            return;
        }

        if (body.contains("lvdou-") && body.contains("-lvdou")) { //如果是网页格式的urls列表
            callback.message("正在解析web配置");
            Pattern pattern = Pattern.compile("lvdou-(.*?)-lvdou");
            Matcher matcher = pattern.matcher(body);
            boolean success = false;
            String urls = "";
            while (matcher.find()) {
                urls = matcher.group(1);
                urls = Utils.decodeBase64(urls);
                if (urls != null && urls.contains("urls")) {
                    if (Json.valid(urls)) {
                        success = true;
                        break;
                    }
                }
            }
            if (success) {
                setUrls(urls);
                checkUrls(0);
            } else callback.error("402-选择线路失败【URLS配置错误】");
            return;
        }

        if (!parseAdmConfig(body, url)) {
            callback.error("403-连接服务器失败" + body);
        }
    }

    private boolean parseAdmConfig(String body, String url) {
        try {
            callback.message("正在解析配置");
            String config = Utils.dataDecryption(body, "基础配置", true);
            if (config != null && !config.isEmpty()) { //如果已经解密成功，就是直接对接的后台
                Adm admConfig = Adm.objectFromData(config);
                if (admConfig.getCode() == 1 && admConfig.getData() != null) {
                    initDepot(config);
                    initConfig(config);
                    setAbout(admConfig);
                    Hawk.put(ADM_URL, url);
                    Hawk.put(ADM_CONFIG, config);
                    callback.message("配置解析完成,请稍后...");
                    callback.success();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setAbout(Adm admConfig) {
        String about = admConfig.getData().getSiteConfig().getApp_config().getAbout();
        String texts = new String(Base64.decode(about, Base64.DEFAULT));
        if (about != null && !about.isEmpty() && Json.valid(texts)) {
            HawkCustom.get().addConfig(texts);
        } else {
            Hawk.put(HawkCustom.HAS_CONFIG, false);
        }
    }

    private void nextRoute(int index, int size, String msg) {
        if (index == -1) {
            loadAppUrl();
        } else {
            if (index < size - 1) {
                callback.message("正在尝试更换线路");
                App.post(() -> checkUrls(index + 1), Constant.INTERVAL_HIDE);
            } else if (index >= size - 1) {
                callback.error("404-连接服务器失败" + msg);
            }
        }
    }

    private void checkUrls(int index) {
        try {
            JSONObject urlsObject = new JSONObject(getUrls());
            JSONArray urlsArray = urlsObject.getJSONArray("urls");
            JSONObject urlsData = urlsArray.getJSONObject(index);
            checkAdmUrl(urlsData.getString("url"), index, urlsArray.length());
        } catch (JSONException e) {
            callback.error("405-线路选择失败【urls格式错误】");
            e.printStackTrace();
        }
    }

    private String getUrlName(int index) {
        String name = "";
        try {
            JSONObject urlsObject = new JSONObject(getUrls());
            JSONArray urlsArray = urlsObject.getJSONArray("urls");
            JSONObject urlsData = urlsArray.getJSONObject(index);
            name = "为您选择" + urlsData.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    private void initDepot(String config) {
        JsonObject object = Json.parse(config).getAsJsonObject();
        JsonObject mainData = App.gson().fromJson(object.get("data"), JsonObject.class);
        List<Depot> items = Depot.arrayFrom(mainData.getAsJsonArray("depotConfig").toString());
        List<Config> depotConfig = new ArrayList<>();
        for (Depot item : items) depotConfig.add(Config.find(item, 0));
        Config mConfig = depotConfig.get(0);
        Config.find(mConfig.getUrl(), mConfig.getName(), 1);
        LiveConfig.load(mConfig, null);
        List<Config> dbItems = Config.getAll(0);
        for (int i = 0; i < dbItems.size(); i++) {
            if (isDeleteDepot(dbItems.get(i).getUrl(), depotConfig)) {
                Config.delete(dbItems.get(i).getUrl());
            }
        }
    }

    private boolean isDeleteDepot(String url, List<Config> yConfig) {
        for (int i = 0; i < yConfig.size(); i++) {
            if (Objects.equals(yConfig.get(i).getUrl(), url)) {
                return false;
            }
        }
        return true;
    }

    private void initConfig(String config) {
        Adm object = Adm.objectFromData(config);
        Hawk.put(HawkConfig.COUNTY_KEY, object.getData().getSiteConfig().getQweather_key());
        int defaultPlayer = Integer.parseInt(object.getData().getSiteConfig().getDefault_player());
        String backdropImage = Utils.getAdminUrl(object.getData().getSiteConfig().getApp_config().getBackdropimage());
        if (Prefers.getInt("player", 888888) == 888888) Setting.putPlayer(defaultPlayer - 1);
        if (backdropImage.length() < 8) Hawk.delete(HawkConfig.API_BACKGROUND);
        else Hawk.put(HawkConfig.API_BACKGROUND, backdropImage);
        Hawk.put(HawkConfig.PICTURE_LOGO_IMG, Utils.getAdminUrl(object.getData().getSiteConfig().getApp_config().getLogoimage()));
        write(FileUtil.getWall(8888), Utils.getAdminUrl(object.getData().getSiteConfig().getApp_config().getSplashimage()));
        write(FileUtil.getWall(9999), Utils.getAdminUrl(object.getData().getSiteConfig().getApp_config().getPlayerimage()));
    }

    public void write(File file, String imgUrl) {
        if (!imgUrl.startsWith("http")) {
            file.delete();
        } else {
            App.execute(() -> {
                try {
                    Path.write(file, OkHttp.newCall(Utils.getAdminUrl(imgUrl)).execute().body().bytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private HttpParams getParams() {
        HttpParams mParams = new HttpParams();
        mParams.put("time", "time");
        mParams.put("token", HawkUser.token());
        mParams.put("app_id", Utils.getAppId());
        mParams.put("apk_mark", Utils.getAppMark());
        mParams.put("version", BuildConfig.VERSION_NAME);
        return mParams;
    }

    private void setUrls(String urls) {
        Hawk.put(ADM_URLS, urls);
    }

    private String getUrls() {
        return Hawk.get(ADM_URLS, "{}");
    }

    public static String getAdmConfig(String defaultValue) {
        return Hawk.get(ADM_CONFIG, defaultValue);
    }

    public static Adm loadAmdConfig(@Nullable String defaultValue) {
        Intrinsics.checkParameterIsNotNull(defaultValue, "defaultValue");
        String msg = getAdmConfig(defaultValue);
        return msg == null || ((CharSequence) msg).length() == 0 ? null : Adm.objectFromData(msg);
    }

    public static String getService() {
        Adm.DataBean.SiteConfigBean siteConfig = getSiteConfig();
        return siteConfig == null || siteConfig.getApp_config().getServiceimage().length() < 8 ? null : siteConfig.getApp_config().getServiceimage();
    }

    public static String getQqgroup() {
        Adm.DataBean.SiteConfigBean siteConfig = getSiteConfig();
        return siteConfig == null || siteConfig.getApp_config().getQqgroup().length() < 8 ? null : siteConfig.getApp_config().getQqgroup();
    }

    public static String hideParse() {
        return getSiteConfig().getDepot_parses_hide();
    }

    public static String hideSite() {
        return getSiteConfig().getDepot_site_hide();
    }

    public static Adm.DataBean.SiteConfigBean getSiteConfig() {
        Adm adm = loadAmdConfig("");
        return adm == null || adm.getCode() != 1 ? null : adm.getData().getSiteConfig();
    }

    public static List<Adm.DataBean.HomeConfigBean> getHomeConfig() {
        Adm adm = loadAmdConfig("");
        return adm == null || adm.getCode() != 1 ? null : adm.getData().getHomeConfig();
    }

    public static List<Adm.DataBean.NoticeListBean> getNoticeList() {
        Adm adm = loadAmdConfig("");
        return adm == null || adm.getCode() != 1 ? null : adm.getData().getNoticeList();
    }
}
