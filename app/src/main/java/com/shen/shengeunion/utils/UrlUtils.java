package com.shen.shengeunion.utils;

public class UrlUtils {
    public static String getCategoryUrl(int id, int page) {
        return "discovery/" + id + "/" + page;
    }

    public static String getCoverPath(String pict_url, int size) {
        return "https:" + pict_url + "_" + size + "x" + size + ".jpg";
    }

    public static String getTicketUrl(String url) {
        if (url == null) {
            return url;
        }
        if (url.startsWith("http") || url.startsWith("https")) {
            return url;
        } else {
            return "https:" + url;
        }
    }

    public static String getSelectedPageUrl(int id) {
        return "recommend/" + id;
    }

    public static String getOnSellContent(int page) {
        return "onSell/" + page;
    }
}
