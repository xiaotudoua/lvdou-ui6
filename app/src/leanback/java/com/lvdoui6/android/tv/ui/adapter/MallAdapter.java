package com.lvdoui6.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lvdoui6.android.tv.databinding.AdapterMallBinding;
import com.lvdoui6.android.tv.lvdou.bean.AdmGroup;

import java.util.ArrayList;
import java.util.List;

public class MallAdapter extends RecyclerView.Adapter<MallAdapter.ViewHolder> {

    private final OnClickListener mListener;
    private final List<AdmGroup.DataBean> mItems;
    private int select;

    public MallAdapter(OnClickListener listener, String body) {
        this.mItems = list(body);
        this.mListener = listener;
    }

    private List<AdmGroup.DataBean> list(String body) {
        List<AdmGroup.DataBean> items = new ArrayList<>();
        AdmGroup groupList = AdmGroup.objectFrom(body);
        if (groupList != null && groupList.getData().size() > 1){
            items.addAll(groupList.getData());
        }
        return items;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return select;
    }

    public interface OnClickListener {

        void onItemClick(AdmGroup.DataBean item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AdapterMallBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdmGroup.DataBean item = mItems.get(position);
        holder.binding.name.setText(item.getName());
        holder.binding.describe.setText(item.getIntro());
        holder.binding.name.setActivated(select == position);
        holder.binding.price.setText(String.valueOf(item.getPrice()));
        holder.binding.getRoot().setOnClickListener(v -> mListener.onItemClick(item));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AdapterMallBinding binding;

        public ViewHolder(@NonNull AdapterMallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
