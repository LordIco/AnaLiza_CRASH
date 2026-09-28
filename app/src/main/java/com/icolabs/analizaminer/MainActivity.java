package com.icolabs.analizaminer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends Activity {
    private WebView webView;
    private boolean appReady = false;

    private class AppBridge {
        @JavascriptInterface
        public void exitApp() {
            runOnUiThread(() -> finishAndRemoveTask());
        }

        @JavascriptInterface
        public String getNetworkTime(String serverUrl) {
            String base = serverUrl == null ? "" : serverUrl.trim().replaceAll("/+$", "");
            String[] sources = new String[]{"LIVA", "WEB"};
            String[] urls = new String[]{
                base.length() > 0 ? base + "/health" : "",
                "https://www.google.com/generate_204"
            };

            for (int i = 0; i < urls.length; i++) {
                if (urls[i].isEmpty()) continue;
                HttpURLConnection conn = null;
                try {
                    long t0 = System.currentTimeMillis();
                    conn = (HttpURLConnection) new URL(urls[i]).openConnection();
                    conn.setConnectTimeout(2500);
                    conn.setReadTimeout(2500);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("User-Agent", "AnaLizaMinerAndroid/1.0.17");
                    conn.setRequestProperty("Accept", "*/*");
                    conn.connect();
                    String raw = conn.getHeaderField("Date");
                    long t1 = System.currentTimeMillis();
                    if (raw != null && !raw.isEmpty()) {
                        long remote = ZonedDateTime.parse(raw, DateTimeFormatter.RFC_1123_DATE_TIME)
                                .toInstant().toEpochMilli();
                        long epoch = remote + Math.max(0L, t1 - t0) / 2L;
                        return "{\"epoch_ms\":" + epoch + ",\"source\":\"" + sources[i] + "\",\"synced\":true}";
                    }
                } catch (Exception ignored) {
                } finally {
                    if (conn != null) conn.disconnect();
                }
            }

            return "{\"epoch_ms\":" + System.currentTimeMillis() + ",\"source\":\"SISTEMA\",\"synced\":false}";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(2, 7, 13));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(2, 7, 13));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = new WebView(this);
        webView.setBackgroundColor(android.graphics.Color.rgb(2, 7, 13));
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        webView.setNestedScrollingEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setLoadWithOverviewMode(false);
        s.setUseWideViewPort(false);
        s.setTextZoom(100);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        s.setUserAgentString(s.getUserAgentString() + " AnaLizaMinerAndroid/1.0.17");

        webView.addJavascriptInterface(new AppBridge(), "AnaLizaAndroid");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url != null && url.contains("/ui/index.html")) {
                    view.clearHistory();
                    appReady = true;
                }
            }
        });

        webView.loadUrl("file:///android_asset/splash.html?v=1026");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                appReady = false;
                webView.loadUrl("file:///android_asset/ui/index.html?v=1026");
            }
        }, 8250);
    }

    @Override
    public void onBackPressed() {
        if (webView == null || !appReady) return;
        webView.evaluateJavascript(
            "(function(){try{return window.AnaLizaHandleBack?window.AnaLizaHandleBack():false}catch(e){return false}})();",
            null
        );
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AnaLizaAndroid");
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
