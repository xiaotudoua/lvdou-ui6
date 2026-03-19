package com.lvdoui6.android.tv.ui.holder;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.bean.Vod;
import com.lvdoui6.android.tv.databinding.AdapterVodListBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.lvdou.HawkCustom;
import com.lvdoui6.android.tv.ui.base.BaseVodHolder;
import com.lvdoui6.android.tv.ui.presenter.VodPresenter;
import com.lvdoui6.android.tv.utils.ImgUtil;

public class VodListHolder extends BaseVodHolder {

    private final VodPresenter.OnClickListener listener;
    private final AdapterVodListBinding binding;
    private final String homeHideVideo;

    public VodListHolder(@NonNull AdapterVodListBinding binding, VodPresenter.OnClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
        this.homeHideVideo = HawkCustom.get().getConfig("home_hide_video", "lvDou");
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && vod != null && Setting.getHomeUI() == 1) {
                RefreshEvent.homeCover(vod.getVodName() + "|" + vod.getVodContent() + "|" + vod.getVodRemarks() + "|" + vod.getVodPic());
            }
        });
    }
    private Vod vod;
    public void setVod(Vod vod) {
        this.vod = vod;
    }
    @Override
    public void initView(Vod item) {
        setVod(item);
        if (homeHideVideo.contains(item.getVodName())) return;
        binding.name.setText(item.getVodName());
        binding.remark.setText(item.getVodRemarks());
        binding.name.setVisibility(item.getNameVisible());
        binding.remark.setVisibility(item.getRemarkVisible());
        binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        binding.getRoot().setOnLongClickListener(v -> listener.onLongClick(item));
        ImgUtil.load(item.getVodName(), item.getVodPic(), binding.image, ImageView.ScaleType.FIT_CENTER, true);
    }
}
