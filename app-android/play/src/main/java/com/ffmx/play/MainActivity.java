package com.ffmx.play;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final String GAME_URL = "https://natansatyroalmeida227-jpg.github.io/https-ffmx.github.io-https-ffmx.github.io-painel-https-ffmx.github.io-gerador-/play/";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(1024, 1024);
        WebView web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        web.setWebViewClient(new WebViewClient());
        web.loadUrl(GAME_URL);
        setContentView(web);
    }

    @Override public void onBackPressed() {
        WebView web=(WebView)findViewById(android.R.id.content);
        super.onBackPressed();
    }
}
