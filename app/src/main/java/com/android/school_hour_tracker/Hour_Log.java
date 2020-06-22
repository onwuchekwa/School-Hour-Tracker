package com.android.school_hour_tracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Hour_Log extends AppCompatActivity {
    private static final String TAG = "HourLogActivity";

    int numClassId;
    String strClassCode, strClassName;
    TextView lblClassCode, lblClassName;
    Chronometer chronometer;
    boolean isRunning;
    long pauseOffsetTime;
    Button btnStart, btnPause, btnReset, btnSave;
    String currentDate, startTime, endTime;
    ListView lvStudyHourList;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour__log);

        /* Reference to the TextViews of the layout activity_hour_log.xml */
        lblClassCode = (TextView) findViewById(R.id.tvClassCode);
        lblClassName = (TextView) findViewById(R.id.tvClassName);

        /* Reference to the Buttons of the layout activity_hour_log.xml */
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnSave = (Button) findViewById(R.id.btn_save);

        /* Reference to the Chronometer of the layout activity_hour_log.xml */
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);

        lvStudyHourList = (ListView) findViewById(R.id.lvHourSpent);

        // Get Intent from the MainActivity
        Intent intent = getIntent();

        /* Initialize Database Helper Class */
        mDatabaseHelper = new DatabaseHelper(this);

        // Get Class ID passed from an extra
        numClassId = intent.getIntExtra("classId", -1);
        strClassCode = intent.getStringExtra("classCode");
        strClassName = intent.getStringExtra("className");

        populateStudyHourListView();

        /* Pass text to the EditText fields */
        if((!strClassCode.equals("") && (!(strClassName != null && strClassName.equals(""))))) {
            lblClassCode.setText(strClassCode);
            lblClassName.setText(strClassName);
        } else {
            toastMessage("There is no data returned from previous action.");
        }

        /*
         * Defining a click event listener for the button "Start Time"
         */
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    Log.d(TAG, "onBtnStart: Timer started");
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffsetTime);
                    chronometer.start();
                    isRunning = true;
                    currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
                    if(startTime == null || startTime.equals(""))
                        startTime = new SimpleDateFormat("HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
                    toastMessage("Timer Started");
                    System.out.println(startTime);
                }
            }
        });

        /*
         * Defining a click event listener for the button "Pause Time"
         */
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTime("Timer Paused");
            }
        });

        /*
         * Defining a click event listener for the button "Reset Time"
         */
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetTime("Timer Reset");
            }
        });

        /*
         * Defining a click event listener for the button "Reset Time"
         */
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeSpend = chronometer.getText().toString();
                stopTime("Timer Stopped and Saved!");
                if(!(timeSpend.equals("00:00"))) {
                    addStudyTime(numClassId, currentDate, startTime, endTime, timeSpend);
                    populateStudyHourListView();
                } else {
                    toastMessage("You have not timed your study. Start timer first");
                }
            }
        });
    }

    /**
     * Stop and pause time
     * @param toastMsg show toast
     */
    public void stopTime(String toastMsg) {
        if (isRunning) {
            Log.d(TAG, toastMsg);
            chronometer.stop();
            pauseOffsetTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isRunning = false;
            endTime = new SimpleDateFormat("HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
            toastMessage(toastMsg);
        }
    }

    /**
     *  Reset time
     * @param toastMsg show toast
     */
    public void resetTime(String toastMsg) {
        Log.d(TAG, toastMsg);
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffsetTime = 0;
        startTime = null;
        toastMessage(toastMsg);
    }

    public void addStudyTime(int recordClassId, String recordDate, String recordStartTime, String recordEndTime,
                             String recordActualTime) {
        boolean insertData = mDatabaseHelper.addStudyRecord(recordClassId, recordDate, recordStartTime, recordEndTime, recordActualTime);
        if(insertData) {
            resetTime("Time Reset");
            toastMessage("Study hour Successfully Inserted");
        } else {
            toastMessage("Something went wrong");
        }
    }

    public void populateStudyHourListView() {
        Log.d(TAG, "populateStudyHourListView: Displaying study hours in the ListView");
        //Get data and append to the list
        Cursor studyHourData = mDatabaseHelper.getAllStudyHours(numClassId);

        /* Items from database is stored in this ArrayList variable */
        ArrayList<String> studyHourListData = new ArrayList<>();
        /* Loop through the ArrayList and add its values to the Cursor */
        if(studyHourData.getCount() == 0) {
            toastMessage("The database is empty.");
        } else {
            while (studyHourData.moveToNext()) {
                studyHourListData.add("Study Date: " + studyHourData.getString(0) + ", "
                        + "Time Spent: " + studyHourData.getString(3));
            }
        }
        /* ArrayAdapter to set items to ListView */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studyHourListData);
        adapter.notifyDataSetChanged();

        /* Setting the adapter to the ListView */
        lvStudyHourList.setAdapter(adapter);

        System.out.println("numID: " + numClassId);
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}