package com.lvdoui6.android.tv.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.databinding.DialogLoginBinding;
import com.lvdoui6.android.tv.event.ServerEvent;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.lvdou.AdmUtils;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.lvdou.bean.AdmUser;
import com.lvdoui6.android.tv.server.Server;
import com.lvdoui6.android.tv.utils.Notify;
import com.lvdoui6.android.tv.utils.QRCode;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class LoginDialog extends Dialog implements DialogInterface.OnDismissListener {

    private int type;
    private boolean finish;
    private AdmUser admUser;
    private final Callback callback;
    private final AlertDialog dialog;
    private final DialogLoginBinding binding;

    public static LoginDialog create(FragmentActivity activity, Callback callback) {
        return new LoginDialog(activity, callback);
    }

    public LoginDialog(FragmentActivity activity, Callback callback) {
        super(activity);
        this.callback = callback;
        this.binding = DialogLoginBinding.inflate(LayoutInflater.from(activity));
        this.dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).create();
    }

    public LoginDialog action(int type, boolean finish) {
        this.type = type;
        this.finish = finish;
        return this;
    }

    public void show() {
        initDialog();
        initEvent();
        initView();
    }

    private void initDialog() {
        WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        params.width = (int) (ResUtil.getScreenWidth() * 0.55f);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setDimAmount(0);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        binding.userText.setText(Hawk.get(HawkUser.USER_ACCOUNT, ""));
        binding.code.setImageBitmap(QRCode.getBitmap(Server.get().getAddress(5), 200, 0));
    }

    private void initEvent() {
        binding.positive.setOnClickListener(v -> {
            String user = binding.userText.getText().toString();
            String pass = binding.passwdText.getText().toString();
            if (user.length() < 5 || pass.length() < 6) {
                Notify.show("账号/密码不能小于6位");
                return;
            }
            login(user + "|" + pass);
        });
        binding.negative.setOnClickListener(v -> {
            String user = binding.userText.getText().toString();
            String pass = binding.passwdText.getText().toString();
            if (user.length() < 5 || pass.length() < 6) {
                Notify.show("账号/密码不能小于6位");
                return;
            }
            register(user + "|" + pass);
        });
    }

    private void login(String text) {
        if (text.contains("|")){
            new AdmUtils().logon(text, new Callback() {
                @Override
                public void success(String body) {
                    admUser = AdmUser.objectFromData(body);
                    if (admUser == null || admUser.getCode() != 1) callback.error(body);
                    else {
                        HawkUser.saveUser(admUser);
                        callback.success(admUser.getData().getUserinfo().getToken());
                        dialog.dismiss();
                    }
                }

                @Override
                public void error(String error) {
                    callback.error(error);
                }
            });
        } else {
            Hawk.put(HawkUser.USER_TOKEN, text);
            new AdmUtils().index(new Callback() {
                @Override
                public void success(String body) {
                    admUser = AdmUser.objectFromData(body);
                    if (admUser == null || admUser.getCode() != 1) {
                        Hawk.delete(HawkUser.USER_TOKEN);
                        callback.error(body);
                    } else {
                        HawkUser.saveUser(admUser);
                        callback.success(admUser.getData().getUserinfo().getToken());
                        dialog.dismiss();
                    }
                }

                @Override
                public void error(String error) {
                    callback.error(error);
                }
            });
        }
    }

    private void register(String text) {
        new AdmUtils().register(text, new Callback() {
            @Override
            public void success(String body) {
                admUser = AdmUser.objectFromData(body);
                if (admUser == null || admUser.getCode() != 1) callback.error(body);
                else {
                    HawkUser.saveUser(admUser);
                    callback.success(admUser.getData().getUserinfo().getToken());
                    dialog.dismiss();
                }
            }

            @Override
            public void error(String error) {
                callback.error(error);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerEvent(ServerEvent event) {
        Log.d("TAG", "onServerEvent: ");
        if (Objects.requireNonNull(event.getType()) == ServerEvent.Type.LOGON) {
            if (event.getText().equals("token")) {
                login(event.getName().replaceAll("token_", ""));
            } else {
                login(event.getName() + "|" +event.getText());
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        EventBus.getDefault().unregister(this);
        if (!finish) App.activity().finish();
    }
}
