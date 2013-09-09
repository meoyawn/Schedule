package com.stiggpwnz.schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stiggpwnz.schedule.Group;
import com.stiggpwnz.schedule.R;

import java.util.List;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Adel Nizamutdinov on 07.09.13
 */
public class GroupsAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<Group> groups;
    boolean light;

    public GroupsAdapter(Context context, List<Group> groups, boolean light) {
        this.inflater = LayoutInflater.from(context);
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return groups != null ? groups.size() : 0;
    }

    @Override
    public Group getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    class ViewHolder {

        @InjectView(R.id.imgFavIcon) ImageView favIcon;
        @InjectView(android.R.id.text1) TextView text1;

        ViewHolder(View convertView) {
            Views.inject(this, convertView);
        }

        void setGroup(Group group) {
            favIcon.setImageResource(group.isFavourite ? R.drawable.ic_action_favourite : R.drawable.ic_action_unfavourite);
            text1.setText(group.name);
            text1.setTextColor(text1.getResources().getColor(light ? android.R.color.primary_text_light : android.R.color.primary_text_dark));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.setGroup(getItem(position));
        return convertView;
    }
}
