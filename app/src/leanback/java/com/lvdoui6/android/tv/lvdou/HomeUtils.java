package com.lvdoui6.android.tv.lvdou;

import android.app.Activity;

import com.lvdoui6.android.tv.api.config.VodConfig;
import com.lvdoui6.android.tv.bean.Config;
import com.lvdoui6.android.tv.bean.History;
import com.lvdoui6.android.tv.databinding.FragmentHomeBinding;
import com.lvdoui6.android.tv.event.RefreshEvent;
import com.lvdoui6.android.tv.impl.Callback;
import com.lvdoui6.android.tv.lvdou.bean.Adm;
import com.lvdoui6.android.tv.ui.activity.CollectActivity;
import com.lvdoui6.android.tv.ui.activity.HistoryActivity;
import com.lvdoui6.android.tv.ui.activity.LiveActivity;
import com.lvdoui6.android.tv.ui.activity.PushActivity;
import com.lvdoui6.android.tv.ui.activity.ServiceActivity;
import com.lvdoui6.android.tv.ui.activity.VideoActivity;
import com.lvdoui6.android.tv.ui.activity.WebActivity;
import com.lvdoui6.android.tv.utils.ImgUtil;
import com.lvdoui6.android.tv.utils.Notify;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

public class HomeUtils {

    public static void setHomeVod(Activity activity, FragmentHomeBinding mBinding){
        if (Hawk.get(HawkConfig.APP_E, "false").equals("false")) return;
        List<Adm.DataBean.HomeConfigBean> homeVod = HawkAdm.getHomeConfig();
        mBinding.service.setOnClickListener(v -> ServiceActivity.start(activity));
        mBinding.history.setOnClickListener(v -> HistoryActivity.start(activity));
        if (homeVod != null && homeVod.size() > 0){
            for (int i = 0; i < homeVod.size(); i++) {
                int finalI = i;
                if (mBinding.vodOneTip.getText().toString().isEmpty()){
                    mBinding.vodOneTip.setText(homeVod.get(i).getTitle());
                    mBinding.rlVodOne.setOnClickListener(v -> onClickListener(activity, homeVod.get(finalI)));
                    ImgUtil.loadVod(homeVod.get(i).getTitle(), Utils.getAdminUrl(homeVod.get(i).getCoverimage()), mBinding.vodOne);
                } else if (mBinding.vodTwoTip.getText().toString().isEmpty()) {
                    mBinding.vodTwoTip.setText(homeVod.get(i).getTitle());
                    mBinding.rlVodTwo.setOnClickListener(v -> onClickListener(activity, homeVod.get(finalI)));
                    ImgUtil.loadVod(homeVod.get(i).getTitle(), Utils.getAdminUrl(homeVod.get(i).getCoverimage()), mBinding.vodTwo);
                } else if (mBinding.vodThreeTip.getText().toString().isEmpty()) {
                    mBinding.vodThreeTip.setText(homeVod.get(i).getTitle());
                    mBinding.rlVodThree.setOnClickListener(v -> onClickListener(activity, homeVod.get(finalI)));
                    ImgUtil.loadVod(homeVod.get(i).getTitle(), Utils.getAdminUrl(homeVod.get(i).getCoverimage()), mBinding.vodThree);
                } else if (mBinding.vodFourTip.getText().toString().isEmpty()) {
                    mBinding.vodFourTip.setText(homeVod.get(i).getTitle());
                    mBinding.rlVodFour.setOnClickListener(v -> onClickListener(activity, homeVod.get(finalI)));
                    ImgUtil.loadVod(homeVod.get(i).getTitle(), Utils.getAdminUrl(homeVod.get(i).getCoverimage()), mBinding.vodFour);
                }
            }
        }
    }

    private static void onClickListener(Activity activity, Adm.DataBean.HomeConfigBean homeConfigBean) {
        if (Hawk.get(HawkConfig.APP_E, "false").equals("false")) return;
        String title = homeConfigBean.getTitle();
        String parameter = homeConfigBean.getParameter();
        if (parameter.contains("===")) {
            String imageUrl = homeConfigBean.getCoverimage();
            String[] param = parameter.split("===");
            if (parameter.startsWith("live")) {
                LiveActivity.start(activity);
            } else if (parameter.startsWith("web")) {
                WebActivity.start(activity, param[1]);
            } else if (parameter.contains("|")) {
                String[] siteConfig = param[0].split("\\|");
                Config config = Config.find(Integer.parseInt(siteConfig[1]));
                if (config == null) CollectActivity.start(activity, title);
                else if (Integer.parseInt(siteConfig[1]) != VodConfig.getCid()) {
                    Notify.progress(activity);
                    loadConfig(activity, config, siteConfig[0], param[1], title, imageUrl);
                } else {
                    VideoActivity.start(activity, param[0], param[1], title, imageUrl);
                }
            }
        } else {
            CollectActivity.start(activity, title);
        }
    }

    public static void loadConfig(Activity activity, Config config, String site, String vod, String name, String pic) {
        VodConfig.load(config, new Callback() {
            @Override
            public void success() {
                VideoActivity.start(activity, site, vod, name, pic);
                RefreshEvent.config();
                RefreshEvent.video();
                Notify.dismiss();
            }

            @Override
            public void error(String msg) {
                Notify.show(msg);
                Notify.dismiss();
            }
        });
    }

    public static List<History> getHomeConfig() {
        List<History> items = new ArrayList<>();
        List<Adm.DataBean.HomeConfigBean> homeConfig = HawkAdm.getHomeConfig();
        if (homeConfig != null && homeConfig.size() > 0) {
            for (int i = 0; i < homeConfig.size(); i++) {
                String picUrl = homeConfig.get(i).getCoverimage();
                if (i == 0) {
                    String home_vod = homeConfig.get(i).getTitle() + "|" + homeConfig.get(i).getBlurbcontent() + "|" + homeConfig.get(i).getSubtitle() + "|" + picUrl;
                    Hawk.put(HawkConfig.HOME_VOD_IMG_M, home_vod);
                    RefreshEvent.homeCover(home_vod);
                }
                History history = new History();
                history.setVodPic(Utils.getAdminUrl(picUrl));
                history.setKey(homeConfig.get(i).getParameter());
                history.setVodName(homeConfig.get(i).getTitle());
                history.setVodRemarks(homeConfig.get(i).getSubtitle());
                history.setEpisodeUrl(homeConfig.get(i).getBlurbcontent());
                items.add(history);
            }
        }
        return items;
    }
}
