package com.miwth.and102_asm.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.miwth.and102_asm.R;

public class YoutubeFragment extends Fragment {
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    ExtendedFloatingActionButton floatingActionButton;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        webView = view.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String youtubeUrl = "https://m.youtube.com/@thanhnn0106";
        webView.loadUrl(youtubeUrl);

        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                webView.reload();
            }
        });

        floatingActionButton = view.findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeLink = "https://www.youtube.com/@thanhnn0106"; // Thay VIDEO_ID bằng ID của video YouTube
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
                startActivity(intent);
            }
        });
        return view;
    }
}