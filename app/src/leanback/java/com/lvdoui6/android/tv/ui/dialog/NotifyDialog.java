package com.lvdoui6.android.tv.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.lvdoui6.android.tv.databinding.DialogNotifyBinding;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.HawkCustom;
import com.lvdoui6.android.tv.lvdou.bean.Demo;
import com.lvdoui6.android.tv.utils.QRCode;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.orhanobut.hawk.Hawk;

public class NotifyDialog implements DialogInterface.OnDismissListener {

    private final DialogNotifyBinding binding;
    private final AlertDialog dialog;
    private final Callback callback;
    private Activity activity;

    public static NotifyDialog create(Activity activity, Callback callback) {
        return new NotifyDialog(activity, callback);
    }

    public interface Callback {
        void retry();

        void close();
    }

    public NotifyDialog(Activity activity, Callback callback) {
        this.callback = callback;
        this.activity = activity;
        this.binding = DialogNotifyBinding.inflate(LayoutInflater.from(activity));
        this.dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).create();
    }

    public void show() {
        initDialog();
        initView();
        initEvent();
    }

    private void initDialog() {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (ResUtil.getScreenWidth() * 0.55f);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setDimAmount(0);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void initView() {
        binding.code.setImageBitmap(QRCode.getBitmap(getReleaseUrl(), 200, 0));
        binding.title.setText("连接服务器失败~建议扫码或访问下面的地址查看发布页");
        binding.text.setText(getReleaseUrl());
    }

    private void initEvent() {
        binding.positive.setOnClickListener(this::onPositive);
        binding.negative.setOnClickListener(this::onNegative);
        binding.test.setOnClickListener(v -> activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getReleaseUrl()))));
    }

    private String getReleaseUrl() {
        String url = HawkCustom.get().getConfig("release_url", Demo.getInstance().getApp_api());
        if (url.contains("blob")) {
            url = url.split("blob")[0];
        }
        if (url.contains("raw")) {
            url = url.split("raw")[0];
        }
        return url;
    }

    private void onPositive(View view) {
        callback.retry();
        dialog.dismiss();
    }

    private void onNegative(View view) {
        callback.close();
        dialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }
}
