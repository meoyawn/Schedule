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

/**
 * Created by Adel Nizamutdinov on 04.09.13
 */
public class LessonsAdapter extends BaseAdapter {

    public static final int LESSONS_NUMBER = 7;

    private LayoutInflater inflater;

    private String[] times;
    private String[] lessons;

    private int blueColor;
    private Drawable whiteDrawable;

    private int lesson;
    private boolean today;

    public LessonsAdapter(Context context, String[] lessons, String[] times) {
        // TODO fix
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.times = times;
        // TODO fix
        this.blueColor = context.getResources().getColor(R.color.blue);
        this.lessons = lessons;
    }

    public void setLessons(String[] lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return LESSONS_NUMBER;
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

        if (today && position == lesson)
            convertView.setBackgroundColor(blueColor);
        else {
            convertView.setBackgroundDrawable(whiteDrawable);
        }

        return convertView;
    }
}
