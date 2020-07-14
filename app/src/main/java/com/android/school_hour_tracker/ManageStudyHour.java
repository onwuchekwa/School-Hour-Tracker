package com.android.school_hour_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.constraint.Group;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ManageStudyHour extends AppCompatActivity {
    private static final String TAG = "ManageStudyHour";

    int numClassId, numStudyId;
    TextView lblClassCode, lblClassName, lblStudyDate, lblStartTime, lblEndTime, lblTimeSpend, lblActualTime, mLblTimeSpend;
    DatabaseHelper mDatabaseHelper;
    Group grpListView;
    Button btn_view, btn_edit, btn_del, btn_update;
    Bundle intent;
    EditText edActualTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_study_hour);

        /* Reference to the TextViews of the layout activity_manage_study_hour.xml */
        lblClassCode = (TextView) findViewById(R.id.tvClassCode);
        lblClassName = (TextView) findViewById(R.id.tvClassName);
        lblStudyDate = (TextView) findViewById(R.id.tvStudyDate);
        lblStartTime = (TextView) findViewById(R.id.tvStartTIme);
        lblEndTime = (TextView) findViewById(R.id.tvEndTime);
        lblTimeSpend = (TextView) findViewById(R.id.tvTimeSpend);
        mLblTimeSpend = (TextView) findViewById(R.id.tvLblActualTime);
        lblActualTime = (TextView) findViewById(R.id.tvActualTime);

        /* Reference to the Groups of the layout activity_manage_study_hour.xml */
        grpListView = (Group) findViewById(R.id.groupListView);

        /* Reference to the Buttons of the layout activity_manage_study_hour.xml */
        btn_view = (Button) findViewById(R.id.btnView);
        btn_edit = (Button) findViewById(R.id.btnEdit);
        btn_del = (Button) findViewById(R.id.btnDelete);
        btn_update = (Button) findViewById(R.id.btnUpdate);

        /* Reference to the EditText of the layout activity_manage_study_hour.xml */
        edActualTime = (EditText) findViewById(R.id.editTimeSpend);

        /* Initialize Database Helper Class */
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        // Get Intent from the MainActivity
        intent = getIntent().getExtras();

        // Get Class ID passed from an extra
        if(intent != null) {
            numStudyId = intent.getInt("studyId", -1);
            numClassId = intent.getInt("classId", -1);
            lblClassCode.setText(intent.getString("classCode"));
            lblClassName.setText(intent.getString("className"));
        }

        // Hide List and Edit Items onCreate
        grpListView.setVisibility(View.GONE);
        lblActualTime.setVisibility(View.GONE);
        mLblTimeSpend.setVisibility(View.GONE);
        edActualTime.setVisibility(View.GONE);
        btn_update.setVisibility(View.GONE);

        /*
         * Defining a click event listener for the button "View"
         */
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent != null) {
                    getDataFromIntent(numStudyId);
                    edActualTime.setVisibility(View.GONE);
                    btn_update.setVisibility(View.GONE);
                    if(!grpListView.isShown()) {
                        grpListView.setVisibility(View.VISIBLE);
                    }
                    mLblTimeSpend.setVisibility(View.VISIBLE);
                    lblActualTime.setVisibility(View.VISIBLE);
                    v.setEnabled(false);
                    if(!btn_edit.isEnabled()) {
                        btn_edit.setEnabled(true);
                    }
                }
            }
        });

        /*
         * Defining a click event listener for the button "Edit"
         */
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent != null) {
                    getDataFromIntent(numStudyId);
                    mLblTimeSpend.setVisibility(View.GONE);
                    lblActualTime.setVisibility(View.GONE);
                    if(!grpListView.isShown()) {
                        grpListView.setVisibility(View.VISIBLE);
                    }
                    edActualTime.setVisibility(View.VISIBLE);
                    btn_update.setVisibility(View.VISIBLE);
                    v.setEnabled(false);
                    if(!btn_view.isEnabled()){
                        btn_view.setEnabled(true);
                    }
                }
            }
        });

        /*
         * Defining a click event listener for the button "Delete"
         */
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageStudyHour.this);
                builder.setTitle("Confirm Delete Class");
                builder.setMessage("This action, when confirmed, cannot be reversed. Are you sure you want to proceed?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.deleteStudyRecord(numStudyId);
                        Log.d(TAG, "onBtnDelete: Study record has been deleted");
                        toastMessage("Study Record with ID: " + numStudyId + " has been deleted from the database");
                        dialog.dismiss();
                        Intent intent = new Intent( ManageStudyHour.this, HourLog.class);
                        intent.putExtras(getIntent());
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        /*
         * Defining a click event listener for the button "Delete"
         */
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regexTime = "^([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
                String studyTime = edActualTime.getText().toString();
                String totalTimeSpend = lblTimeSpend.getText().toString();
                String actualStudyTime = lblActualTime.getText().toString();
                if(studyTime.matches(regexTime)) {
                    if(!studyTime.equals(actualStudyTime)) {
                        try {
                            Date dtStudyTime = new SimpleDateFormat("HH:mm:ss", Locale.US).parse(studyTime);
                            Calendar cStudyTime = Calendar.getInstance();
                            cStudyTime.setTime(dtStudyTime);
                            cStudyTime.add(Calendar.DATE, 1);

                            Date dtTotalTimeSpend = new SimpleDateFormat("HH:mm:ss", Locale.US).parse(totalTimeSpend);
                            Calendar cTotalTimeSpend = Calendar.getInstance();
                            cTotalTimeSpend.setTime(dtTotalTimeSpend);
                            cTotalTimeSpend.add(Calendar.DATE, 1);

                            if(cStudyTime.getTime().before(cTotalTimeSpend.getTime()) || cStudyTime.getTime().equals(cTotalTimeSpend.getTime())) {
                                Log.d(TAG, "Updating Study Time: Study Time, " + cStudyTime.getTime() + ", is before the Total Time Spend, " + cTotalTimeSpend.getTime());
                                if((!studyTime.equals(""))) {
                                    mDatabaseHelper.updateStudyRecordData(numStudyId, studyTime);
                                    toastMessage("Class has been updated with new data");
                                    Log.d(TAG, "onBtnUpdate: Class has been updated with new data");
                                    Intent intent = new Intent( ManageStudyHour.this, HourLog.class);
                                    intent.putExtras(getIntent());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    toastMessage("You must provide course code and course name");
                                }
                            } else {
                                toastMessage("Time entered is more than the Total Time Spend plus breaks.");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        toastMessage("You did not make any change");
                    }
                } else {
                    toastMessage("Study Time must be in 24-Hour format (HH:mm:ss)");
                }
            }
        });
    }

    /**
     * Get data passed from intent to ListViews
     */
    public void getDataFromIntent(int numStudyId) {
        Cursor singleStudyReport = mDatabaseHelper.generateSingleStudyReport(numStudyId);
        int singleStudyId = -1;
        String studyDate = null, studyStartTime = null, studyEndTime = null, timeSpend = null, actualTime = null;
        while (singleStudyReport.moveToNext()) {
            singleStudyId = singleStudyReport.getInt(0);
            studyDate = singleStudyReport.getString(1);
            studyStartTime = singleStudyReport.getString(2);
            studyEndTime = singleStudyReport.getString(3);
            timeSpend = singleStudyReport.getString(4);
            actualTime = singleStudyReport.getString(5);
        }
        if(singleStudyId > -1) {
            lblStudyDate.setText(studyDate);
            lblStartTime.setText(studyStartTime);
            lblEndTime.setText(studyEndTime);
            lblTimeSpend.setText(timeSpend);
            lblActualTime.setText(actualTime);
            edActualTime.setText(actualTime);
        }
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
        Intent intent = new Intent( ManageStudyHour.this, HourLog.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
