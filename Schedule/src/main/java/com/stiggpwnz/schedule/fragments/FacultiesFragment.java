package com.stiggpwnz.schedule.fragments;

import android.R;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.stiggpwnz.schedule.FileMetadata;
import com.stiggpwnz.schedule.Utils;
import com.stiggpwnz.schedule.fragments.base.RetainedProgressFragment;

import java.io.File;
import java.io.FileReader;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Func1;

import static com.stiggpwnz.schedule.fragments.MainFragment.DROPBOX;

/**
 * Created by stiggpwnz on 01.09.13
 */
public class FacultiesFragment extends RetainedProgressFragment implements AdapterView.OnItemClickListener {

    ListView listView;
    FileMetadata[] fileMetadatas;
    private Subscription subscription;

    @Override
    protected void onRetryClick() {
        onFirstCreated();
    }

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
            setContentShownNoAnimation(true);
        }
    }

    @Override
    public void onFirstCreated() {
        setContentShown(false);
        File file = getFile();
        Observable<FileMetadata[]> observable;
        if (file.exists() && file.length() > 0) {
            observable = parseFile(file);
        } else {
            observable = Observable.from(Ion.with(getActivity(), DROPBOX + "/list.json")
                    .write(file))
                    .flatMap(new Func1<File, Observable<FileMetadata[]>>() {

                        @Override
                        public Observable<FileMetadata[]> call(final File file) {
                            return parseFile(file);
                        }
                    });
        }
        subscription = observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FileMetadata[]>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (getView() != null) {
                            setContentEmpty(true);
                            setContentShown(true);
                        }
                    }

                    @Override
                    public void onNext(FileMetadata[] fileMetadatas) {
                        FacultiesFragment.this.fileMetadatas = fileMetadatas;
                        setAdapter();
                        setContentEmpty(false);
                        setContentShown(true);
                    }
                });

    }

    private Observable<FileMetadata[]> parseFile(final File file) {
        return Observable.create(new Func1<Observer<FileMetadata[]>, Subscription>() {

            @Override
            public Subscription call(Observer<FileMetadata[]> observer) {
                try {
                    observer.onNext(new Gson().fromJson(new FileReader(file), FileMetadata[].class));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        });
    }

    private File getFile() {
        return new File(Utils.getFilesDir(getActivity()), "list.json");
    }

    void setAdapter() {
        ArrayAdapter<FileMetadata> adapter = new ArrayAdapter<FileMetadata>(getActivity(), R.layout.simple_list_item_1, fileMetadatas);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bus.post(parent.getAdapter().getItem(position));
    }
}
