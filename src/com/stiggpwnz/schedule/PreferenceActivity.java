package com.stiggpwnz.schedule;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

public class PreferenceActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String FACULTY = "faculty";
    public static final String GROUP = "group";

    private static final int GROUP_NAME_CELL = 1;

    private ScheduleApplication app;
    private SharedPreferences prefs;
    private ListPreference groups;
    private ListPreference faculty;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        app = (ScheduleApplication) getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.preferences);

        faculty = (ListPreference) findPreference(FACULTY);
        setSummary(faculty);

        groups = new ListPreference(this);
        new GroupsParser().execute();
        groups.setKey(GROUP);
        groups.setDialogTitle(getString(R.string.group));
        groups.setTitle(getString(R.string.group));
        getPreferenceScreen().addPreference(groups);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static void setSummary(ListPreference preference) {
        try {
            String value = preference.getValue();
            CharSequence[] entries = preference.getEntries();
            if (value != null && entries != null) {
                int index = preference.findIndexOfValue(value);
                CharSequence charSequence = entries[index];
                preference.setSummary(charSequence);
            }
        } catch (Exception e) {

        }
    }

    private static String getEntry(ListPreference preference) {
        String value = preference.getValue();
        if (value != null && preference.getEntries() != null) {
            int index = preference.findIndexOfValue(value);
            CharSequence charSequence = preference.getEntries()[index];
            return (String) charSequence;
        }
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(FACULTY)) {
            app.resetFaculty();
            setSummary(faculty);

            Editor editor = sharedPreferences.edit();
            editor.remove(GROUP);
            editor.commit();
            groups.setSummary(null);

            new GroupsParser().execute();
        } else if (key.equals(GROUP)) {
            app.resetGroup();
            app.setGroupName(getEntry(groups));
            setSummary(groups);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    private static class NamesAndNumbers {

        String[] names;
        String[] numbers;

        NamesAndNumbers(String[] names, String[] numbers) {
            this.names = names;
            this.numbers = numbers;
        }

    }

    private class GroupsParser extends AsyncTask<Void, Void, NamesAndNumbers> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            groups.setEnabled(false);
            setSupportProgressBarIndeterminateVisibility(true);
        }

        private int getGroupIndex() {
            String[] faculties = getResources().getStringArray(R.array.filenames);
            for (int i = 0; i < faculties.length; i++) {
                if (faculties[i].equals(app.getFaculty()))
                    return i;
            }
            return -1;
        }

        private NamesAndNumbers parseNamesAndNumbers(Sheet sheet, int group) {
            if (group != -1) {
                String prefix = getResources().getStringArray(R.array.groups_prefix)[group];
                int lastRowNum = sheet.getLastRowNum();
                List<String> names = new ArrayList<String>();
                List<String> numbers = new ArrayList<String>();
                for (int i = sheet.getFirstRowNum(); i <= lastRowNum; i++) {
                    Cell cell = sheet.getRow(i).getCell(GROUP_NAME_CELL);

                    String stringCellValue;
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            stringCellValue = cell.getStringCellValue();
                            break;

                        case Cell.CELL_TYPE_NUMERIC:
                            stringCellValue = String.valueOf((int) cell.getNumericCellValue());
                            break;

                        default:
                            continue;
                    }

                    if (stringCellValue.startsWith(prefix) && stringCellValue.charAt(1) != ' ') {
                        names.add(stringCellValue);
                        numbers.add(String.valueOf(i));
                    }
                }
                int size = names.size();
                String[] namesArray = names.toArray(new String[size]);
                String[] numbersArray = numbers.toArray(new String[size]);
                return new NamesAndNumbers(namesArray, numbersArray);
            }
            return null;
        }

        @Override
        protected NamesAndNumbers doInBackground(Void... params) {
            Sheet sheet = app.getSheet();
            int group = getGroupIndex();
            return parseNamesAndNumbers(sheet, group);
        }

        @Override
        protected void onPostExecute(NamesAndNumbers result) {
            super.onPostExecute(result);
            setSupportProgressBarIndeterminateVisibility(false);
            if (result != null) {
                groups.setEnabled(true);
                groups.setEntries(result.names);
                groups.setEntryValues(result.numbers);
            }
            setSummary(groups);
        }

    }

}
