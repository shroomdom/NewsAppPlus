package com.example.android.newsappplus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Methods to help request and receive news data from Guardian
 */
public class QueryUtils {

    /**
     * Create a private constructor so can only be used within this class
     */
    private QueryUtils() {
    }

    public static ArrayList<News> getJsonAPI(String urlComplete) {
        ArrayList<News> newsList = new ArrayList<>();
        URL url = null;
        String jsonResponse = null;

        try {
            // Try to create an URL from the String
            url = new URL(urlComplete);
            try {
                // System.out.println("make http request");
                jsonResponse = makeHttpRequest(url);
                // System.out.println("show json response" + jsonResponse);
                newsList = extractNews(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            // System.out.println("url generation " + e);
        }
        return newsList;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        HttpURLConnection urlHandshake = null;
        String jsonGData = null;

        try {
            // Try to establish a HTTP connection with the request URL and set up the properties of the request
            urlHandshake = (HttpURLConnection) url.openConnection();
            urlHandshake.setReadTimeout(10000 /* milliseconds */);
            urlHandshake.setConnectTimeout(15000 /* milliseconds */);
            urlHandshake.setRequestMethod("GET");
            urlHandshake.connect();

            if (urlHandshake.getResponseCode() == 200) {
                // System.out.println("connection established");
                InputStream inputStream = urlHandshake.getInputStream();
                // System.out.println("start of getStreamReader");
                jsonGData = getStreamReader(inputStream);
                // System.out.println("json gData" + jsonGData.toString());
            } else {
                // System.out.println("No connection established");
            }

        } catch (IOException e) {
            // System.out.println("Error Here" + e);

        } finally {
            // Disconnect the HTTP connection if it is not disconnected yet
            if (urlHandshake != null) {
                urlHandshake.disconnect();
            }
        }
        return jsonGData;
    }

    private static String getStreamReader(InputStream inputStream) throws IOException {
        StringBuilder httpData = new StringBuilder();
        // System.out.println("inside getStreamReader");
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            // System.out.println("step 1");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // System.out.println("step 2");
            String line = reader.readLine();
            // System.out.println("step 3");
            while (line != null) {
                httpData.append(line);
                // System.out.println("step 4");
                line = reader.readLine();
            }
        }
        // System.out.println("show http data " + httpData.toString());
        return httpData.toString();
    }

    /**
     * Return a list of newsList objects that has been built up from parsing a JSON response.
     */
    public static ArrayList<News> extractNews(String guardianJsonAPI) {

        // Create an empty ArrayList that we can start adding news to
        ArrayList<News> newsList = new ArrayList<>();
        // System.out.println("show guardian json api" + guardianJsonAPI);

        if (TextUtils.isEmpty(guardianJsonAPI)) {
            return null;
        }
        /**
         *  Try to parse the response. If there's a problem with the way the JSON
         *  is formatted, a JSONException exception object will be thrown.
         *
         *  Catch the exception so the app doesn't crash.
         */
        try {
            // System.out.println("start try stmt");
            // Create a JSONObject from the JSON response string
            JSONObject initialJsonData = new JSONObject(guardianJsonAPI);
            // System.out.println("start response object");
            JSONObject responseObject = initialJsonData.getJSONObject("response");
            // System.out.println("start jsonArray");
            JSONArray resultsListArray = responseObject.getJSONArray("results");

            if (resultsListArray != null) {

                // System.out.println("show length of resultsListArray" + resultsListArray.length());
                for (int i = 0; i < resultsListArray.length(); i++) {
                    JSONObject currentNews = resultsListArray.getJSONObject(i);
                    JSONArray tagsList = currentNews.getJSONArray("tags");

                    String title = currentNews.getString("webTitle");
                    ArrayList<String> author = getAuthorName(tagsList);
                    String category = currentNews.getString("sectionName");
                    String datePublished = currentNews.getString("webPublicationDate");
                    String url = currentNews.getString("webUrl");

                    // System.out.println("show title" + title);
                    News tempNews = new News(title, author, category, datePublished, url);
                    newsList.add(tempNews);
                }
            }

        } catch (JSONException e) {
            System.out.println("catch jsonException" + e);
        }
        return newsList;
    }

    private static ArrayList<String> getAuthorName(JSONArray tagsList) {
        ArrayList<String> authorName = new ArrayList<>();

        if (tagsList != null) {

            // System.out.println("show length of resultsListArray" + resultsListArray.length());
            for (int i = 0; i < tagsList.length(); i++) {

                try {
                    JSONObject currentTag = tagsList.getJSONObject(i);
                    String author = currentTag.getString("webTitle");
                    authorName.add(author);
                } catch (JSONException e) {
                    System.out.println("catch jsonException" + e);
                }
            }
        }
        return authorName;
    }
}
