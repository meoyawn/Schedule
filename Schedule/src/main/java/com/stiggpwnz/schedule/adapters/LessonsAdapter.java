package com.stiggpwnz.schedule.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stiggpwnz.schedule.Lesson;
import com.stiggpwnz.schedule.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Adel Nizamutdinov on 04.09.13
 */
public class LessonsAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private String[] times;
    private Lesson[] lessons;

    private int highlightedColor;
    private Drawable whiteDrawable;

    private int currentLesson;

    private int darkTextColor;
    private int lightTextColor;

    private boolean evenWeek;

    public LessonsAdapter(Context context, Lesson[] lessons, String[] times, int lesson, int highlightedColor, boolean evenWeek) {
        this.inflater = LayoutInflater.from(context);
        this.times = times;
        this.highlightedColor = highlightedColor;
        this.lessons = lessons;
        this.currentLesson = lesson;
        this.darkTextColor = context.getResources().getColor(android.R.color.primary_text_light);
        this.lightTextColor = context.getResources().getColor(android.R.color.primary_text_dark);
        this.evenWeek = evenWeek;
    }

    public void setLessons(Lesson[] lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Lesson getItem(int position) {
        return lessons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {

        @InjectView(R.id.time) TextView time;
        @InjectView(R.id.lesson) TextView textLesson;

        ViewHolder(View convertView) {
            Views.inject(this, convertView);
        }

        void draw(View convertView, int position) {
            time.setText(times[position]);
            if (getItem(position) != null) {
                textLesson.setText(getItem(position).get(evenWeek));
            }

            if (position == currentLesson) {
                convertView.setBackgroundColor(highlightedColor);
                time.setTextColor(lightTextColor);
                textLesson.setTextColor(lightTextColor);
            } else {
                convertView.setBackgroundDrawable(whiteDrawable);
                time.setTextColor(darkTextColor);
                textLesson.setTextColor(darkTextColor);
            }
        }
    }

    public void setHighlightedColor(int highlightedColor) {
        this.highlightedColor = highlightedColor;
        notifyDataSetChanged();
    }

    public void setEvenWeek(boolean evenWeek) {
        this.evenWeek = evenWeek;
        notifyDataSetChanged();
    }

    public void setLesson(int position, Lesson lesson) {
        lessons[position] = lesson;
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
        viewholder.draw(convertView, position);

        return convertView;
    }

    public void setLesson(int lesson) {
        this.currentLesson = lesson;
        notifyDataSetChanged();
    }
}