package com.stiggpwnz.schedule;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;

public class LessonsAdapter extends BaseAdapter {

    public static final int LESSONS_NUMBER = 7;

    private LayoutInflater inflater;

    private String[] times;
    private String[] lessons;

    private int blue;
    private Drawable white;

    private int lesson;
    private boolean today;

    public LessonsAdapter(Context context, String[] lessons, boolean today) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.times = context.getResources().getStringArray(R.array.times);
        this.blue = context.getResources().getColor(R.color.blue);
        this.lessons = lessons;
        this.today = today;
        if (today)
            updateLesson();
        else
            lesson = -1;
    }

    public void setLessons(String[] lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    public void setLesson(int lesson, String input) {
        lessons[lesson] = input;
        notifyDataSetChanged();
    }

    public void setToday(boolean today) {
        if (this.today != today) {
            this.today = today;
            updateLesson();
        }
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

    private static class ViewHolder {
        TextView time;
        TextView lesson;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lesson, null);
            viewholder = new ViewHolder();
            viewholder.time = (TextView) convertView.findViewById(R.id.textView1);
            viewholder.lesson = (TextView) convertView.findViewById(R.id.textView2);
            if (white == null)
                white = convertView.getBackground();
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.time.setText(times[position]);
        viewholder.lesson.setText(lessons[position]);

        if (today && position == lesson)
            convertView.setBackgroundColor(blue);
        else
            convertView.setBackgroundDrawable(white);

        return convertView;
    }

    public void updateLesson() {
        lesson = -1;
        Calendar calendar = Calendar.getInstance();
        if (today && calendar.get(Calendar.DAY_OF_WEEK) + 5 != DaysAdapter.DAYS_NUMBER) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            switch (hour) {
                case 8:
                    if (minute >= 30)
                        lesson = 0;
                    break;

                // 8.30�9.50
                case 9:
                    if (minute < 50)
                        lesson = 0;
                    else
                        lesson = 1;
                    break;

                case 10:
                    lesson = 1;
                    break;

                // 10.00�11.20
                case 11:
                    if (minute < 20)
                        lesson = 1;
                    else
                        lesson = 2;
                    break;

                // 11.30�12.50
                case 12:
                    if (minute < 50)
                        lesson = 2;
                    else
                        lesson = 3;
                    break;

                case 13:
                    lesson = 3;
                    break;

                // 13.05�14.25
                case 14:
                    if (minute < 25)
                        lesson = 3;
                    else
                        lesson = 4;
                    break;

                // 14.40�16.00
                case 15:
                    lesson = 4;
                    break;

                case 16:
                    lesson = 5;
                    break;

                // 16.10�17.30
                case 17:
                    if (minute < 30)
                        lesson = 5;
                    else
                        lesson = 6;
                    break;

                // 17.40�19.00
                case 18:
                    lesson = 6;
                    break;
            }
        }
        notifyDataSetChanged();
    }
}
