package com.lvdoui6.android.tv.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.databinding.ActivityWebviewBinding;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.ui.base.BaseActivity;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ProgressBar;

import androidx.viewbinding.ViewBinding;
import com.orhanobut.hawk.Hawk;
import java.util.HashMap;
import java.util.Objects;

public class WebActivity extends BaseActivity {

    private ActivityWebviewBinding mBinding;

    public static void start(Activity activity, String url) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra("url", url);
        activity.startActivityForResult(intent, 1000);
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityWebviewBinding.inflate(getLayoutInflater());
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        ProgressBar mProgressBar = findViewById(R.id.progressBar);
        HashMap<String, String> httpHeaders = new HashMap<>();
        mBinding.context.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CookieManager.getInstance().setCookie(url, "token=" + Hawk.get(HawkUser.token(), ""));
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (s.contains("TRADE_SUCCESS") || s.contains("index/recharge/moneylog")) {
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        mBinding.context.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.context.requestFocusFromTouch();
        WebSettings settings = mBinding.context.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUserAgentString("PC");
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        CookieManager.getInstance().setAcceptCookie(true);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        httpHeaders.put("token", Hawk.get(HawkUser.token(), ""));
        mBinding.context.loadUrl(Objects.requireNonNull(url), httpHeaders);
    }

    @Override
    protected void onDestroy() {
        try {
            mBinding.context.clearCache(true);
            mBinding.context.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mBinding.context.clearHistory();
            mBinding.context.removeAllViews();
            mBinding.context.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}