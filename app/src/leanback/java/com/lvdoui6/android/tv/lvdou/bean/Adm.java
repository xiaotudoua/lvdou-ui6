package com.lvdoui6.android.tv.lvdou.bean;

import com.lvdoui6.android.tv.App;

import java.util.List;

public class Adm {

    private int code;

    private String msg;

    private String time;

    private DataBean data;

    public static Adm objectFromData(String str) {

        return App.gson().fromJson(str, Adm.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private SiteConfigBean siteConfig;
        private List<NoticeListBean> noticeList;
        private List<HomeConfigBean> homeConfig;
        private List<DepotConfigBean> depotConfig;
        private List<ParsesConfigBean> parsesConfig;

        public static DataBean objectFromData(String str) {

            return App.gson().fromJson(str, DataBean.class);
        }

        public SiteConfigBean getSiteConfig() {
            return siteConfig;
        }

        public void setSiteConfig(SiteConfigBean siteConfig) {
            this.siteConfig = siteConfig;
        }

        public List<NoticeListBean> getNoticeList() {
            return noticeList;
        }

        public void setNoticeList(List<NoticeListBean> noticeList) {
            this.noticeList = noticeList;
        }

        public List<HomeConfigBean> getHomeConfig() {
            return homeConfig;
        }

        public void setHomeConfig(List<HomeConfigBean> homeConfig) {
            this.homeConfig = homeConfig;
        }

        public List<DepotConfigBean> getDepotConfig() {
            return depotConfig;
        }

        public void setDepotConfig(List<DepotConfigBean> depotConfig) {
            this.depotConfig = depotConfig;
        }

        public List<ParsesConfigBean> getParsesConfig() {
            return parsesConfig;
        }

        public void setParsesConfig(List<ParsesConfigBean> parsesConfig) {
            this.parsesConfig = parsesConfig;
        }

        public static class SiteConfigBean {

            private String name;
            private String mail_smtp_user;
            private String send_money;
            private String send_vips;
            private String device_overrun;
            private String payment_platform;
            private String amount_conflict;
            private String qrcode_display_method;
            private String qrcode_rule;
            private String live_api;
            private String epg_api;
            private String hot_search_api;
            private String depot_site_hide;
            private String depot_class_hide;
            private String depot_parses_hide;
            private String maccms_key;
            private String qweather_key;
            private String default_player;
            private String resource_renaming;
            private String service_qq;
            private String pay_type_list;
            private AppConfigBean app_config;

            public static SiteConfigBean objectFromData(String str) {

                return App.gson().fromJson(str, SiteConfigBean.class);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMail_smtp_user() {
                return mail_smtp_user;
            }

            public void setMail_smtp_user(String mail_smtp_user) {
                this.mail_smtp_user = mail_smtp_user;
            }

            public String getSend_money() {
                return send_money;
            }

            public void setSend_money(String send_money) {
                this.send_money = send_money;
            }

            public String getSend_vips() {
                return send_vips;
            }

            public void setSend_vips(String send_vips) {
                this.send_vips = send_vips;
            }

            public String getDevice_overrun() {
                return device_overrun;
            }

            public void setDevice_overrun(String device_overrun) {
                this.device_overrun = device_overrun;
            }

            public String getPayment_platform() {
                return payment_platform;
            }

            public void setPayment_platform(String payment_platform) {
                this.payment_platform = payment_platform;
            }

            public String getAmount_conflict() {
                return amount_conflict;
            }

            public void setAmount_conflict(String amount_conflict) {
                this.amount_conflict = amount_conflict;
            }

            public String getQrcode_display_method() {
                return qrcode_display_method;
            }

            public void setQrcode_display_method(String qrcode_display_method) {
                this.qrcode_display_method = qrcode_display_method;
            }

            public String getQrcode_rule() {
                return qrcode_rule;
            }

            public void setQrcode_rule(String qrcode_rule) {
                this.qrcode_rule = qrcode_rule;
            }

            public String getLive_api() {
                return live_api;
            }

            public void setLive_api(String live_api) {
                this.live_api = live_api;
            }

            public String getEpg_api() {
                return epg_api;
            }

            public void setEpg_api(String epg_api) {
                this.epg_api = epg_api;
            }

            public String getHot_search_api() {
                return hot_search_api;
            }

            public void setHot_search_api(String hot_search_api) {
                this.hot_search_api = hot_search_api;
            }

            public String getDepot_site_hide() {
                return depot_site_hide;
            }

            public void setDepot_site_hide(String depot_site_hide) {
                this.depot_site_hide = depot_site_hide;
            }

            public String getDepot_class_hide() {
                return depot_class_hide;
            }

            public void setDepot_class_hide(String depot_class_hide) {
                this.depot_class_hide = depot_class_hide;
            }

            public String getDepot_parses_hide() {
                return depot_parses_hide;
            }

            public void setDepot_parses_hide(String depot_parses_hide) {
                this.depot_parses_hide = depot_parses_hide;
            }

            public String getMaccms_key() {
                return maccms_key;
            }

            public void setMaccms_key(String maccms_key) {
                this.maccms_key = maccms_key;
            }

            public String getQweather_key() {
                return qweather_key;
            }

            public void setQweather_key(String qweather_key) {
                this.qweather_key = qweather_key;
            }

            public String getDefault_player() {
                return default_player;
            }

            public void setDefault_player(String default_player) {
                this.default_player = default_player;
            }

            public String getResource_renaming() {
                return resource_renaming;
            }

            public void setResource_renaming(String resource_renaming) {
                this.resource_renaming = resource_renaming;
            }

            public String getService_qq() {
                return service_qq;
            }

            public void setService_qq(String service_qq) {
                this.service_qq = service_qq;
            }

            public String getPay_type_list() {
                return pay_type_list;
            }

            public void setPay_type_list(String pay_type_list) {
                this.pay_type_list = pay_type_list;
            }

            public AppConfigBean getApp_config() {
                return app_config;
            }

            public void setApp_config(AppConfigBean app_config) {
                this.app_config = app_config;
            }

            public static class AppConfigBean {

                private String name;
                private String key;
                private String qqgroup;
                private String operationmode;
                private String logoimage;
                private String splashimage;
                private String backdropimage;
                private String playerimage;
                private String serviceimage;
                private String about;

                public static AppConfigBean objectFromData(String str) {

                    return App.gson().fromJson(str, AppConfigBean.class);
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getQqgroup() {
                    return qqgroup;
                }

                public void setQqgroup(String qqgroup) {
                    this.qqgroup = qqgroup;
                }

                public String getOperationmode() {
                    return operationmode;
                }

                public void setOperationmode(String operationmode) {
                    this.operationmode = operationmode;
                }

                public String getLogoimage() {
                    return logoimage;
                }

                public void setLogoimage(String logoimage) {
                    this.logoimage = logoimage;
                }

                public String getSplashimage() {
                    return splashimage;
                }

                public void setSplashimage(String splashimage) {
                    this.splashimage = splashimage;
                }

                public String getBackdropimage() {
                    return backdropimage;
                }

                public void setBackdropimage(String backdropimage) {
                    this.backdropimage = backdropimage;
                }

                public String getPlayerimage() {
                    return playerimage;
                }

                public void setPlayerimage(String playerimage) {
                    this.playerimage = playerimage;
                }

                public String getServiceimage() {
                    return serviceimage;
                }

                public void setServiceimage(String serviceimage) {
                    this.serviceimage = serviceimage;
                }

                public String getAbout() {
                    return about;
                }

                public void setAbout(String about) {
                    this.about = about;
                }
            }
        }

        public static class NoticeListBean {

            private int id;
            private String title;
            private String content;
            private long updatetime;
            private String status;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(long updatetime) {
                this.updatetime = updatetime;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }

        public static class HomeConfigBean {

            private String title;
            private String subtitle;
            private String parameter;
            private String blurbcontent;
            private String coverimage;
            private String status;

            public static HomeConfigBean objectFromData(String str) {

                return App.gson().fromJson(str, HomeConfigBean.class);
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSubtitle() {
                return subtitle;
            }

            public void setSubtitle(String subtitle) {
                this.subtitle = subtitle;
            }

            public String getParameter() {
                return parameter;
            }

            public void setParameter(String parameter) {
                this.parameter = parameter;
            }

            public String getBlurbcontent() {
                return blurbcontent;
            }

            public void setBlurbcontent(String blurbcontent) {
                this.blurbcontent = blurbcontent;
            }

            public String getCoverimage() {
                return coverimage;
            }

            public void setCoverimage(String coverimage) {
                this.coverimage = coverimage;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

        }

        public static class DepotConfigBean {

            private String name;
            private String url;
            private String status;
            private String status_text;

            public static DepotConfigBean objectFromData(String str) {

                return App.gson().fromJson(str, DepotConfigBean.class);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatus_text() {
                return status_text;
            }

            public void setStatus_text(String status_text) {
                this.status_text = status_text;
            }
        }

        public static class ParsesConfigBean {

            private String name;
            private String url;
            private String ext;
            private String type;
            private String status;

            public static ParsesConfigBean objectFromData(String str) {

                return App.gson().fromJson(str, ParsesConfigBean.class);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getExt() {
                return ext;
            }

            public void setExt(String ext) {
                this.ext = ext;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
