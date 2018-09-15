package com.example.android.newsappplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list, parent, false);
        }

        // Get the News object located at this position in the list
        News currentNews = getItem(position);

        /**
         *  Get article title, author, category, date published data and set this text
         */
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentNews.getTitle());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        authorTextView.setText(currentNews.getAuthorString());

        TextView categoryTextView = (TextView) listItemView.findViewById(R.id.category_text_view);
        categoryTextView.setText(currentNews.getCategory());

        TextView datePublishedTextView = (TextView) listItemView.findViewById(R.id.date_text_view);

        // Display the publish date of the news in a more readable format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        SimpleDateFormat dateFormatNew = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US);

        try {
            Date newsDate = dateFormat.parse(currentNews.getDatePublished());

            String parsedDate = dateFormatNew.format(newsDate);
            datePublishedTextView.setText(parsedDate);
        } catch (ParseException e) {
        }
        return listItemView;
    }
}
