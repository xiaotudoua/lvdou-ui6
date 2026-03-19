package com.lvdoui6.android.tv.player.extractor;

import android.os.SystemClock;

import com.lvdoui6.android.tv.App;
import com.lvdoui6.android.tv.player.Source;
import com.lvdoui6.android.tv.ui.activity.VideoActivity;

public class Push implements Source.Extractor {

    @Override
    public boolean match(String scheme, String host) {
        return "push".equals(scheme);
    }

    @Override
    public String fetch(String url) throws Exception {
        if (App.activity() != null) VideoActivity.start(App.activity(), url.substring(7));
        SystemClock.sleep(500);
        return "";
    }

    @Override
    public void stop() {
    }

    @Override
    public void exit() {
    }
}
