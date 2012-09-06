package com.stiggpwnz.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class EditLessonFragment extends SherlockDialogFragment {

	public static interface Listener {
		public void saveLesson(int day, int lesson, String output);
	}

	public static final String INPUT = "input";
	public static final String LESSON = "lesson";
	public static final String DAY = "day";

	private EditText edit;
	private Listener listener;

	public static EditLessonFragment newInstance(int day, int lesson, String input) {
		Bundle args = new Bundle();
		args.putInt(DAY, day);
		args.putInt(LESSON, lesson);
		args.putString(INPUT, input);

		EditLessonFragment frag = new EditLessonFragment();
		frag.setArguments(args);
		return frag;
	}

	public EditLessonFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (Listener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new AlertDialog.Builder(getSherlockActivity());
		builder.setTitle(R.string.edit);

		View view = getSherlockActivity().getLayoutInflater().inflate(R.layout.edit, null);
		builder.setView(view);

		edit = (EditText) view.findViewById(R.id.edit);
		String string = savedInstanceState == null ? getArguments().getString(INPUT) : savedInstanceState.getString(INPUT);
		edit.setText(string);
		edit.requestFocus();
		getSherlockActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		builder.setPositiveButton(R.string.save, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int day = getArguments().getInt(DAY);
				int lesson = getArguments().getInt(LESSON);
				String output = edit.getText().toString().trim();
				listener.saveLesson(day, lesson, output);
				dismissAllowingStateLoss();
			}
		});

		builder.setNegativeButton(R.string.discard, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismissAllowingStateLoss();
			}
		});

		return builder.create();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putString(INPUT, edit.getText().toString());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

}
