package com.lvdoui6.android.tv.lvdou;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.databinding.DialogPaymentBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Payment {

    private DialogPaymentBinding binding;
    private AlertDialog dialog;
    private Callback callback;

    private static class Loader {
        static volatile Payment INSTANCE = new Payment();
    }

    public static Payment get() {
        return Loader.INSTANCE;
    }

    public void start(Activity activity, Callback callback) {
        this.callback = callback;
        App.execute(() -> doInBackground(activity));
    }

    private void doInBackground(Activity activity) {
        App.post(() -> show(activity, "请选择支付方式", HawkAdm.loadAmdConfig("").getData().getSiteConfig().getService_qq()));
    }

    private void show(Activity activity, String tip, String desc) {
        binding = DialogPaymentBinding.inflate(LayoutInflater.from(activity));
        binding.version.setText(tip);
        binding.wechat.setOnClickListener(this::wechat);
        binding.alipay.setOnClickListener(this::alipay);
        binding.cancel.setOnClickListener(this::cancel);
        create(activity).show();
        binding.desc.setText(desc);
    }

    private AlertDialog create(Activity activity) {
        return dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).setCancelable(false).create();
    }

    private void wechat(View view) {
        callback.paymentMethod("wechat");
        dismiss();
    }

    private void alipay(View view) {
        callback.paymentMethod("alipay");
        dismiss();
    }

    private void cancel(View view) {
        callback.paymentMethod("cancel");
        dismiss();
    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    public interface Callback {
        void paymentMethod(String type);
    }
}
