package com.android.school_hour_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Hour_Log extends AppCompatActivity {
    private static final String TAG = "Hour_LogActivity";

    int numClassId;
    String strClassCode, strClassName;
    TextView lblClassCode, lblClassName;
    Chronometer chronometer;
    boolean isRunning;
    long pauseOffsetTime;
    Button btnStart, btnPause, btnReset, btnSave;
    String currentDate, startTime, endTime;

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

        // Get Intent from the MainActivity
        Intent intent = getIntent();

        // Get Class ID passed from an extra
        numClassId = intent.getIntExtra("classId", -1);
        strClassCode = intent.getStringExtra("classCode");
        strClassName = intent.getStringExtra("className");

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
                //TODO: Change this to save the data to the database
                String timeSpend = String.valueOf(chronometer.getText());
                stopTime("Timer Stopped and Saved!");
                //TODO: Timer to be reset after inserting to the database
                Log.d(TAG, "Date: " + currentDate + " Time Started: " + startTime + " Actual Time Spend: " + timeSpend +
                        " Time Ended: " + endTime);
                //TODO: Populate ListView with Date and Time Spent
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
        toastMessage(toastMsg);
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}