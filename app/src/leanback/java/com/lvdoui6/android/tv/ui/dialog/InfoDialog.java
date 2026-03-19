package com.lvdoui6.android.tv.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;

import androidx.appcompat.app.AlertDialog;

import com.lvdoui6.android.tv.databinding.DialogMallBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.HawkInfo;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.ui.adapter.InfoAdapter;
import com.lvdoui6.android.tv.ui.custom.SpaceItemDecoration;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.orhanobut.hawk.Hawk;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Objects;

public class InfoDialog implements InfoAdapter.OnClickListener, DialogInterface.OnDismissListener {

    private final DialogMallBinding binding;
    private final InfoAdapter adapter;
    private final AlertDialog dialog;
    private final Activity activity;

    public static InfoDialog create(Activity activity) {
        return new InfoDialog(activity);
    }

    public InfoDialog index(int index) {
        adapter.setSelect(index);
        return this;
    }

    public InfoDialog(Activity activity) {
        this.activity = activity;
        this.binding = DialogMallBinding.inflate(LayoutInflater.from(activity));
        this.dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).create();
        this.adapter = new InfoAdapter(this);
    }

    public void show() {
        setRecyclerView();
        setDialog();
    }

    private void setRecyclerView() {
        dialog.setOnDismissListener(this);
        binding.recycler.setAdapter(adapter);
        binding.recycler.setHasFixedSize(true);
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
        dialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (binding.webView.getVisibility() == View.VISIBLE){
                    binding.webView.setVisibility(View.GONE);
                    return true;
                }
                if (!isDismiss){
                    isDismiss = true;
                    return false;
                }
            }
            return false;
        });
        dialog.show();
    }

    private boolean isDismiss = true;

    @Override
    public void onItemClick(Adm.DataBean.NoticeListBean item) {
        HawkInfo.markMessageAsRead(String.valueOf(item.getId()));
        String content = item.getContent();
        if (content.startsWith("http")) {
            loadUrl(content.split("\\|")[0]);
        } else {
            isDismiss = false;
            DescDialog.show(activity, content);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void loadUrl(String url) {
        binding.webView.setVisibility(View.VISIBLE);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                super.onPageStarted(webView, url, bitmap);
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
        binding.webView.loadUrl(url);
    }

    private void hideWebView() {
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        hideWebView();
        RefreshEvent.homeInfo("lvdou");
    }
}
