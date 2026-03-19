package com.lvdoui6.android.tv.ui.activity;

import static org.greenrobot.eventbus.EventBus.TAG;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.ViewPager;

import com.android.cast.dlna.dmr.DLNARendererService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.Constant;
import com.lvdoui6.android.tv.R;
import com.lvdoui6.android.tv.Setting;
import com.lvdoui6.android.tv.Updater;
import com.lvdoui6.android.tv.api.config.LiveConfig;
import com.lvdoui6.android.tv.api.config.VodConfig;
import com.lvdoui6.android.tv.api.config.WallConfig;
import com.lvdoui6.android.tv.bean.Button;
import com.lvdoui6.android.tv.bean.Class;
import com.lvdoui6.android.tv.bean.Config;
import com.lvdoui6.android.tv.bean.Filter;
import com.lvdoui6.android.tv.bean.Result;
import com.lvdoui6.android.tv.bean.Site;
import com.lvdoui6.android.tv.bean.Vod;
import com.lvdoui6.android.tv.databinding.ActivityHomeBinding;
import com.lvdoui6.android.tv.db.AppDatabase;
import com.lvdoui6.android.tv.event.CastEvent;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.event.ServerEvent;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.impl.ConfigCallback;
import com.lvdoui6.android.tv.impl.RestoreCallback;
import com.lvdoui6.android.tv.lvdou.AdmUtils;
import com.lvdoui6.android.tv.lvdou.HawkAdm;
import com.lvdoui6.android.tv.lvdou.HawkConfig;
import com.lvdoui6.android.tv.lvdou.HawkCustom;
import com.lvdoui6.android.tv.lvdou.HawkInfo;
import com.lvdoui6.android.tv.lvdou.HawkUser;
import com.lvdoui6.android.tv.lvdou.Utils;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.lvdou.bean.AdmUser;
import com.lvdoui6.android.tv.lvdou.impl.AdmCallback;
import com.lvdoui6.android.tv.model.SiteViewModel;
import com.lvdoui6.android.tv.player.Source;
import com.lvdoui6.android.tv.server.Server;
import com.lvdoui6.android.tv.ui.base.BaseActivity;
import com.lvdoui6.android.tv.ui.custom.CustomTitleView;
import com.lvdoui6.android.tv.ui.dialog.HistoryDialog;
import com.lvdoui6.android.tv.ui.dialog.InfoDialog;
import com.lvdoui6.android.tv.ui.dialog.MenuDialog;
import com.lvdoui6.android.tv.ui.dialog.NotifyDialog;
import com.lvdoui6.android.tv.ui.dialog.RestoreDialog;
import com.lvdoui6.android.tv.ui.dialog.SiteDialog;
import com.lvdoui6.android.tv.ui.fragment.HomeFragment;
import com.lvdoui6.android.tv.ui.fragment.VodFragment;
import com.lvdoui6.android.tv.ui.presenter.TypePresenter;
import com.lvdoui6.android.tv.utils.Clock;
import com.lvdoui6.android.tv.utils.FileChooser;
import com.lvdoui6.android.tv.utils.FileUtil;
import com.lvdoui6.android.tv.utils.ImgUtil;
import com.lvdoui6.android.tv.utils.KeyUtil;
import com.lvdoui6.android.tv.utils.Notify;
import com.lvdoui6.android.tv.utils.ResUtil;
import com.lvdoui6.android.tv.utils.Tbs;
import com.lvdoui6.android.tv.utils.UrlUtil;
import com.github.catvod.utils.Prefers;
import com.github.catvod.utils.Trans;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.permissionx.guolindev.PermissionX;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity implements CustomTitleView.Listener, RestoreCallback, TypePresenter.OnClickListener, ConfigCallback {

    public ActivityHomeBinding mBinding;
    private ArrayObjectAdapter mAdapter;
    private HomeActivity.PageAdapter mPageAdapter;
    private SiteViewModel mViewModel;
    public Result mResult;
    private boolean loading;
    private boolean coolDown;
    private View mOldView;
    private boolean confirm;
    private Clock mClock;
    private View mFocus;

    private Site getHome() {
        return VodConfig.get().getHome();
    }

    @Override
    protected ViewBinding getBinding() {
        return mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkAction(intent);
    }

    @Override
    protected void initView() {
        Hawk.put(HawkUser.MARK_CODE, Utils.getAndroidId());
        DLNARendererService.Companion.start(this, R.drawable.ic_logo);
        mClock = Clock.create(mBinding.time).format("HH:mm");
        Updater.get().release().start(this);
        Server.get().start();
        Tbs.init();
        setTitleView();
        setRecyclerView();
        setViewModel();
        setHomeType();
        setPager();
        setAdmUrl();
    }

    private void lvDouEvent() {
        mBinding.service.setOnClickListener(v -> ServiceActivity.start(getActivity()));
        mBinding.search.setOnClickListener(v -> SearchActivity.start(getActivity()));
        mBinding.push.setOnClickListener(v -> PushActivity.start(getActivity()));
        mBinding.user.setOnClickListener(v -> UserActivity.start(getActivity()));
        mBinding.live.setOnClickListener(v -> LiveActivity.start(getActivity()));
//        mBinding.vod.setOnClickListener(v -> VodActivity.start(getActivity(), mResult.clear()));
        mBinding.info.setOnClickListener(v -> InfoDialog.create(this).index(0).show());
//        mBinding.weather.setOnLongClickListener(v -> upWeather());
//        mBinding.weather.setOnClickListener(v -> showCitySelectDialog());
    }

    private void setAdmUrl() {
        LoadImage();
        lvDouEvent();
        mBinding.speed.setText("正在选择线路");
        HawkAdm.get().load(new AdmCallback() {

            @Override
            public void message(String msg) {
                mBinding.speed.setText(msg);
            }

            @Override
            public void success() {
                App.post(() -> {
                    if (isVisible(mBinding.splash)) {
                        mBinding.splash.setVisibility(View.GONE);
                    }
                }, Constant.INTERVAL_HIDE);
                getAppConfig();
                initConfig();
                infoList();
            }

            @Override
            public void error(String msg) {
                mBinding.speed.setText(msg);
                NotifyDialog.create(getActivity(), new NotifyDialog.Callback() {
                    @Override
                    public void retry() {
                        Hawk.delete(HawkAdm.ADM_URL);
                        Hawk.delete(HawkAdm.ADM_URLS);
                        setAdmUrl();
                    }

                    @Override
                    public void close() {
                        finish();
                    }
                }).show();
            }
        });
    }

    private void LoadImage() {
        try {
            File file = FileUtil.getWall(8888);
            mBinding.splash.setVisibility(View.VISIBLE);
            if (!file.exists() && file.length() < 1) {
                mBinding.splash.setBackgroundResource(R.drawable.ic_app_splash);
            } else {
                mBinding.splash.setBackgroundDrawable(WallConfig.drawable(Drawable.createFromPath(file.getAbsolutePath())));
            }
        } catch (Exception e) {
            FileUtil.getWall(9999).delete();
            mBinding.splash.setBackgroundResource(R.drawable.ic_app_splash);
        }
    }

    public void infoList() {
        App.execute(() -> {
            List<Adm.DataBean.NoticeListBean> noticeList = HawkAdm.getNoticeList();
            if (noticeList != null) {
                for (int i = 0; i < noticeList.size(); i++) {
                    String id = String.valueOf(noticeList.get(i).getId());
                    if (!HawkInfo.isMessageRead(id)) {
                        App.post(() -> mBinding.info.setImageResource(R.drawable.ic_home_info_unread));
                        break;
                    } else {
                        App.post(() -> mBinding.info.setImageResource(R.drawable.ic_home_info));
                    }
                }
            }
        });
    }

    private void getAppConfig() {
        if (!HawkCustom.get().hasConfig()) {
            HawkCustom.get().load(new Callback() {
                @Override
                public void success() {
                    setAppConfig();
                }
            });
            return;
        }
        setAppConfig();
    }

    private void setAppConfig() {
        String homeCoverImg = HawkCustom.get().getConfig("home_cover_img", "");
        if (!homeCoverImg.isEmpty())HawkAdm.get().write(FileUtil.getWall(3333), homeCoverImg);
        if (HawkAdm.getService() != null) mBinding.service.setVisibility(View.VISIBLE);
        if (HawkCustom.get().getConfig("home_live", false)) {
            mBinding.live.setVisibility(View.VISIBLE);
        }
        if (HawkCustom.get().getConfig("home_push", false)) {
            mBinding.push.setVisibility(View.VISIBLE);
        }
        if (HawkCustom.get().getConfig("home_info", false)) {
            mBinding.info.setVisibility(View.VISIBLE);
        }
        index(HawkCustom.get().getConfig("auto_logon", false));
        if (Hawk.get(HawkConfig.APP_E, "false").equals("false")){
            Setting.putHomeHistory(true);
            Setting.putHomeUI(0);
        }
        if (!Hawk.get(HawkConfig.APP_G, false)){
            int homeUi = HawkCustom.get().getConfig("home_ui", 0);
            if (Hawk.get(HawkConfig.APP_E, "false").equals("false")) homeUi = 0;
            Hawk.put(HawkConfig.APP_G, true);
            Setting.putHomeHistory(homeUi == 0);
            Setting.putHomeUI(homeUi);
        }
        if (Setting.getHomeUI() == 0) {
            String colorStr = HawkCustom.get().getConfig("home_cover", "#CC44566E");
            mBinding.cover.setBackgroundColor(Color.parseColor(colorStr));
        } else if (Setting.getHomeUI() == 1) {
            try {
                File file = FileUtil.getWall(3333);
                if (!file.exists() && file.length() < 1) {
                    mBinding.cover.setBackgroundResource(R.drawable.home_vod_bg);
                } else {
                    mBinding.cover.setBackgroundDrawable(WallConfig.drawable(Drawable.createFromPath(file.getAbsolutePath())));
                }
            } catch (Exception e) {
                mBinding.cover.setBackgroundResource(R.drawable.home_vod_bg);
                e.printStackTrace();
            }
        }
    }

    private void index(boolean autoRegister) {
        if (HawkUser.checkLogin()) {
            new AdmUtils().index(new Callback() {
                @Override
                public void success(String result) {
                    AdmUser userData = AdmUser.objectFromData(result);
                    if (userData != null && userData.getCode() == 1 && userData.getData() != null) {
                        HawkUser.saveUser(userData);
                    } else {
                        HawkUser.saveUser(null);
                    }
                }
            });
        } else if (autoRegister) {
            new AdmUtils().autoRegister(new Callback() {
                @Override
                public void success(String result) {
                    AdmUser userData = AdmUser.objectFromData(result);
                    if (userData != null && userData.getCode() == 1 && userData.getData() != null) {
                        HawkUser.saveUser(userData);
                    } else {
                        new AdmUtils().autoRegister(null, false);
                    }
                }
            }, true);
        }
    }

    private void setHomeCover(String vodInfo) {
        String[] vodArr = vodInfo.split("\\|");
        if (Setting.getHomeUI() == 0) {
            ImgUtil.rect(vodArr[0], 0, Utils.getAdminUrl(vodArr[3]), mBinding.pic);
        } else {
            ImgUtil.rect(vodArr[0], Utils.getAdminUrl(vodArr[3]), mBinding.pic);
        }
        getHomeFragment().setVodInfo(vodArr);
        if (vodArr[1].isEmpty()) getVodContent(vodArr[0]);
    }

    private void getVodContent(String name) {
        OkGo.getInstance().cancelTag(this);
        OkGo.<String>get(HawkCustom.get().getConfig("vodblurb", "https://api.tiankongapi.com/api.php/provide/vod/?ac=detail&wd=") + name)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        if (body.isEmpty()) {
                            getHomeFragment().setHomeVodBlurb("获取影片详情失败,但这并不影响视频播放。您可以点击影片进入视频页查看或反馈给客服处理。我们很期待收到您的反馈！感谢支持~~~");
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            JSONObject videoObject = jsonArray.getJSONObject(0);
                            getHomeFragment().setHomeVodBlurb(videoObject.getString("vod_blurb"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            getHomeFragment().setHomeVodBlurb("获取影片详情失败,但这并不影响视频播放。您可以点击影片进入视频页查看或反馈给客服处理。我们很期待收到您的反馈！感谢支持~~~");
                        }
                    }

                    @Override
                    public void onError(Response<String> error) {
                        getHomeFragment().setHomeVodBlurb("获取影片详情失败,但这并不影响视频播放。您可以点击影片进入视频页查看或反馈给客服处理。我们很期待收到您的反馈！感谢支持~~~");
                        Log.d(TAG, "错误: " + error.body());
                    }
                });
    }

    @Override
    protected void initEvent() {
        mBinding.title.setListener(this);
        mBinding.pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mBinding.recycler.setSelectedPosition(position);
            }
        });
        mBinding.recycler.addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(@NonNull RecyclerView parent, @Nullable RecyclerView.ViewHolder child, int position, int subposition) {
                onChildSelected(child);
            }
        });
    }

    private void checkAction(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            VideoActivity.push(this, intent.getStringExtra(Intent.EXTRA_TEXT));
        } else if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            if ("text/plain".equals(intent.getType()) || UrlUtil.path(intent.getData()).endsWith(".m3u")) {
                loadLive("file:/" + FileChooser.getPathFromUri(this, intent.getData()));
            } else {
                VideoActivity.push(this, intent.getData().toString());
            }
        }
    }

    private void setTitleView() {
        mBinding.homeSiteLock.setVisibility(Setting.isHomeSiteLock() ? View.VISIBLE : View.GONE);
        if (Setting.getHomeUI() == 0) {
            mBinding.title.setTextSize(24);
            mBinding.time.setTextSize(24);
        } else {
            mBinding.title.setTextSize(20);
            mBinding.time.setTextSize(20);
        }
    }

    private void setRecyclerView() {
        setHomeUI();
        mBinding.recycler.setHorizontalSpacing(ResUtil.dp2px(16));
        mBinding.recycler.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBinding.recycler.setAdapter(new ItemBridgeAdapter(mAdapter = new ArrayObjectAdapter(new TypePresenter(this))));
    }

    private void setHomeUI() {
        if (Setting.getHomeUI() == 0) {
            ViewGroup.LayoutParams layoutParams = mBinding.pic.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mBinding.pic.setLayoutParams(layoutParams);
//            mBinding.recycler.setVisibility(View.GONE);
        } else if (Setting.getHomeUI() == 1) {
            ViewGroup.LayoutParams layoutParams = mBinding.pic.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mBinding.pic.setLayoutParams(layoutParams);
//                mBinding.recycler.setVisibility(View.GONE);
        } else {
            mBinding.cover.setVisibility(View.GONE);
        }
    }

    private void setViewModel() {
        mViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        mViewModel.result.observe(this, result -> {
            setTypes(mResult = result);
        });
    }

    private List<Class> getTypes(Result result) {
        List<Class> items = new ArrayList<>();
        for (String cate : getHome().getCategories())
            for (Class item : result.getTypes())
                if (Trans.s2t(cate).equals(item.getTypeName())) items.add(item);
        return items;
    }

    private String getKey() {
        return getHome().getKey();
    }

    private List<Filter> getFilter(String typeId) {
        return Filter.arrayFrom(Prefers.getString("filter_" + getKey() + "_" + typeId));
    }

    private void setHomeType() {
        Class home = new Class();
        home.setTypeId("home");
        home.setTypeName(ResUtil.getString(R.string.home));
        mAdapter.add(home);
    }

    public void homeContent() {
        mResult = Result.empty();
        mBinding.title.setText(ResUtil.getString(R.string.app_name));
        if (getHome().getKey().isEmpty()) return;
        mFocus = getCurrentFocus();
        getHomeFragment().mBinding.progressLayout.showProgress();
        mViewModel.homeContent();
    }

    public void setTypes(Result result) {
        result.setTypes(getTypes(result));
        for (Map.Entry<String, List<Filter>> entry : result.getFilters().entrySet())
            Prefers.put("filter_" + getKey() + "_" + entry.getKey(), App.gson().toJson(entry.getValue()));
        for (Class item : result.getTypes()) item.setFilters(getFilter(item.getTypeId()));
        if (mAdapter.size() > 1) mAdapter.removeItems(1, mAdapter.size() - 1);
        if (result.getTypes().size() > 0) mAdapter.addAll(1, result.getTypes());
        setPager();
        mPageAdapter.notifyDataSetChanged();
        if (Setting.getHomeUI() == 1) {
            getHomeFragment().addVideo(result);
            List<Vod> vod = result.getList();
            if (vod.size() > 0){
                RefreshEvent.homeCover(vod.get(0).getVodName() + "|" + vod.get(0).getVodContent() + "|" + vod.get(0).getVodRemarks() + "|" + vod.get(0).getVodPic());
            }
        }
        getHomeFragment().mBinding.progressLayout.showContent();
        App.post(() -> setFocus(), 200);
    }

    private void setPager() {
        mBinding.pager.setAdapter(mPageAdapter = new HomeActivity.PageAdapter(getSupportFragmentManager()));
        mBinding.pager.setNoScrollItem(0);
    }

    private void onChildSelected(@Nullable RecyclerView.ViewHolder child) {
        if (mOldView != null) mOldView.setActivated(false);
        if (child == null) return;
        mOldView = child.itemView;
        mOldView.setActivated(true);
        App.post(mRunnable, 100);
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int position = mBinding.recycler.getSelectedPosition();
            mBinding.pager.setCurrentItem(position);
            if (position == 0) showToolBar();
            else hideToolBar();
        }
    };

    private void updateFilter(Class item) {
        if (item.getFilter() == null) return;
        getFragment().toggleFilter(item.toggleFilter());
        mAdapter.notifyArrayItemRangeChanged(1, mAdapter.size() - 1);
    }

    public void hideToolBar() {
        mBinding.toolbar.setVisibility(View.GONE);
        if (mBinding.recycler.getVisibility() == View.VISIBLE)
            mBinding.blank.setVisibility(View.VISIBLE);
        else mBinding.blank.setVisibility(View.GONE);
    }

    public void showToolBar() {
        mBinding.toolbar.setVisibility(View.VISIBLE);
        mBinding.blank.setVisibility(View.GONE);
    }

    private HomeFragment getHomeFragment() {
        return (HomeFragment) mPageAdapter.instantiateItem(mBinding.pager, 0);
    }

    private VodFragment getFragment() {
        return (VodFragment) mPageAdapter.instantiateItem(mBinding.pager, mBinding.pager.getCurrentItem());
    }

    private void setCoolDown() {
        App.post(() -> coolDown = false, 2000);
        coolDown = true;
    }

    private boolean hasSettingButton() {
        return Setting.getHomeButtons(Button.getDefaultButtons()).contains("6");
    }

    @Override
    public void onItemClick(Class item) {
        if (mBinding.pager.getCurrentItem() == 0) {
            SiteDialog.create(this).action().show();
        } else {
            updateFilter(item);
        }
    }

    @Override
    public void onRefresh(Class item) {
        if (mBinding.pager.getCurrentItem() == 0) mBinding.title.requestFocus();
        else getFragment().onRefresh();
    }

    @Override
    public void setConfig(Config config) {
        setConfig(config, "");
    }

    private void setConfig(Config config, String success) {
        if (config.getUrl().startsWith("file") && !PermissionX.isGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).request((allGranted, grantedList, deniedList) -> load(config, success));
        } else {
            load(config, success);
        }
    }

    public void initConfig() {
        if (isLoading()) return;
        WallConfig.get().init();
        LiveConfig.get().init().load();
        VodConfig.get().init().load(getCallback(""), true);
        setLoading(true);
    }

    private Callback getCallback(String success) {
        return new Callback() {
            @Override
            public void success() {
                checkAction(getIntent());
                RefreshEvent.video();
                setLogo();
                if (!TextUtils.isEmpty(success)) Notify.show(success);
            }

            @Override
            public void error(String msg) {
                if (TextUtils.isEmpty(msg) && AppDatabase.getBackup().exists())
                    RestoreDialog.create(getActivity()).show();
                if (getHomeFragment().init) getHomeFragment().mBinding.progressLayout.showContent();
                else App.post(() -> getHomeFragment().mBinding.progressLayout.showContent(), 1000);
                mResult = Result.empty();
                Notify.show(msg);
                setLoading(false);
            }
        };
    }

    @Override
    public void onRestore() {
        PermissionX.init(this).permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).request((allGranted, grantedList, deniedList) -> AppDatabase.restore(new Callback() {
            @Override
            public void success() {
                if (allGranted && getHomeFragment().init)
                    getHomeFragment().mBinding.progressLayout.showProgress();
                if (allGranted) setAdmUrl();
            }
        }));
    }

    private void load(Config config, String success) {
        switch (config.getType()) {
            case 0:
                getHomeFragment().mBinding.progressLayout.showProgress();
                VodConfig.load(config, getCallback(success));
                break;
        }
    }

    private void loadLive(String url) {
        LiveConfig.load(Config.find(url, 1), new Callback() {
            @Override
            public void success() {
                LiveActivity.start(getActivity());
            }
        });
    }

    private void setConfirm() {
        confirm = true;
        Notify.show(R.string.app_exit);
        App.post(() -> confirm = false, 5000);
    }


    @Override
    public void showDialog() {
        if (!hasSettingButton()) {
            MenuDialog.create(this).show();
            return;
        }
        if (Setting.isHomeSiteLock()) return;
        SiteDialog.create(this).action().show();
    }

    @Override
    public void onRefresh() {
        FileUtil.clearCache(new Callback() {
            @Override
            public void success() {
                Config config = VodConfig.get().getConfig().json("").save();
                if (!config.isEmpty())
                    setConfig(config, ResUtil.getString(R.string.config_refreshed));
            }
        });
    }

    @Override
    public boolean onItemLongClick(Class item) {
        if (mBinding.pager.getCurrentItem() != 0) return true;
        onRefresh();
        return true;
    }


    @Override
    public void setSite(Site item) {
        VodConfig.get().setHome(item);
        homeContent();
    }

    @Override
    public void onChanged() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        super.onRefreshEvent(event);
        switch (event.getType()) {
            case CONFIG:
                setLogo();
                break;
            case VIDEO:
                homeContent();
                break;
            case IMAGE:
                getHomeFragment().refreshRecommond();
                break;
            case HISTORY:
                getHomeFragment().getHistory();
                break;
            case SIZE:
                homeContent();
                break;
            case VODINFO:
                setHomeCover(event.getPath());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerEvent(ServerEvent event) {
        switch (event.getType()) {
            case SEARCH:
                CollectActivity.start(this, event.getText(), true);
                break;
            case PUSH:
                VideoActivity.push(this, event.getText());
                break;
            case LOGON:
                logon(event.getName(), event.getText());
                break;
        }
    }

    private void logon(String user, String text){
        if (HawkUser.checkLogin()) {
            Notify.show("您已登录");
            return;
        }
        new AdmUtils().logon(user + "|" + text, new Callback() {
            @Override
            public void success(String body) {
                AdmUser admUser = AdmUser.objectFromData(body);
                if (admUser == null || admUser.getCode() != 1) {
                    Notify.show(body);
                } else {
                    HawkUser.saveUser(admUser);
                    Notify.show(admUser.getMsg());
                }
            }

            @Override
            public void error(String error) {
                Notify.show(error);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCastEvent(CastEvent event) {
        if (VodConfig.get().getConfig().equals(event.getConfig())) {
            VideoActivity.cast(this, event.getHistory().update(VodConfig.getCid()));
        } else {
            VodConfig.load(event.getConfig(), getCallback(event));
        }
    }

    private Callback getCallback(CastEvent event) {
        return new Callback() {
            @Override
            public void success() {
                RefreshEvent.history();
                RefreshEvent.config();
                RefreshEvent.video();
                onCastEvent(event);
            }

            @Override
            public void error(String msg) {
                Notify.show(msg);
            }
        };
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    private void setLogo() {
        Glide.with(this).load(Hawk.get(HawkConfig.PICTURE_LOGO_IMG, "")).circleCrop().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).listener(getListener()).into(mBinding.logo);
    }

    private RequestListener<Drawable> getListener() {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                mBinding.logo.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                mBinding.logo.setVisibility(View.VISIBLE);
                return false;
            }
        };
    }

    private void setFocus() {
        setLoading(false);
        if (!mBinding.title.isFocusable()) App.post(() -> mBinding.title.setFocusable(true), 500);
        if (mFocus != mBinding.title) {
            if (Setting.getHomeUI() == 0) getHomeFragment().mBinding.recycler.requestFocus();
            else mBinding.recycler.requestFocus();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean isHomeFragment = mBinding.pager.getCurrentItem() == 0;
        if (isHomeFragment && KeyUtil.isMenuKey(event)) {
            if (Setting.getHomeMenuKey() == 0) MenuDialog.create(this).show();
            else if (Setting.getHomeMenuKey() == 1) SiteDialog.create(this).action().show();
            else if (Setting.getHomeMenuKey() == 2) HistoryDialog.create(this).type(0).show();
            else if (Setting.getHomeMenuKey() == 3) LiveActivity.start(this);
            else if (Setting.getHomeMenuKey() == 4) HistoryActivity.start(this);
            else if (Setting.getHomeMenuKey() == 5) SearchActivity.start(this);
            else if (Setting.getHomeMenuKey() == 6) PushActivity.start(this);
            else if (Setting.getHomeMenuKey() == 7) KeepActivity.start(this);
            else if (Setting.getHomeMenuKey() == 8) SettingActivity.start(this);
        }
        if (!isHomeFragment && KeyUtil.isMenuKey(event))
            updateFilter((Class) mAdapter.get(mBinding.pager.getCurrentItem()));
        if (!isHomeFragment && KeyUtil.isBackKey(event) && event.isLongPress() && getFragment().goRoot())
            setCoolDown();
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClock.start();
        setTitleView();
        setHomeUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mClock.stop();
    }

    @Override
    protected boolean handleBack() {
        return true;
    }

    @Override
    protected void onBackPress() {
        if (isVisible(mBinding.recycler) && mBinding.recycler.getSelectedPosition() != 0) {
            mBinding.recycler.scrollToPosition(0);
        } else if (mPageAdapter != null && getHomeFragment().init && getHomeFragment().mBinding.progressLayout.isProgress()) {
            getHomeFragment().mBinding.progressLayout.showContent();
        } else if (mPageAdapter != null && getHomeFragment().init && getHomeFragment().mPresenter != null && getHomeFragment().mPresenter.isDelete()) {
            getHomeFragment().setHistoryDelete(false);
        } else if (getHomeFragment().canBack()) {
            getHomeFragment().goBack();
        } else if (!confirm) {
            setConfirm();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        boolean isHomeFragment = mBinding.pager.getCurrentItem() == 0;
        if (isHomeFragment) {
            super.onBackPressed();
            return;
        }
        Class item = (Class) mAdapter.get(mBinding.pager.getCurrentItem());
        if (item.getFilter() != null && item.getFilter()) updateFilter(item);
        else if (getFragment().canBack()) getFragment().goBack();
        else if (!coolDown) super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        WallConfig.get().clear();
        LiveConfig.get().clear();
        VodConfig.get().clear();
        AppDatabase.backup();
        Server.get().stop();
        Source.get().exit();
    }

    class PageAdapter extends FragmentStatePagerAdapter {
        public PageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new HomeFragment();
            Class type = (Class) mAdapter.get(position);
            return VodFragment.newInstance(getHome().getKey(), type.getTypeId(), type.getStyle(), type.getExtend(false), "1".equals(type.getTypeFlag()));
        }

        @Override
        public int getCount() {
            return mAdapter.size();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        }
    }
}
