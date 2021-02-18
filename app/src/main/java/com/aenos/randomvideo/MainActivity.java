package com.aenos.randomvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    WebView webView;
    PyObject module;
    SharedPreferences sharedPreferences;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        sharedPreferences = getSharedPreferences("url", Context.MODE_PRIVATE);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python python = Python.getInstance();
        module = python.getModule("main");

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new Browser());
        webView.setWebChromeClient(new MyWebClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        String lastVideo = sharedPreferences.getString("url", "");
        if (!lastVideo.equals("")) {
            webView.loadUrl(lastVideo);
        } else {
            new Background().execute();
        }
    }

    public class Background extends AsyncTask<Void, Void, Void> {
        String url;

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected()) {
                try {
                    PyObject id = module.callAttr("get_random_video_id");
                    url = "https://www.youtube.com/embed/" + id;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("url", url);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            webView.loadUrl(url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nextVideo) {
            new Background().execute();
        }
        if (item.getItemId() == R.id.openInBrowser) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(webView.getUrl()));
            startActivity(intent);
        }
        if (item.getItemId() == R.id.github) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/aenosinc/RandomVideo/"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isConnected() {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    class Browser extends WebViewClient {
        Browser() {
        }

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString) {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            return BitmapFactory.decodeResource(MainActivity.this.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) MainActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            MainActivity.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = MainActivity.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = MainActivity.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) MainActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

}