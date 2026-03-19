package com.lvdoui6.android.tv.ui.activity;

import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.github.catvod.utils.Shell;
import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.bean.Config;
import com.lvdoui6.android.tv.databinding.ActivityCrashBinding;
import com.lvdoui6.android.tv.ui.base.BaseActivity;
import com.lvdoui6.android.tv.utils.Notify;

import java.util.List;
import java.util.Objects;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CrashActivity extends BaseActivity {

    private ActivityCrashBinding mBinding;

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityCrashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initEvent() {
        mBinding.details.setOnClickListener(v -> showError());
        mBinding.restart.setOnClickListener(v -> {
            resetDepot(0);
            resetDepot(1);
            CustomActivityOnCrash.restartApplication(this, Objects.requireNonNull(CustomActivityOnCrash.getConfigFromIntent(getIntent())));
        });
    }

    private void resetDepot(int type) {
        List<Config> mItem = Config.getAll(type);
        if (mItem.size() > 0){
            for (int i = 0; i < mItem.size(); i++) {
                Config.delete(mItem.get(i).getUrl(), mItem.get(i).getType());
            }
        }
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.crash_details_title)
                .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent()))
                .setPositiveButton(R.string.crash_details_close, null)
                .show();
    }
}
