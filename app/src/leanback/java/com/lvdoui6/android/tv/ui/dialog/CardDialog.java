package com.lvdoui6.android.tv.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.lvdoui6.android.tv.databinding.DialogCardBinding;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.lvdou.AdmUtils;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.lvdou.Utils;
import com.lvdoui6.android.tv.utils.Notify;
import com.lvdoui6.android.tv.utils.QRCode;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * @author fongmi
 * @date :2022/09/23
 * @description:
 */

public class CardDialog implements DialogInterface.OnDismissListener {

    private static final String TAG = "DialogCardBinding";
    private final StringBuilder stringBuilder = new StringBuilder();
    private DialogCardBinding binding;
    private final AlertDialog dialog;
    private final Callback callback;

    public static CardDialog create(Activity activity, Callback callback) {
        return new CardDialog(activity, callback);
    }

    public CardDialog(Activity activity, Callback callback) {
        this.callback = callback;
        this.binding = DialogCardBinding.inflate(LayoutInflater.from(activity));
        this.dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).create();
    }

    public void show() {
        dialog.setOnDismissListener(this);
        setDialog();
        initEv();
    }

    private void initEv() {
        binding.delete.setOnClickListener(v -> delete());
        binding.cardSend.setOnClickListener(v -> cardSend());
        binding.btnNumber0.setOnClickListener(this::numberClick);
        binding.btnNumber1.setOnClickListener(this::numberClick);
        binding.btnNumber2.setOnClickListener(this::numberClick);
        binding.btnNumber3.setOnClickListener(this::numberClick);
        binding.btnNumber4.setOnClickListener(this::numberClick);
        binding.btnNumber5.setOnClickListener(this::numberClick);
        binding.btnNumber6.setOnClickListener(this::numberClick);
        binding.btnNumber7.setOnClickListener(this::numberClick);
        binding.btnNumber8.setOnClickListener(this::numberClick);
        binding.btnNumber9.setOnClickListener(this::numberClick);
    }

    private void setDialog() {
        WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        params.width = ResUtil.getScreenWidth();
        params.height = ResUtil.getScreenHeight();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setDimAmount(0.2f);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        binding.code.setImageBitmap(QRCode.getBitmap(Utils.getAdminUrl("/uploads/tvbox/web/card.html"), 150, 1));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        callback.error("被关闭");
    }

    private void delete() {
        if (stringBuilder.length() > 0) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        readyExchange();
    }

    public void numberClick(View view) {
        stringBuilder.append(view.getTag());
        readyExchange();
    }

    private void readyExchange() {
        String str = stringBuilder.toString();
        binding.cardEditText.setText(str);
    }

    private void cardSend() {
        final String key = binding.cardEditText.getText().toString();
        if (key.isEmpty()) {
            Notify.show("兑换码不能为空~");
        } else {
            if (HawkUser.checkLogin()) {
                recHarGe(key);
            } else {
                Notify.show("请登录后操作~");
            }
        }
    }

    private void recHarGe(String card) {
        new AdmUtils().camiRecharge(card, new Callback() {
            @Override
            public void success(String body) {
                if (body != null && !TextUtils.isEmpty(body)) {
                    try {
                        JSONObject jo = new JSONObject(body);
                        if (jo.getInt("code") == 1){
                            dialog.dismiss();
                            callback.success();
                        }
                        Notify.show(jo.getString("msg"));
                    } catch (JSONException e) {
                        Notify.show(e.toString().length() > 1 ? e.toString() : "请求失败");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void error(String error) {
                Notify.show(error);
            }
        });
    }
}

