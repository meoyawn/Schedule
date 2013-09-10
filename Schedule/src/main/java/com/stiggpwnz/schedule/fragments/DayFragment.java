package com.stiggpwnz.schedule.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stiggpwnz.schedule.LayoutHeightAnimation;
import com.stiggpwnz.schedule.Lesson;
import com.stiggpwnz.schedule.R;
import com.stiggpwnz.schedule.adapters.LessonsAdapter;
import com.stiggpwnz.schedule.fragments.base.BaseListFragment;

import org.joda.time.DateTime;

import java.sql.SQLException;

import butterknife.InjectView;
import butterknife.Views;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

import static com.stiggpwnz.schedule.fragments.MainFragment.getCurrentLesson;

/**
 * Created by stiggpwnz on 31.08.13
 */
public class DayFragment extends BaseListFragment {

    public static DayFragment newInstance(String lessons, int highlightedColor, boolean evenWeek) {
        DayFragment fragment = new DayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("lessons", lessons);
        bundle.putInt("color", highlightedColor);
        bundle.putBoolean("editEven week", evenWeek);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean today = getArguments().getBoolean("today", false);
        setListAdapter(new LessonsAdapter(getActivity(),
                gson.fromJson(getArguments().getString("lessons"), Lesson[].class),
                getResources().getStringArray(R.array.times),
                getLesson(today),
                getArguments().getInt("color"),
                getArguments().getBoolean("editEven week")));
    }

    public void setLessons(Lesson[] lessons) {
        getArguments().putString("lessons", gson.toJson(lessons));
        getListAdapter().setLessons(lessons);
    }

    @Override
    public LessonsAdapter getListAdapter() {
        return (LessonsAdapter) super.getListAdapter();
    }

    public void setToday(boolean today) {
        getArguments().putBoolean("today", today);
        getListAdapter().setLesson(getLesson(today));
    }

    public void setHighLightedColor(int color) {
        getListAdapter().setHighlightedColor(color);
        getArguments().putInt("color", color);
    }

    private int getLesson(boolean today) {
        if (today) {
            return getCurrentLesson(getResources().getStringArray(R.array.times), getString(R.string.time_separator), DateTime.now());
        } else {
            return -1;
        }
    }

    public void setEvenWeek(boolean evenWeek) {
        getListAdapter().setEvenWeek(evenWeek);
        getArguments().putBoolean("editEven week", evenWeek);
    }

    static class EditDialog implements CompoundButton.OnCheckedChangeListener {

        @InjectView(R.id.editTextOdd) EditText editOdd;
        @InjectView(R.id.editTextEven) EditText editEven;
        @InjectView(R.id.textOdd) TextView oddText;
        @InjectView(R.id.textEven) TextView evenText;
        @InjectView(R.id.oddContainer) LinearLayout oddContainer;
        @InjectView(R.id.evenContainer) LinearLayout evenContainer;
        @InjectView(R.id.checkBoxParity) CheckBox parity;

        Lesson lesson;

        EditDialog(View view, Lesson lesson) {
            Views.inject(this, view);
            this.lesson = lesson;

            editOdd.setText(lesson.odd);
            editOdd.setSelection(editOdd.getText().length());

            editEven.setText(lesson.even);
            editEven.setSelection(editEven.getText().length());

            if (lesson.isTheSame()) {
                setWidth(oddText, 0);
                setHeight(evenContainer, 0);
                parity.setChecked(false);
            }
            parity.setOnCheckedChangeListener(this);
        }

        public Lesson getLesson() {
            lesson.odd = editOdd.getText().toString();
            lesson.even = parity.isChecked() ? editEven.getText().toString() : lesson.odd;
            return lesson;
        }

        static void setHeight(View view, int height) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }

        static void setWidth(View view, int height) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = height;
            view.setLayoutParams(layoutParams);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LayoutHeightAnimation textAnimation = new LayoutHeightAnimation(oddText)
                    .from(oddText.getWidth())
                    .to(isChecked ? evenText.getWidth() : 0)
                    .setWidth(true);
            if (isChecked) {
                textAnimation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        oddText.clearAnimation();
                        setWidth(oddText, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            oddText.startAnimation(textAnimation);

            LayoutHeightAnimation containerAnimation = new LayoutHeightAnimation(evenContainer)
                    .from(evenContainer.getHeight())
                    .to(isChecked ? oddContainer.getHeight() : 0);
            if (isChecked) {
                containerAnimation.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        evenContainer.clearAnimation();
                        setHeight(evenContainer, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            evenContainer.startAnimation(containerAnimation);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit, null);
        final EditDialog editDialog = new EditDialog(view, getListAdapter().getItem(position));
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.edit)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        Observable.create(new Func1<Observer<Lesson>, Subscription>() {

                            @Override
                            public Subscription call(Observer<Lesson> observer) {
                                try {
                                    Lesson lesson = editDialog.getLesson();
                                    databaseHelper.getDao(Lesson.class).createOrUpdate(lesson);
                                    observer.onNext(lesson);
                                    observer.onCompleted();
                                } catch (SQLException e) {
                                    observer.onError(e);
                                }
                                return Subscriptions.empty();
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Lesson>() {

                                    @Override
                                    public void call(Lesson lesson) {
                                        getListAdapter().setLesson(position, lesson);
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        editDialog.editOdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        alertDialog.show();
    }
}
