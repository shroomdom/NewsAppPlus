package com.example.android.newsappplus;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrlComplete;

    public NewsLoader(Context context, String urlComplete) {
        super(context);
        mUrlComplete = urlComplete;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread
     */
    @Override
    public List<News> loadInBackground() {
        List<News> news = QueryUtils.getJsonAPI(mUrlComplete);
        return news;
    }
}

