package com.lvdoui6.android.tv.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;

import androidx.appcompat.app.AlertDialog;

import com.lvdoui6.android.tv.databinding.DialogMallBinding;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.lvdou.AdmUtils;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.HawkConfig;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.lvdou.Payment;
import com.lvdoui6.android.tv.lvdou.Utils;
import com.lvdoui6.android.tv.lvdou.bean.AdmGroup;
import com.lvdoui6.android.tv.lvdou.impl.MallCallback;
import com.lvdoui6.android.tv.ui.adapter.MallAdapter;
import com.lvdoui6.android.tv.ui.custom.SpaceItemDecoration;
import com.lvdoui6.android.tv.utils.Notify;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.orhanobut.hawk.Hawk;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Objects;

public class MallDialog implements MallAdapter.OnClickListener, DialogInterface.OnDismissListener {

    private final DialogMallBinding binding;
    private final MallCallback callback;
    private final MallAdapter adapter;
    private final AlertDialog dialog;
    private final Activity mActivity;
    private final BigDecimal money;

    public static MallDialog create(Activity activity, String money, String body) {
        return new MallDialog(activity, money, body);
    }

    public MallDialog index(int index) {
        adapter.setSelect(index);
        return this;
    }

    public MallDialog(Activity activity, String money, String body) {
        this.money = new BigDecimal(money);
        this.mActivity = activity;
        this.callback = (MallCallback) activity;
        this.binding = DialogMallBinding.inflate(LayoutInflater.from(activity));
        this.dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).create();
        this.adapter = new MallAdapter(this, body);
    }

    public void show() {
        setRecyclerView();
        setDialog();
    }

    private void setRecyclerView() {
        dialog.setOnDismissListener(this);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setHasFixedSize(true);
        binding.payTip.setVisibility(View.GONE);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 16));
        binding.recycler.post(() -> binding.recycler.scrollToPosition(adapter.getSelect()));
    }

    private void setDialog() {
        if (adapter.getItemCount() == 0) return;
        WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        params.width = ResUtil.getScreenWidth();
        params.height = ResUtil.getScreenHeight();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setDimAmount(0.2f);
        dialog.show();
    }

    private void activateVip(int groupId) {
        new AdmUtils().upgradeGroup(groupId, new Callback() {
            @Override
            public void success(String body) {
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    if (jsonObject.getInt("code") == 1) {
                        callback.payEvent(2);
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.fillInStackTrace();
                    Notify.show(e.getMessage());
                }
            }

            @Override
            public void error(String error) {
                Notify.show("请求服务器失败~");
            }
        });
    }

    @Override
    public void onItemClick(AdmGroup.DataBean item) {
        if (item.getId() == 1) {
            CardDialog.create(mActivity, new Callback(){
                @Override
                public void success() {
                    callback.payEvent(2);
                    dialog.dismiss();
                }

                @Override
                public void error(String msg) {
//                    dialog.dismiss();
                }
            }).show();
        } else {
            String itemPriceStr = item.getPrice();
            BigDecimal itemPrice = new BigDecimal(itemPriceStr);
            int comparison = itemPrice.compareTo(money);
            if (comparison <= 0) {
                activateVip(item.getId());
            } else {
                Payment.get().start(mActivity, type -> {
                    if (type.equals("cancel")) return;
                    loadUrl(item.getPrice(), type, item.getIntro(), item.getId());
                    callback.payEvent(1);
                });
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void loadUrl(String money, String type, String memo, int groupId) {
        binding.payTip.setVisibility(View.VISIBLE);
        binding.webView.setVisibility(View.VISIBLE);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                super.onPageStarted(webView, url, bitmap);
                Log.d("TAG", "onPageStarted: " + url);
                if (url.contains("return") && url.contains("true")) activateVip(groupId);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        settings.setSupportZoom(true);
        binding.webView.setInitialScale(130);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieManager.getInstance().setCookie(Hawk.get(HawkAdm.ADM_URL, ""), "token=" + HawkUser.token());
        binding.webView.loadUrl(Utils.getAdminUrl(HawkConfig.WEB_CREATE_ORDER + "?money=" + money + "&paytype=" + type + "&memo=" + memo));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d("TAG", "onDismiss: 被关闭");
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(Hawk.get(HawkAdm.ADM_URL, ""));
            if (cookies != null) {
                String[] cookieArray = cookies.split(";");
                for (String cookie : cookieArray) {
                    String[] nameValue = cookie.trim().split("=", 2);
                    if (nameValue.length == 2 && "token".equals(nameValue[0])) {
                        String expiredCookie = "token=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/; Domain=" + Hawk.get(HawkAdm.ADM_URL, "");
                        cookieManager.setCookie(Hawk.get(HawkAdm.ADM_URL, ""), expiredCookie);
                        break;
                    }
                }
            }
            binding.webView.clearCache(true);
            binding.webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            binding.webView.clearHistory();
            binding.webView.removeAllViews();
            binding.webView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
