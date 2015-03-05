package com.intersog.androidcourse.rimma.homework12;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends Activity {
    private static final String PREF_IS_CHECKED = "CheckBox is checked";
    private static final String FILE_NAME = "EditText data";
    private CheckBox checkBox;
    private EditText editTextFile;
    private EditText editTextDB;
    private DBHelper dbHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        editTextFile = (EditText) findViewById(R.id.editTextFile);
        editTextDB = (EditText) findViewById(R.id.editTextDB);


    }

    @Override
    protected void onStart() {
        super.onStart();

        checkBox.setChecked(getSavedCheckBoxState());
        String text = readDataFromFile(FILE_NAME);
        if (text != null) {
            editTextFile.setText(text);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveCheckBoxState();
        String text = editTextFile.getText().toString();
        saveEditTextDataToFile(FILE_NAME, text);

    }

    private void saveCheckBoxState() {
        preferences.edit().putBoolean(PREF_IS_CHECKED, checkBox.isChecked()).apply();
    }

    private boolean getSavedCheckBoxState() {
        return preferences.getBoolean(PREF_IS_CHECKED, false);
    }

    private void saveEditTextDataToFile(String filename, String data) {
        File file = new File(getFilesDir(), filename);
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter(file, false));
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String readDataFromFile(String filename) {
        BufferedReader input = null;
        StringBuffer text = new StringBuffer();
        String line = null;
        try {
            input = new BufferedReader(new InputStreamReader(openFileInput(filename)));
            while ((line = input.readLine()) != null) {
                text.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text.toString();
    }

    public void saveToDB(View v) {
        if (!editTextDB.getText().toString().trim().equals("")) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_DATA, editTextDB.getText().toString());
            long rowID = db.insert(DBHelper.TABLE_NAME, null, values);
            if (rowID == -1) {
                Toast.makeText(this, "Row wasn't inserted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Row #" + rowID + " was  inserted.", Toast.LENGTH_SHORT).show();
            }
            dbHelper.close();
        } else {
            Toast.makeText(this, "Field is empty. Enter some text first.", Toast.LENGTH_SHORT).show();
        }

    }
}
