package com.moutamid.uchannelbooster.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.moutamid.uchannelbooster.R;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TestingActivity extends AppCompatActivity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        // Inject JavaScript to detect subscription status
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Load YouTube video page
        webView.loadUrl("https://www.youtube.com/watch?v=27m980F_obg");
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void showToast(String message) {
            runOnUiThread(() -> Toast.makeText(TestingActivity.this, message, Toast.LENGTH_SHORT).show());
        }
    }

    // Inject JavaScript when page loads
    private void injectJavaScript() {
        webView.evaluateJavascript(
                "(function() { " +
                        "var subscribeBtn = document.querySelector(\"#subscribe-button > ytd-subscribe-button-renderer\");" +
                        "if (subscribeBtn) {" +
                        "if (subscribeBtn.innerText.includes('Subscribed')) {" +
                        "Android.showToast('You are already subscribed!');" +
                        "} else {" +
                        "subscribeBtn.addEventListener('click', function() {" +
                        "setTimeout(function() {" +
                        "if (subscribeBtn.innerText.includes('Subscribed')) {" +
                        "Android.showToast('Subscription successful!');" +
                        "} else {" +
                        "Android.showToast('Subscription failed or canceled.');" +
                        "}" +
                        "}, 2000);" +  // Delay check for UI update
                        "});" +
                        "}" +
                        "}" +
                        "})();",
                null);
    }
}
