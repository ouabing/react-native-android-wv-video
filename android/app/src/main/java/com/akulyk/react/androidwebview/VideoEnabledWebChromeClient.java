package com.akulyk.react.androidwebview;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import static android.view.ViewGroup.LayoutParams;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;

/**
 * Provides support for full-screen video on Android
 */
public class VideoEnabledWebChromeClient extends WebChromeClient {

    private final FrameLayout.LayoutParams FULLSCREEN_LAYOUT_PARAMS = new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER);

    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private Activity mActivity;
    private View mWebView;
    private View mVideoView;
    private ReactContext mContext;

    public VideoEnabledWebChromeClient(ReactContext context, WebView webView) {
        mWebView = webView;
        mContext = context;
        mActivity = context.getCurrentActivity();
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (mVideoView != null) {
            callback.onCustomViewHidden();
            return;
        }

        // Store the view and it's callback for later, so we can dispose of them correctly
        mVideoView = view;
        mCustomViewCallback = callback;

        view.setBackgroundColor(Color.BLACK);

        getRootView().addView(view, FULLSCREEN_LAYOUT_PARAMS);

        mWebView.setVisibility(View.GONE);

        WritableMap eventData = Arguments.createMap();
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("WebViewPlayVideoFullScreenStart", eventData);
    }

    @Override
    public void onHideCustomView() {
        if (mVideoView == null) {
            return;
        }

        mVideoView.setVisibility(View.GONE);

        // Remove the custom view from its container.
        getRootView().removeView(mVideoView);
        mVideoView = null;
        mCustomViewCallback.onCustomViewHidden();

        mWebView.setVisibility(View.VISIBLE);

        WritableMap eventData = Arguments.createMap();
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("WebViewPlayVideoFullScreenEnd", eventData);
    }

    private ViewGroup getRootView() {
        return ((ViewGroup) mActivity.findViewById(android.R.id.content));
    }
}
