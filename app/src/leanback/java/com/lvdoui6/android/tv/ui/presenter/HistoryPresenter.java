package com.lvdoui6.android.tv.ui.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.leanback.widget.Presenter;

import com.lvdoui6.android.tv.Product;
import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.bean.History;
import com.lvdoui6.android.tv.databinding.AdapterVodBinding;
import com.lvdoui6.android.tv.databinding.AdapterVodHomeBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.utils.ImgUtil;
import com.lvdoui6.android.tv.utils.ResUtil;

public class HistoryPresenter extends Presenter {

    private final OnClickListener mListener;
    private int width, height;
    private boolean delete;

    public HistoryPresenter(OnClickListener listener) {
        this.mListener = listener;
        setLayoutSize();
    }

    public interface OnClickListener {

        void onItemClick(History item);

        void onItemDelete(History item);

        boolean onLongClick();
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    private void setLayoutSize() {
        int space = ResUtil.dp2px(48) + ResUtil.dp2px(16 * (Product.getColumn() - 1));
        int base = ResUtil.getScreenWidth() - space;
        width = base / Product.getColumn();
        height = (int) (width / 0.75f);
    }

    @NonNull
    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(AdapterVodHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object object) {
        History item = (History) object;
        ViewHolder holder = (ViewHolder) viewHolder;
        setClickListener(holder.view, item);
        setOnFocusChangeListener((ViewHolder) viewHolder, (History) object);
        holder.binding.name.setText(item.getVodName());
        holder.binding.remark.setVisibility(delete ? View.GONE : View.VISIBLE);
        holder.binding.remark.setText(item.getVodRemarks());
        ImgUtil.loadVod(item.getVodName(), item.getVodPic(), holder.binding.image);
    }

    private void setOnFocusChangeListener(ViewHolder holder, History item) {
        holder.view.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                RefreshEvent.homeCover(item.getVodName() + "|" + item.getEpisodeUrl() + "|" + item.getVodRemarks() + "|" + item.getVodPic());
            }
        });
    }

    private void setClickListener(View root, History item) {
        root.setOnLongClickListener(view -> mListener.onLongClick());
        root.setOnClickListener(view -> {
            if (isDelete()) mListener.onItemDelete(item);
            else mListener.onItemClick(item);
        });
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    public static class ViewHolder extends Presenter.ViewHolder {

        private final AdapterVodHomeBinding binding;

        public ViewHolder(@NonNull AdapterVodHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}