package com.lvdoui6.android.tv.ui.holder;

import androidx.annotation.NonNull;

import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.bean.Vod;
import com.lvdoui6.android.tv.databinding.AdapterVodOvalBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.lvdou.HawkCustom;
import com.lvdoui6.android.tv.ui.base.BaseVodHolder;
import com.lvdoui6.android.tv.ui.presenter.VodPresenter;
import com.lvdoui6.android.tv.utils.ImgUtil;

public class VodOvalHolder extends BaseVodHolder {

    private final VodPresenter.OnClickListener listener;
    private final AdapterVodOvalBinding binding;
    private final String homeHideVideo;

    public VodOvalHolder(@NonNull AdapterVodOvalBinding binding, VodPresenter.OnClickListener listener) {
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

    public VodOvalHolder size(int[] size) {
        binding.image.getLayoutParams().width = size[0];
        binding.image.getLayoutParams().height = size[1];
        return this;
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
        binding.name.setVisibility(item.getNameVisible());
        binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        binding.getRoot().setOnLongClickListener(v -> listener.onLongClick(item));
        ImgUtil.oval(item.getVodName(), item.getVodPic(), binding.image);
    }
}
