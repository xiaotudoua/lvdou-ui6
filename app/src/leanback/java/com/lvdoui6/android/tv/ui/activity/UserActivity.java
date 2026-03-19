package com.lvdoui6.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.BuildConfig;
import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.Updater;
import com.lvdoui6.android.tv.databinding.ActivityUserBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.lvdou.AdmUtils;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.HawkCustom;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.lvdou.Utils;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.lvdou.bean.AdmUser;
import com.lvdoui6.android.tv.lvdou.impl.MallCallback;
import com.lvdoui6.android.tv.ui.base.BaseActivity;
import com.lvdoui6.android.tv.ui.dialog.DescDialog;
import com.lvdoui6.android.tv.ui.dialog.InfoDialog;
import com.lvdoui6.android.tv.ui.dialog.LoginDialog;
import com.lvdoui6.android.tv.ui.dialog.MallDialog;
import com.lvdoui6.android.tv.utils.ImgUtil;
import com.lvdoui6.android.tv.utils.Notify;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends BaseActivity implements MallCallback {

    private AdmUser.DataBean.UserinfoBean userInfo;
    private ActivityUserBinding mBinding;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, UserActivity.class));
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityUserBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        index();
        setRollingInfo();
        mBinding.version.setText(BuildConfig.VERSION_NAME);
        boolean word = HawkCustom.get().getConfig("user_word", false);
        mBinding.word.setVisibility(word ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initEvent() {
        mBinding.mall.setOnClickListener(v -> getMall());
        mBinding.user.setOnClickListener(this::userInfo);
        mBinding.user.setOnLongClickListener(this::logout);
        mBinding.keep.setOnClickListener(v -> KeepActivity.start(getActivity()));
        mBinding.history.setOnClickListener(v -> HistoryActivity.start(getActivity()));
        mBinding.setting.setOnClickListener(v -> SettingNavActivity.start(getActivity()));
        mBinding.as.setOnClickListener(v -> WebActivity.start(getActivity(), Utils.getApi("uploads/tvbox/web/payrule.html")));
        mBinding.bs.setOnClickListener(v -> WebActivity.start(getActivity(), Utils.getApi("uploads/tvbox/web/paytutorial.html")));
        mBinding.cs.setOnClickListener(v -> WebActivity.start(getActivity(), Utils.getApi("uploads/tvbox/web/payproblem.html")));
        mBinding.about.setOnClickListener(v -> DescDialog.show(this, HawkCustom.get().getConfig("about", "获取失败")));
        mBinding.version.setOnClickListener(v -> Updater.get().force().release().start(this));
    }

    private void index() {
        new AdmUtils().index(new Callback() {
            @Override
            public void success(String result) {
                AdmUser userData = AdmUser.objectFromData(result);
                if (userData != null && userData.getCode() == 1 && userData.getData() != null){
                    HawkUser.saveUser(userData);
                }
                initData();
            }

            @Override
            public void error(String msg) {
                Log.d("TAG", "token有效: " + msg);
            }
        });
    }

    private void initData() {
        userInfo = HawkUser.userInfo();
        if (userInfo != null) {
            mBinding.score.setText(getString(R.string.user_score, userInfo.getScore()));
            mBinding.money.setText(getString(R.string.user_money, userInfo.getMoney()));
            long vipTime = userInfo.getVipendtime();
            if (vipTime == 88888888){
                mBinding.vipEndTime.setText("尊贵的永久会员");
                mBinding.vipEndTime.setTextColor(getResources().getColor(R.color.accent));
            } else {
                mBinding.vipEndTime.setText(getString(R.string.user_vip_time, Utils.stampToDate(vipTime * 1000)));
            }
            ImgUtil.rect(userInfo.getNickname(), Utils.getAdminUrl(userInfo.getAvatar()), mBinding.avatar);
            setUserInfo(true);
        } else {
            setUserInfo(false);
        }
    }

    private void setUserInfo(boolean isLogin) {
        mBinding.name.setText(isLogin ? userInfo.getNickname() : "未登录");
        mBinding.score.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);
        mBinding.money.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);
        mBinding.vipEndTime.setVisibility(isLogin ? View.VISIBLE : View.INVISIBLE);
    }

    private void mall(String body) {
        MallDialog.create(this, userInfo.getMoney(), body).index(1).show();
    }

    private void getMall() {
        if (!HawkUser.checkLogin()){
            Notify.show("请先登录");
            login();
            return;
        }
        if (userInfo.getVipendtime() == 88888888){
            Notify.show("您已是永久会员");
            return;
        }
        Notify.progress(getActivity());
        App.execute(() -> {
            new AdmUtils().getMall(new Callback() {

                @Override
                public void success(String body) {
                    App.post(Notify::dismiss);
                    if (body != null) mall(body);
                    else Notify.show("获取套餐列表为空");
                }

                @Override
                public void error(String error) {
                    App.post(Notify::dismiss);
                    Notify.show("获取套餐列表失败" + error);
                }
            });
        });
    }

    private void userInfo(View view) {
        if (!HawkUser.checkLogin()) {
            login();
        } else {
            Notify.show("您已登录,长按可退出登录");
        }
    }

    private boolean logout(View view) {
        if (!HawkUser.checkLogin()) {
            login();
        } else {
            new AdmUtils().logout(new Callback() {
                @Override
                public void success(String body) {
                    HawkUser.saveUser(null);
                    Notify.show(body);
                    initData();
                }

                @Override
                public void error(String error) {
                    Notify.show(error);
                }
            });
        }
        return true;
    }

    private void login() {
        LoginDialog.create(this, new Callback() {
            @Override
            public void success(String result) {
                Log.d("TAG", "success: " + result);
                Notify.show("登录成功");
                initData();
            }

            @Override
            public void error(String msg) {
                Log.d("TAG", "error: " + msg);
                Notify.show(msg);
            }
        }).action(1, true).show();
    }

    private void setRollingInfo() {
        List<Adm.DataBean.NoticeListBean> noticeList = HawkAdm.getNoticeList();
        if (noticeList != null && noticeList.size() > 0){
            List<String> info = new ArrayList<>();
            for (int i = 0; i < noticeList.size(); i++) {
                info.add(noticeList.get(i).getTitle());
            }
            mBinding.info.startWithList(info);
            mBinding.info.startWithList(info);
            mBinding.llInfo.setOnClickListener(v -> {
                int noticeId = mBinding.info.getPosition();
                InfoDialog.create(this).index(noticeId).show();
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        super.onRefreshEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefreshEvent.history();
    }

    @Override
    public void payEvent(int code) {
        if (code == 2) index();
        Log.d("TAG", "payEvent: " + code);
    }
}
