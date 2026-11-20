package com.mrgorila.pedidos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        webView = new WebView(this);
        configure(webView);
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void configure(WebView view) {
        WebSettings s = view.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setSupportMultipleWindows(true);
        view.setWebViewClient(new WebViewClient());
        view.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onCreateWindow(WebView parent, boolean dialog, boolean userGesture, Message resultMsg) {
                WebView popup = new WebView(MainActivity.this);
                configurePopup(popup);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(popup);
                resultMsg.sendToTarget();
                return true;
            }
        });
    }

    private void configurePopup(WebView popup) {
        WebSettings s = popup.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        popup.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.postDelayed(() -> printWebView(view), 700);
            }
        });
        popup.setWebChromeClient(new WebChromeClient());
    }

    private void printWebView(WebView view) {
        try {
            PrintManager manager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            String name = "MR-GORILA-Lista-de-surtido";
            manager.print(name, view.createPrintDocumentAdapter(name), new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir la impresión: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) { webView.destroy(); webView = null; }
        super.onDestroy();
    }
}
