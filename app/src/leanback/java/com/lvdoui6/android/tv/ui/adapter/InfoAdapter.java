package com.lvdoui6.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lvdoui6.android.tv.databinding.AdapterInfoBinding;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.Utils;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.utils.Notify;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    private final OnClickListener mListener;
    private final List<Adm.DataBean.NoticeListBean> mItems;
    private int select;

    public InfoAdapter(OnClickListener listener) {
        this.mItems = HawkAdm.getNoticeList();
        this.mListener = listener;
    }

    public interface OnClickListener {

        void onItemClick(Adm.DataBean.NoticeListBean item);
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return select;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterInfoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Adm.DataBean.NoticeListBean notice = mItems.get(position);
        String content = notice.getContent();
        holder.binding.name.setText(notice.getTitle());
        holder.binding.describe.setText(content.startsWith("http") ? content.split("\\|")[1] : content);
        holder.binding.time.setText(Utils.stampToDate(notice.getUpdatetime() * 1000));
        holder.binding.name.setActivated(select == position);
        holder.binding.getRoot().setOnClickListener(v -> mListener.onItemClick(notice));
        holder.binding.getRoot().setOnLongClickListener(v -> time(notice.getUpdatetime()));
    }

    private boolean time(long time) {
        Notify.show("发布时间:" + time);
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AdapterInfoBinding binding;

        public ViewHolder(@NonNull AdapterInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
