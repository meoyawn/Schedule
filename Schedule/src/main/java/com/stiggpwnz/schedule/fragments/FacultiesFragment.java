package com.stiggpwnz.schedule.fragments;

import android.R;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.stiggpwnz.schedule.FileMetadata;
import com.stiggpwnz.schedule.fragments.base.RetainedProgressFragment;

import static com.stiggpwnz.schedule.fragments.MainFragment.DROPBOX;

/**
 * Created by stiggpwnz on 01.09.13.
 */
public class FacultiesFragment extends RetainedProgressFragment implements AdapterView.OnItemClickListener, FutureCallback<FileMetadata[]> {

    ListView listView;
    FileMetadata[] fileMetadatas;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        listView = new ListView(getActivity());
        listView.setOnItemClickListener(this);
        setContentView(listView);
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        if (fileMetadatas != null) {
            setAdapter();
        }
    }

    @Override
    public void onFirstCreated(View view) {
        setContentShown(false);
        Ion.with(getActivity(), DROPBOX + "/list.json")
                .group(this)
                .as(FileMetadata[].class)
                .setCallback(this);
    }

    @Override
    public void onCompleted(Exception e, FileMetadata[] fileMetadatas) {
        if (e == null) {
            this.fileMetadatas = fileMetadatas;
            setAdapter();
            setContentEmpty(false);
        } else {
            setEmptyText(e.getMessage());
            setContentEmpty(true);
        }
        setContentShown(true);
    }

    void setAdapter() {
        ArrayAdapter<FileMetadata> adapter = new ArrayAdapter<FileMetadata>(getActivity(), R.layout.simple_list_item_1, fileMetadatas);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        Ion.getDefault(getActivity()).cancelAll(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bus.post(parent.getAdapter().getItem(position));
    }
}
