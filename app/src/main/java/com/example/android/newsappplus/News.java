package com.example.android.newsappplus;

import java.util.ArrayList;

public class News {
    private String mTitle;
    private ArrayList<String> mAuthor;
    private String mCategory;
    private String mDatePublished;
    private String mURL;

    /**
     * Create a new News object
     */
    public News(String title, ArrayList<String> author, String category, String datePublished, String url) {
        mTitle = title;
        mAuthor = author;
        mCategory = category;
        mDatePublished = datePublished;
        mURL = url;
    }

    /**
     * Get the article title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the author of article.
     */
    public ArrayList<String> getAuthor() {
        return mAuthor;
    }

    public String getAuthorString(){
        String authorInOneLine = "";
        for (int a=0; a < mAuthor.size(); a++){
            authorInOneLine = authorInOneLine + mAuthor.get(a) + '\n';
        }
        return authorInOneLine;
    }

    /**
     * Get the article category.
     */
    public String getCategory() {
        return mCategory;
    }

    /**
     * Get the article publish date.
     */
    public String getDatePublished() {
        return mDatePublished;
    }

    /**
     * Get the article URL.
     */
    public String getURL() {
        return mURL;
    }
}

