package com.lvdoui6.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.api.config.VodConfig;
import com.lvdoui6.android.tv.bean.Config;
import com.lvdoui6.android.tv.databinding.ActivitySettingNavBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.impl.DohCallback;
import com.lvdoui6.android.tv.impl.ProxyCallback;
import com.lvdoui6.android.tv.player.ExoUtil;
import com.lvdoui6.android.tv.ui.base.BaseActivity;
import com.lvdoui6.android.tv.ui.dialog.DohDialog;
import com.lvdoui6.android.tv.ui.dialog.ProxyDialog;
import com.lvdoui6.android.tv.utils.FileUtil;
import com.lvdoui6.android.tv.utils.Notify;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.lvdoui6.android.tv.utils.UrlUtil;
import com.github.catvod.bean.Doh;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Shell;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SettingNavActivity extends BaseActivity implements DohCallback, ProxyCallback {

    private ActivitySettingNavBinding mBinding;
    private String[] configCache;
    private int type;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingNavActivity.class));
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivitySettingNavBinding.inflate(getLayoutInflater());
    }

    private int getDohIndex() {
        return Math.max(0, VodConfig.get().getDoh().indexOf(Doh.objectFrom(Setting.getDoh())));
    }

    @Override
    protected void initView() {
        focusChangeListener();
        mBinding.dohText.setText(getDohList()[getDohIndex()]);
        mBinding.configCacheText.setText((configCache = ResUtil.getStringArray(R.array.select_config_cache))[Setting.getConfigCache()]);
    }

    @Override
    protected void initEvent(){
        mBinding.api.setOnClickListener(v-> SettingActivity.start(this));
        mBinding.player.setOnClickListener(v-> SettingPlayerActivity.start(this));
        mBinding.danmu.setOnClickListener(v-> SettingDanmuActivity.start(this));
        mBinding.custom.setOnClickListener(v-> SettingCustomActivity.start(this));
        mBinding.configCache.setOnClickListener(this::setConfigCache);
        mBinding.proxy.setOnClickListener(this::onProxy);
        mBinding.reset.setOnClickListener(this::onReset);
        mBinding.doh.setOnClickListener(this::setDoh);
    }

    private void focusChangeListener(){
        mBinding.proxy.setOnFocusChangeListener((v, F) -> {
            mBinding.proxyText.setVisibility(F ? View.VISIBLE : View.GONE);
        });
        mBinding.doh.setOnFocusChangeListener((v, F) -> {
            mBinding.dohText.setVisibility(F ? View.VISIBLE : View.GONE);
        });
        mBinding.configCache.setOnFocusChangeListener((v, F) -> {
            mBinding.configCacheText.setVisibility(F ? View.VISIBLE : View.GONE);
        });
    }

    private void onProxy(View view) {
        ProxyDialog.create(this).show();
    }

    private String[] getDohList() {
        List<String> list = new ArrayList<>();
        for (Doh item : VodConfig.get().getDoh()) list.add(item.getName());
        return list.toArray(new String[0]);
    }

    private void setDoh(View view) {
        DohDialog.create(this).index(getDohIndex()).show();
    }

    @Override
    public void setProxy(String proxy) {
        ExoUtil.reset();
        Setting.putProxy(proxy);
        OkHttp.get().setProxy(proxy);
        Notify.progress(getActivity());
        VodConfig.load(Config.vod(), getCallback());
        mBinding.proxyText.setText(UrlUtil.scheme(proxy));
    }

    @Override
    public void setDoh(Doh doh) {
        OkHttp.get().setDoh(doh);
        Notify.progress(getActivity());
        Setting.putDoh(doh.toString());
        mBinding.dohText.setText(doh.getName());
        VodConfig.load(Config.vod(), getCallback());
    }

    private void setConfigCache(View view) {
        int index = Setting.getConfigCache();
        Setting.putConfigCache(index = index == configCache.length - 1 ? 0 : ++index);
        mBinding.configCacheText.setText(configCache[index]);
    }

    private void onReset(View view) {
        new MaterialAlertDialogBuilder(this).setTitle(R.string.dialog_reset_app).setMessage(R.string.dialog_reset_app_data).setNegativeButton(R.string.dialog_negative, null).setPositiveButton(R.string.dialog_positive, (dialog, which) -> reset()).show();
    }

    private void reset() {
        Notify.progress(this);
        List<Config> mItem = Config.getAll(0);
        if (mItem.size() > 0){
            for (int i = 0; i < mItem.size(); i++) {
                Config.delete(mItem.get(i).getUrl(), mItem.get(i).getType());
            }
        }
        List<Config> mItems = Config.getAll(1);
        if (mItems.size() > 0){
            for (int i = 0; i < mItems.size(); i++) {
                Config.delete(mItems.get(i).getUrl(), mItems.get(i).getType());
            }
        }
        new Handler().postDelayed(() -> {
            Notify.dismiss();
            Shell.exec("pm clear " + App.get().getPackageName());
        }, 2000); // 2000 milliseconds delay
    }

    private Callback getCallback() {
        return new Callback() {
            @Override
            public void success() {
                setConfig();
            }

            @Override
            public void error(String msg) {
                Notify.show(msg);
                setConfig();
            }
        };
    }

    private void setCacheText() {
        FileUtil.getCacheSize(new Callback() {
            @Override
            public void success(String result) {
//                mBinding.cacheText.setText(result);
            }
        });
    }

    private void setConfig() {
        switch (type) {
            case 0:
                setCacheText();
                Notify.dismiss();
                RefreshEvent.history();
                RefreshEvent.video();
                break;
            case 1:
                setCacheText();
                Notify.dismiss();
                break;
            case 2:
                setCacheText();
                Notify.dismiss();
                break;
        }
    }
}
