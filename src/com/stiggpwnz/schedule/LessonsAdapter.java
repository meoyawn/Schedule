package com.stiggpwnz.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LessonsAdapter extends BaseAdapter {

	public static final int LESSONS_NUMBER = 7;

	private LayoutInflater inflater;

	private String[] times;
	private String[] lessons;

	private int blue;
	private int lesson;

	public LessonsAdapter(Context context, String[] lessons, int lesson) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.times = context.getResources().getStringArray(R.array.times);
		this.blue = context.getResources().getColor(R.color.abs__holo_blue_light);
		this.lessons = lessons;
		this.lesson = lesson;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.lesson, null);
			viewholder = new ViewHolder();
			viewholder.time = (TextView) convertView.findViewById(R.id.textView1);
			viewholder.lesson = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		viewholder.time.setText(times[position]);
		viewholder.lesson.setText(lessons[position]);
		if (position == lesson)
			convertView.setBackgroundColor(blue);

		return convertView;
	}

}
