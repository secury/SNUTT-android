package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;

/**
 * Created by makesource on 2017. 2. 20..
 */

public class PrivacyFragment extends SNUTTBaseFragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privacy, container, false);
        webView = (WebView) rootView.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.loadUrl(getString(R.string.api_server) + getString(R.string.privacy));
        return rootView;
    }

}
