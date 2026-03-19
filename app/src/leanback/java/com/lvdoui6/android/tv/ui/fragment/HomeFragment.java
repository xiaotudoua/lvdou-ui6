package com.lvdoui6.android.tv.ui.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.api.config.VodConfig;
import com.lvdoui6.android.tv.bean.Button;
import com.lvdoui6.android.tv.bean.Config;
import com.lvdoui6.android.tv.bean.Func;
import com.lvdoui6.android.tv.bean.History;
import com.lvdoui6.android.tv.bean.Result;
import com.lvdoui6.android.tv.bean.Site;
import com.lvdoui6.android.tv.bean.Vod;
import com.lvdoui6.android.tv.databinding.FragmentHomeBinding;
import com.lvdoui6.android.tv.lvdou.HawkConfig;
import com.lvdoui6.android.tv.lvdou.HomeUtils;
import com.lvdoui6.android.tv.ui.activity.CollectActivity;
import com.lvdoui6.android.tv.ui.activity.HistoryActivity;
import com.lvdoui6.android.tv.ui.activity.HomeActivity;
import com.lvdoui6.android.tv.ui.activity.KeepActivity;
import com.lvdoui6.android.tv.ui.activity.LiveActivity;
import com.lvdoui6.android.tv.ui.activity.PushActivity;
import com.lvdoui6.android.tv.ui.activity.SearchActivity;
import com.lvdoui6.android.tv.ui.activity.SettingActivity;
import com.lvdoui6.android.tv.ui.activity.VideoActivity;
import com.lvdoui6.android.tv.ui.activity.VodActivity;
import com.lvdoui6.android.tv.ui.activity.WebActivity;
import com.lvdoui6.android.tv.ui.base.BaseFragment;
import com.lvdoui6.android.tv.ui.custom.CustomRowPresenter;
import com.lvdoui6.android.tv.ui.custom.CustomSelector;
import com.lvdoui6.android.tv.ui.presenter.FuncPresenter;
import com.lvdoui6.android.tv.ui.presenter.HeaderPresenter;
import com.lvdoui6.android.tv.ui.presenter.HistoryPresenter;
import com.lvdoui6.android.tv.ui.presenter.ProgressPresenter;
import com.lvdoui6.android.tv.ui.presenter.VodPresenter;
import com.lvdoui6.android.tv.utils.Notify;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.orhanobut.hawk.Hawk;

import java.util.List;


public class HomeFragment extends BaseFragment implements VodPresenter.OnClickListener, FuncPresenter.OnClickListener, HistoryPresenter.OnClickListener {

    public FragmentHomeBinding mBinding;

    private ArrayObjectAdapter mHistoryAdapter;
    public HistoryPresenter mPresenter;
    private ArrayObjectAdapter mAdapter;
    public boolean init;
    private int homeUI;
    private String button;

    private Site getHome() {
        return VodConfig.get().getHome();
    }

