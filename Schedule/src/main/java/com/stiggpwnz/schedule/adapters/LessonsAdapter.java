package com.stiggpwnz.schedule.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stiggpwnz.schedule.R;

import butterknife.InjectView;
import butterknife.Views;

import static com.stiggpwnz.schedule.fragments.MainFragment.isLight;

/**
 * Created by Adel Nizamutdinov on 04.09.13
 */
public class LessonsAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private String[] times;
    private String[] lessons;

    private int highlightedColor;
    private Drawable whiteDrawable;

    private int lesson;

    public LessonsAdapter(Context context, String[] lessons, String[] times, int lesson, int highlightedColor) {
        this.inflater = LayoutInflater.from(context);
        this.times = times;
        this.highlightedColor = highlightedColor;
        this.lessons = lessons;
        this.lesson = lesson;
    }

    public void setLessons(String[] lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public String getItem(int position) {
        return lessons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

        @InjectView(R.id.time) TextView time;
        @InjectView(R.id.lesson) TextView lesson;

        public ViewHolder(View convertView) {
            Views.inject(this, convertView);
        }
    }

    public void setHighlightedColor(int highlightedColor) {
        this.highlightedColor = highlightedColor;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lesson, parent, false);
            convertView.setTag(new ViewHolder(convertView));
            if (whiteDrawable == null) {
                whiteDrawable = convertView.getBackground();
            }
        }

        viewholder = (ViewHolder) convertView.getTag();
        viewholder.time.setText(times[position]);
        viewholder.lesson.setText(lessons[position]);

        if (position == lesson) {
            convertView.setBackgroundColor(highlightedColor);
            int color = viewholder.time.getResources().getColor(isLight(highlightedColor) ?
                    android.R.color.primary_text_light :
                    android.R.color.primary_text_dark);
            viewholder.time.setTextColor(color);
            viewholder.lesson.setTextColor(color);
        } else {
            convertView.setBackgroundDrawable(whiteDrawable);
        }

        return convertView;
    }

    public void setLesson(int lesson) {
        this.lesson = lesson;
        notifyDataSetChanged();
    }
}