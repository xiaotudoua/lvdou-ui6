package com.lvdoui6.android.tv.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.lvdoui6.android.tv.databinding.ActivityPushBinding;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.Utils;
import com.lvdoui6.android.tv.server.Server;
import com.lvdoui6.android.tv.ui.base.BaseActivity;
import com.lvdoui6.android.tv.utils.ImgUtil;
import com.lvdoui6.android.tv.utils.QRCode;
import com.lvdoui6.android.tv.utils.Sniffer;
import com.lvdoui6.android.tv.utils.Util;

public class ServiceActivity extends BaseActivity {

    private ActivityPushBinding mBinding;

    public static void start(Activity activity) {
        start(activity, 2);
    }

    public static void start(Activity activity, int tab) {
        Intent intent = new Intent(new Intent(activity, ServiceActivity.class));
        intent.putExtra("tab", tab);
        activity.startActivity(intent);
    }

    private int getTab() {
        return getIntent().getIntExtra("tab", 2);
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityPushBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.clip.setVisibility(View.GONE);
        mBinding.clipGroup.setVisibility(View.GONE);

        ImgUtil.rect("客服", Utils.getAdminUrl(HawkAdm.getService()), mBinding.code);
        mBinding.info.setText("扫码联系客服");

        Log.d("TAG", "QQ群: " + HawkAdm.getQqgroup());
        mBinding.codeGroup.setImageBitmap(QRCode.getBitmap(HawkAdm.getQqgroup(), 250, 1));
        mBinding.group.setVisibility(View.VISIBLE);
        mBinding.infoGroup.setText("扫码加入Q群");
    }

    @Override
    protected void initEvent() {
        mBinding.code.setOnClickListener(this::onCode);
        mBinding.clip.setOnClickListener(this::onClip);
    }

    private void onClip(View view) {
        CharSequence text = Util.getClipText();
        if (!TextUtils.isEmpty(text)) VideoActivity.start(this, Sniffer.getUrl(text.toString()), false);
    }

    private void onCode(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Server.get().getAddress(getTab())));
        startActivity(intent);
    }
}
