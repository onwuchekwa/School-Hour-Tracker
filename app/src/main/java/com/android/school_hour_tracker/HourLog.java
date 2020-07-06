package com.android.school_hour_tracker;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HourLog extends AppCompatActivity {
    private static final String TAG = "HourLogActivity";

    int numClassId;
    String strClassCode, strClassName, currentDate, startTime, endTime;;
    TextView lblClassCode, lblClassName;
    Chronometer chronometer;
    boolean isRunning;
    long pauseOffsetTime;
    Button btnStart, btnPause, btnReset, btnSave, btnReport;
    ListView lvStudyHourList;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_log);

        /* Reference to the TextViews of the layout activity_hour_log.xml */
        lblClassCode = (TextView) findViewById(R.id.tvLblClassCode);
        lblClassName = (TextView) findViewById(R.id.tvClassCode);

        /* Reference to the Buttons of the layout activity_hour_log.xml */
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnReport = (Button) findViewById(R.id.btnGetReport);

        /* Reference to the Chronometer of the layout activity_hour_log.xml */
        chronometer = (Chronometer) findViewById(R.id.chronometerTimer);

        lvStudyHourList = (ListView) findViewById(R.id.lvHourSpent);

        // Get Intent from the MainActivity
        Bundle intent = getIntent().getExtras();

        /* Initialize Database Helper Class */
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        // Get Class ID passed from an extra
        if(intent != null) {
            numClassId = intent.getInt("classId", -1);
            strClassCode = intent.getString("classCode");
            strClassName = intent.getString("className");
        }

        populateStudyHourListView();

        // Disable Reset, Save and Pause button
        btnPause.setEnabled(false);
        btnSave.setEnabled(false);
        btnReset.setEnabled(false);
        if(lvStudyHourList.getCount() == 0) {
            btnReport.setEnabled(false);
        } else {
            btnReport.setEnabled(true);
        }

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
                    btnStart.setEnabled(false);
                    btnPause.setEnabled(true);
                    btnReset.setEnabled(true);
                    btnSave.setEnabled(true);
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
                btnStart.setText("Resume");
                btnPause.setEnabled(false);
                btnStart.setEnabled(true);
            }
        });

        /*
         * Defining a click event listener for the button "Reset Time"
         */
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetTime("Timer Reset");
                btnStart.setText("Start Time");
                btnStart.setEnabled(true);
                btnPause.setEnabled(false);
                btnSave.setEnabled(false);
                btnReset.setEnabled(false);
                if(lvStudyHourList.getCount() == 0) {
                    btnReport.setEnabled(false);
                } else {
                    btnReport.setEnabled(true);
                }
            }
        });

        /**
         * Defining a click event listener for the button "Reset Time"
         */
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence mChronometer = chronometer.getText();
                String timeSpend;
                if(mChronometer.length() == 5) {
                    timeSpend = "00:" + mChronometer.toString();
                } else if(mChronometer.length() == 7) {
                    timeSpend = "0" + mChronometer.toString();
                } else {
                    timeSpend = mChronometer.toString();
                }

                stopTime("Timer Stopped and Saved!");

                if(!(timeSpend.equals("00:00"))) {
                    addStudyTime(numClassId, currentDate, startTime, endTime, timeSpend);
                    populateStudyHourListView();
                    btnStart.setText("Start Time");
                    btnStart.setEnabled(true);
                    btnPause.setEnabled(false);
                    btnSave.setEnabled(false);
                    btnReset.setEnabled(false);
                    if(lvStudyHourList.getCount() == 0) {
                        btnReport.setEnabled(false);
                    } else {
                        btnReport.setEnabled(true);
                    }
                } else {
                    toastMessage("You have not timed your study. Start timer first");
                }
            }
        });

        /**
         * Defining a click event listener for the button "Generate Report"
         */
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numClassId > -1) {
                     Log.d(TAG, "btnGetReport: The Class ID is: " + numClassId);
                     Intent intent = new Intent(HourLog.this, GenerateStudyReport.class);
                     intent.putExtra("classId", numClassId);
                     intent.putExtra("classCode", strClassCode);
                     intent.putExtra("className", strClassName);
                     intent.putExtras(getIntent());
                     startActivity(intent);
                     finish();
                } else {
                    toastMessage("There is no ID associated with that class name!");
                }
            }
        });

        /**
         * Defining a click event listener for the button "ListView"
         */
        lvStudyHourList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String classStudyData = parent.getItemAtPosition(position).toString();
                String actualStudyTime = classStudyData.substring(classStudyData.indexOf(",") + 13).trim();
                String studyDate = classStudyData.substring(11, classStudyData.indexOf(",")).trim();
                Log.d(TAG, "You clicked on " + studyDate + " and " + actualStudyTime);
                Cursor studyIdData = mDatabaseHelper.getStudyHourId(studyDate, actualStudyTime);
                int singleStudyId = -1;
                while (studyIdData.moveToNext()) {
                    singleStudyId = studyIdData.getInt(0);
                }
                if(singleStudyId > -1) {
                    Log.d(TAG, "onItemClick: The Study ID is: " + singleStudyId);
                    Intent intent = new Intent(HourLog.this, ManageStudyHour.class);
                    intent.putExtra("studyId", singleStudyId);
                    intent.putExtras(getIntent());
                    startActivity(intent);
                    finish();
                } else {
                    toastMessage("There is no ID associated with that study date and time!");
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
        if (isRunning) {
            chronometer.stop();
            isRunning = false;
        }
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

        // Clear ArrayList
        studyHourListData.clear();

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
    }

    /**
     * Check if BackButton menu item is selected
     * @param item get backButton menu item
     * @return selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Pass Intent when BackButton is pressed
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent( HourLog.this, ClassNavigationOptions.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }

    /**
     * Repopulate ListView onResume
     */
    public void onResume() {
        super.onResume();
        populateStudyHourListView();
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}