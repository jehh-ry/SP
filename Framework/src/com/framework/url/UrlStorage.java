package com.framework.url;

import java.util.ArrayList;
import java.util.List;

public class UrlStorage {
    private static final List<String> urls = new ArrayList<>();

    public static void addUrl(String url) {
        urls.add(url);
    }

    public static List<String> getUrls() {
        return urls;
    }
}
