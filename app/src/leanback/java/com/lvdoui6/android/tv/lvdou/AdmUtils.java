package com.lvdoui6.android.tv.lvdou;

import com.lvdoui6.android.tv.impl.Callback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;

public class AdmUtils {

    /**
     * 自动登录
     * @param logon 是否为登录
     */

    public void autoRegister(Callback callback, boolean logon) {
        String mark = HawkUser.mark() + "|12345678";
        if (logon) logon(mark, callback);
        else register(mark, callback);
    }

    /**
     * 会员登录
     * @param text 用户名|密码
     */
    public void logon(String text, Callback callback) {
        String[] paramsAll = text.split("\\|");
        Map<String, String> params = new HashMap<>();
        params.put("account", paramsAll[0]);
        params.put("password", paramsAll[1]);
        Hawk.put(HawkUser.USER_ACCOUNT, paramsAll[0]);
        newCall(HawkConfig.API_USER_LOGIN, "", params, callback);
    }

    /**
     * 会员注册
     * @param text 用户名|密码
     */
    public void register(String text, Callback callback) {
        String[] paramsAll = text.split("\\|");
        Map<String, String> params = new HashMap<>();
        params.put("username", paramsAll[0]);
        params.put("password", paramsAll[1]);
        Hawk.put(HawkUser.USER_ACCOUNT, paramsAll[0]);
        newCall(HawkConfig.API_USER_REG, "", params, callback);
    }

    /**
     * 会员信息
     */
    public void index(Callback callback) {
        newCall(HawkConfig.API_USER_INDEX, HawkUser.token(), null, callback);
    }

    /**
     * 退出登录
     */
    public void logout(Callback callback) {
        newCall(HawkConfig.API_USER_LOGOUT, HawkUser.token(), null, callback);
    }

    /**
     * 是否签到
     */
    public void isSign(Callback callback) {
        newCall(HawkConfig.API_USER_IS_SIGN, HawkUser.token(), null, callback);
    }

    /**
     * 发起签到
     */
    public void toSign(Callback callback) {
        newCall(HawkConfig.API_USER_SIGN_IN, HawkUser.token(), null, callback);
    }

    /**
     * 邀请人数
     */
    public void invite(Callback callback) {
        newCall(HawkConfig.API_INVITE_COUNT, HawkUser.token(), null, callback);
    }

    /**
     * 获取会员组
     */
    public void getMall(Callback callback){
        newCall(HawkConfig.API_GROUP_INDEX, HawkUser.token(), null, callback);
    }

    /**
     * 余额升级会员组
     * @param groupId 会员组id
     */
    public void upgradeGroup(int groupId, Callback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(groupId));
        newCall(HawkConfig.API_BALANCE_UP_MEMBER_GROUP, HawkUser.token(), params, callback);
    }

    /**
     * 积分升级会员组
     * @param groupId 会员组id
     */
    public void scoreUpgradeGroup(int groupId, Callback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(groupId));
        newCall(HawkConfig.API_SCORE_UP_MEMBER_GROUP, HawkUser.token(), params, callback);
    }

    /**
     * 卡密充值
     * @param card 卡密
     */
    public void camiRecharge(String card, Callback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("card", card);
        newCall(HawkConfig.API_CAMI_UP_MEMBER_GROUP, HawkUser.token(), params, callback);
    }

    /**
     * 网络请求
     * **/
    public void newCall(String url, String token, Map<String, String> params, Callback callback){
        if (params == null) params = new HashMap<>();
        OkGo.<String>post(Utils.getApi(url))
                .tag(url)
                .params(params)
                .headers("token", token)
                .params("sign", Utils.dataSign(Utils.getAppId() + Utils.getAppMark()))
                .params("apk_mark", Utils.getAppMark())
                .params("app_id", Utils.getAppId())
                .params("mark", HawkUser.mark())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (callback != null) callback.success(getBody(response, url));
                    }

                    @Override
                    public void onError(Response<String> error) {
                        if (callback != null) callback.error(error.body());
                    }
                });
    }

    private String getBody(Response<String> body, String url){
        return Utils.dataDecryption(body.body(), url, true);
    }
}
