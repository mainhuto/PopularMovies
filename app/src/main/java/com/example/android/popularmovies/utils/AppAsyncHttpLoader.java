package com.example.android.popularmovies.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AppAsyncHttpLoader extends AsyncTaskLoader {

    public static final String SEARCH_QUERY_URL_1_EXTRA = "query_1";
    public static final String SEARCH_QUERY_URL_2_EXTRA = "query_2";

    private Bundle args;
    private List<String> jsonResultList;
    private LoaderListener loaderListener;

    public interface LoaderListener{
        public void onForceLoad();
    }

    public AppAsyncHttpLoader(Context context, Bundle args) {
        super(context);
        if (LoaderListener.class.isAssignableFrom(context.getClass())) {
            loaderListener = (LoaderListener)context;
        }
        this.args = args;
    }

    @Override
    protected void onStartLoading() {

        if ( (jsonResultList != null) && (jsonResultList.size() > 0) ) {
            deliverResult(jsonResultList);
        } else {
            if (args != null) {
                if (loaderListener != null) {
                    loaderListener.onForceLoad();
                }
                forceLoad();
            }
        }

    }

    @Override
    public List<String> loadInBackground() {

        List<String> fetchedDataList = new ArrayList<>();

        List<String> queries = new ArrayList<String>();
        String urlString = args.getString(SEARCH_QUERY_URL_1_EXTRA);
        if (TextUtils.isEmpty(urlString)) {
            return null;
        }

        queries.add(urlString);
        if (args.containsKey(SEARCH_QUERY_URL_2_EXTRA)) {
            queries.add(args.getString(SEARCH_QUERY_URL_2_EXTRA));
        }

        try {
            for ( String searchQueryUrlString: queries) {
                URL searchQueryUrl = new URL(searchQueryUrlString);
                String fetchedData = NetworkUtil.getResponseFromHttpUrl(searchQueryUrl);
                fetchedDataList.add(fetchedData);
            }
            return fetchedDataList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(Object data) {
        jsonResultList = (List<String>)data;
        super.deliverResult(data);
    }

}
