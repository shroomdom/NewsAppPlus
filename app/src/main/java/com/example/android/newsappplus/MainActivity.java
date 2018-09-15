package com.example.android.newsappplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Display list of news on main page
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private ArrayList<News> newsList;
    private ListView listView;
    private NewsAdapter adapter;
    private static final int IDENTIFIER = 1;
    private TextView errorMessageView;

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.activity_main_list_view);
        adapter = new NewsAdapter(this, new ArrayList<News>());

        errorMessageView = (TextView) findViewById(R.id.error_text_view);
        listView.setEmptyView(errorMessageView);

        boolean hasInternet = checkConnectivity();
        System.out.println("Checking Internet" + hasInternet);
        if (hasInternet) {
            // Set the adapter on the ListView
            // so the list can be populated in the user interface
            listView.setAdapter(adapter);

            // Set onClickListener to the list on news root page and opens other news articles
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    News currentNews = adapter.getItem(position);
                    Uri newsUri = Uri.parse(currentNews.getURL());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                    startActivity(websiteIntent);
                }
            });
        } else {
            errorMessageView.setText(R.string.error_finding_news);
        }

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(IDENTIFIER, null, this);
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String urlBeginning = "http://content.guardianapis.com/search?";

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String articleCount = sharedPrefs.getString(
                getString(R.string.settings_article_count_key),
                getString(R.string.settings_article_count_default));

        String newsSections  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(urlBeginning);
        // http://content.guardianapis.com/search?q=debates&section=politics&show-tags=contributor&api-key=test

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("q", "debates");
        uriBuilder.appendQueryParameter("section", newsSections);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", articleCount);
        uriBuilder.appendQueryParameter("api-key", "test");
        System.out.println("customized URL generated " + uriBuilder.toString());

        // Create a NewsLoader with the request URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsIncomingList) {

        // Clear the adapter of previous news data
        adapter.clear();

        // System.out.println("start of loader");
        /**Add news to data if incoming news list isn't empty
         * If incoming news list is empty, return error message
         */
        if (newsIncomingList != null && !newsIncomingList.isEmpty()) {
            // System.out.println("check if list is empty");
            adapter.addAll(newsIncomingList);
        } else {
            // System.out.println("list is null");
            errorMessageView.setText(R.string.error_finding_news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Reset loader so we can clear out our existing data from the adapter
        adapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get a reference to the LoaderManager, in order to interact with loaders
        LoaderManager loaderManager = getLoaderManager();

        // Restart the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).//
        loaderManager.restartLoader(IDENTIFIER, null, this);
    }
}


