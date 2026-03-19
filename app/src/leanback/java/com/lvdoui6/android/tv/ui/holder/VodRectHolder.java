package com.lvdoui6.android.tv.ui.holder;

import androidx.annotation.NonNull;

import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.bean.Vod;
import com.lvdoui6.android.tv.databinding.AdapterVodRectBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.lvdou.HawkCustom;
import com.lvdoui6.android.tv.ui.base.BaseVodHolder;
import com.lvdoui6.android.tv.ui.presenter.VodPresenter;
import com.lvdoui6.android.tv.utils.ImgUtil;

public class VodRectHolder extends BaseVodHolder {

    private final VodPresenter.OnClickListener listener;
    private final AdapterVodRectBinding binding;
    private final String homeHideVideo;
    private Vod vod;
    public void setVod(Vod vod) {
        this.vod = vod;
    }

    public VodRectHolder(@NonNull AdapterVodRectBinding binding, VodPresenter.OnClickListener listener) {
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

    public VodRectHolder size(int[] size) {
        binding.getRoot().getLayoutParams().width = size[0];
        binding.getRoot().getLayoutParams().height = size[1];
        return this;
    }

    @Override
    public void initView(Vod item) {
        setVod(item);
        if (homeHideVideo.contains(item.getVodName())) return;
        binding.name.setText(item.getVodName());
        binding.year.setText(item.getVodYear());
        binding.site.setText(item.getSiteName());
        binding.remark.setText(item.getVodRemarks());
        binding.site.setVisibility(item.getSiteVisible());
        binding.year.setVisibility(item.getYearVisible());
        binding.name.setVisibility(item.getNameVisible());
        binding.remark.setVisibility(item.getRemarkVisible());
        binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        binding.getRoot().setOnLongClickListener(v -> listener.onLongClick(item));
        ImgUtil.rect(item.getVodName(), item.getVodPic(), binding.image);
    }
}