    @Override
    protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mBinding = FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        HomeUtils.setHomeVod(getActivity(), mBinding);
        mBinding.progressLayout.showProgress();
        setRecyclerView();
        setAdapter();
        initEvent();
        setHomeUI();
        init = true;
    }

    public void setHomeVodBlurb(String text) {
        mBinding.text.setText(text
                .replaceAll("<p>", "")
                .replaceAll("</p>", "")
                .replaceAll("<br>", "")
                .replaceAll("&quot", ""));
    }

    public void setVodInfo(String[] vodArr) {
        mBinding.name.setText(vodArr[0]);
        setHomeVodBlurb(vodArr[1]);
        mBinding.score.setText(vodArr[2]);
    }

    public void setHomeUI() {
        if (Setting.getHomeUI() == 2) {
            mBinding.recommend.setVisibility(View.VISIBLE);
        } else {
            mBinding.recommend.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        getHistory();
    }

    protected void initEvent() {
        mBinding.recycler.addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(@NonNull RecyclerView parent, @Nullable RecyclerView.ViewHolder child, int position, int subposition) {
                if (position < 4) getHomeActicity().showToolBar();
                else getHomeActicity().hideToolBar();
                if (mPresenter != null && mPresenter.isDelete()) setHistoryDelete(false);
            }
        });
    }

    private HomeActivity getHomeActicity() {
        return (HomeActivity) getActivity();
    }

    private void setRecyclerView() {
        CustomSelector selector = new CustomSelector();
        selector.addPresenter(Integer.class, new HeaderPresenter());
        selector.addPresenter(String.class, new ProgressPresenter());
        selector.addPresenter(ListRow.class, new CustomRowPresenter(16), VodPresenter.class);
        selector.addPresenter(ListRow.class, new CustomRowPresenter(22), FuncPresenter.class);
        selector.addPresenter(ListRow.class, new CustomRowPresenter(16), HistoryPresenter.class);
        mBinding.recycler.setAdapter(new ItemBridgeAdapter(mAdapter = new ArrayObjectAdapter(selector)));
        mBinding.recycler.setVerticalSpacing(ResUtil.dp2px(16));
    }

    private void setAdapter() {
        ListRow funcRow = getFuncRow();
        if (funcRow != null) mAdapter.add(funcRow);
        if (Setting.isHomeHistory()) mAdapter.add(R.string.home_history);
        mAdapter.add(R.string.home_recommend);
        mHistoryAdapter = new ArrayObjectAdapter(mPresenter = new HistoryPresenter(this));
        homeUI = Setting.getHomeUI();
        button = Setting.getHomeButtons(Button.getDefaultButtons());
        if (funcRow != null) setTitleNextFocus(funcRow);
    }

    public void addVideo(Result result) {
        int index = getRecommendIndex();
        if (Hawk.get(HawkConfig.APP_E, "false").equals("false")) return;
        if (mAdapter.size() > index) mAdapter.removeItems(index, mAdapter.size() - index);
        List<Vod> items = result.getList();
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new VodPresenter(this));
        adapter.setItems(items, null);
        mAdapter.add(new ListRow(adapter));
    }

    private ListRow getFuncRow() {
        List<Button> buttonList = Button.getButtons();
        if (buttonList.isEmpty()) return null;
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new FuncPresenter(this));
        for (int i = 0; i < buttonList.size(); i++) {
            adapter.add(Func.create(buttonList.get(i).getResId()));
        }
        if (adapter.size() > 1) {
            ((Func) adapter.get(0)).setNextFocusLeft(((Func) adapter.get(adapter.size() - 1)).getId());
            ((Func) adapter.get(adapter.size() - 1)).setNextFocusRight(((Func) adapter.get(0)).getId());
        }
        return new ListRow(adapter);
    }

    private void setTitleNextFocus(ListRow funcRow) {
        if (funcRow == null) return;
        Func func = (Func) funcRow.getAdapter().get(0);
        int downId = getHomeActicity().mBinding.recycler.getVisibility() == View.VISIBLE ? -1 : func.getId();
        getHomeActicity().mBinding.title.setNextFocusDownId(downId);
    }

    private void refreshFuncRow() {
        if (homeUI == Setting.getHomeUI() && Setting.getHomeButtons(Button.getDefaultButtons()).equals(button))
            return;
        if (!TextUtils.isEmpty(button)) mAdapter.removeItems(0, 1);
        homeUI = Setting.getHomeUI();
        button = Setting.getHomeButtons(Button.getDefaultButtons());
        ListRow funcRow = getFuncRow();
        if (funcRow != null) mAdapter.add(0, funcRow);
        if (funcRow != null) setTitleNextFocus(funcRow);
    }

    public void refreshRecommond() {
        int index = getRecommendIndex();
        mAdapter.notifyArrayItemRangeChanged(index, mAdapter.size() - index);
    }

    public void getHistory() {
        getHistory(false);
    }

    public void getHistory(boolean renew) {
        int historyIndex = getHistoryIndex();
        int recommendIndex = getRecommendIndex();
        if (historyIndex == -1) {
            if (!Setting.isHomeHistory()) return;
            int historyStringIndex = recommendIndex - 1;
            historyStringIndex = historyStringIndex < 0 ? 0 : historyStringIndex;
            mAdapter.add(historyStringIndex, R.string.home_history);
        }
        if (!Setting.isHomeHistory()) {
            mAdapter.removeItems(historyIndex - 1, 2);
            return;
        }
        historyIndex = getHistoryIndex();
        recommendIndex = getRecommendIndex();
        List<History> items = HomeUtils.getHomeConfig();
        boolean exist = recommendIndex - historyIndex == 2;
        if (renew)
            mHistoryAdapter = new ArrayObjectAdapter(mPresenter = new HistoryPresenter(this));
        if ((items.isEmpty() && exist) || (renew && exist)) mAdapter.removeItems(historyIndex, 1);
        if ((items.size() > 0 && !exist) || (renew && exist))
            mAdapter.add(historyIndex, new ListRow(mHistoryAdapter));
        mHistoryAdapter.setItems(items, null);
    }

    public void setHistoryDelete(boolean delete) {
        mPresenter.setDelete(delete);
        mHistoryAdapter.notifyArrayItemRangeChanged(0, mHistoryAdapter.size());
    }

    private void clearHistory() {
        mAdapter.removeItems(getHistoryIndex(), 1);
        History.delete(VodConfig.getCid());
        mPresenter.setDelete(false);
        mHistoryAdapter.clear();
    }

    private int getHistoryIndex() {
        for (int i = 0; i < mAdapter.size(); i++)
            if (mAdapter.get(i).equals(R.string.home_history)) return i + 1;
        return -1;
    }

    private int getRecommendIndex() {
        for (int i = 0; i < mAdapter.size(); i++)
            if (mAdapter.get(i).equals(R.string.home_recommend)) return i + 1;
        return -1;
    }

    @Override
    public void onItemClick(Func item) {
        switch (item.getResId()) {
            case R.string.home_history_short:
                HistoryActivity.start(getActivity());
                break;
            case R.string.home_vod:
                VodActivity.start(getActivity(), getHomeActicity().mResult.clear());
                break;
            case R.string.home_live:
                LiveActivity.start(getActivity());
                break;
            case R.string.home_search:
                SearchActivity.start(getActivity());
                break;
            case R.string.home_keep:
                KeepActivity.start(getActivity());
                break;
            case R.string.home_push:
                PushActivity.start(getActivity());
                break;
            case R.string.home_setting:
                SettingActivity.start(getActivity());
                break;
        }
    }

    @Override
    public void onItemClick(History item) {
        String parameter = item.getSiteKey();
        if (parameter.contains("===")) {
            String[] param = parameter.split("===");
            if (parameter.startsWith("live")) {
                LiveActivity.start(getActivity());
            } else if (parameter.startsWith("web")) {
                WebActivity.start(getActivity(), param[1]);
            } else if (parameter.contains("|")) {
                String[] siteConfig = param[0].split("\\|");
                Config config = Config.find(Integer.parseInt(siteConfig[1]));
                if (config == null) {
                    CollectActivity.start(getActivity(), item.getVodName());
                } else if (Integer.parseInt(siteConfig[1]) != VodConfig.getCid()) {
                    Notify.progress(getActivity());
                    HomeUtils.loadConfig(getActivity(), config, siteConfig[0], param[1], item.getVodName(), item.getVodPic());
                } else {
                    VideoActivity.start(getActivity(), param[0], param[1], item.getVodName(), item.getVodPic());
                }
            }
        } else {
            CollectActivity.start(getActivity(), item.getVodName());
        }
    }

    @Override
    public void onItemDelete(History item) {
        mHistoryAdapter.remove(item.delete());
        if (mHistoryAdapter.size() > 0) return;
        mAdapter.removeItems(getHistoryIndex(), 1);
        mPresenter.setDelete(false);
    }

    @Override
    public boolean onLongClick() {
        if (mPresenter.isDelete()) {
            new MaterialAlertDialogBuilder(getActivity()).setTitle(R.string.dialog_delete_record).setMessage(R.string.dialog_delete_history).setNegativeButton(R.string.dialog_negative, null).setPositiveButton(R.string.dialog_positive, (dialog, which) -> clearHistory()).show();
        } else {
            setHistoryDelete(true);
        }
        return true;
    }

    @Override
    public void onItemClick(Vod item) {
        if (getHome().isIndexs()) CollectActivity.start(getActivity(), item.getVodName());
        else
            VideoActivity.start(getActivity(), item.getVodId(), item.getVodName(), item.getVodPic());
    }

    @Override
    public boolean onLongClick(Vod item) {
        CollectActivity.start(getActivity(), item.getVodName());
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFuncRow();
    }

    public boolean canBack() {
        return mBinding.recycler.getSelectedPosition() != 0;
    }

    public void goBack() {
        mBinding.recycler.scrollToPosition(0);
    }

}
