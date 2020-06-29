package com.android.school_hour_tracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GenerateStudyReport extends AppCompatActivity {
    private static final String TAG = "GenerateStudyReport";
    private static final int FLAG_START_DATE = 0;
    private static final int FLAG_END_DATE = 1;

    int numClassId, year, month, day;
    String strClassCode, strClassName;
    TextView tvClassInfo;
    EditText editStartDate, editEndDate;
    Button btnViewReport;
    ListView listView;
    Calendar mCalender;
    DatePickerDialog mDatePickerDialog;

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_study_report);

        /* Reference to the TextView of the layout activity_generate_study_report.xml */
        tvClassInfo = (TextView) findViewById(R.id.tvInfo);

        /* Reference to the EditText of the layout activity_generate_study_report.xml */
        editStartDate = (EditText) findViewById(R.id.edStartDate);
        editEndDate = (EditText) findViewById(R.id.edEndDate);

        /* Reference to the EditText of the layout activity_generate_study_report.xml */
        btnViewReport = (Button) findViewById(R.id.btnViewReport);

        // Get Current Date
        mCalender = Calendar.getInstance();

        // Get Intent from the MainActivity
        Intent intent = getIntent();

        // Get Class ID passed from an extra
        numClassId = intent.getIntExtra("classId", -1);
        strClassCode = intent.getStringExtra("classCode");
        strClassName = intent.getStringExtra("className");

        // Set Text to the TextView
        tvClassInfo.setText(String.format("Select Start and End Dates to Generate Report for: %s - %s", strClassCode, strClassName));

        // Assign Calendar Variables
        year = mCalender.get(Calendar.YEAR);
        month = mCalender.get(Calendar.MONTH);
        day = mCalender.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(GenerateStudyReport.this, datePickerListener, year, month, day);

        /* Reference to the ListView of the layout main.xml */
        listView = (ListView) findViewById(R.id.lvStudyReport);

        /* Initialize Database Helper Class */
        mDatabaseHelper = new DatabaseHelper(this);

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "You have clicked on: " + FLAG_START_DATE);
                mDatePickerDialog.getDatePicker().setTag(FLAG_START_DATE);
                mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePickerDialog.show();
            }
        });

        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "You have clicked on: " + FLAG_END_DATE);
                mDatePickerDialog.getDatePicker().setTag(FLAG_END_DATE);
                mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePickerDialog.show();
            }
        });

        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mStartDate = editStartDate.getText().toString();
                String mEndDate = editEndDate.getText().toString();
                if(mStartDate.length() != 0 && mEndDate.length() != 0)
                    populateReportListView();
                else
                    toastMessage("You must select report start and end date");
            }
        });
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            int tag = ((Integer)view.getTag());
            mCalender.set(Calendar.YEAR, year);
            mCalender.set(Calendar.MONTH, month);
            mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if(tag == FLAG_START_DATE) {
                addText(editStartDate);
            } else if(tag == FLAG_END_DATE) {
                addText(editEndDate);
            }
        }
    };

    private void addText(EditText editText) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        editText.setText(simpleDateFormat.format(mCalender.getTime()));
    }

    public void populateReportListView() {
        Log.d(TAG, "populateReportListView: Displaying Report in the ListView");

        String strStartDate = editStartDate.getText().toString();
        String strEndDate = editEndDate.getText().toString();

        //Get data and append to the list
        Cursor reportData = mDatabaseHelper.generateReports(strClassCode, strStartDate, strEndDate);
        /* Items from database is stored in this ArrayList variable */
        ArrayList<String> reportListData = new ArrayList<>();
        /* Loop through the ArrayList and add its values to the Cursor */
        if(reportData.getCount() == 0) {
            toastMessage("The database is empty.");
        } else {
            while (reportData.moveToNext()) {
                reportListData.add("Summary of Study Time Between " + strStartDate + " and " + strEndDate
                        + "\n\n\t\tClass: " + reportData.getString(1)
                        + "\n\t\tCode: " + reportData.getString(0)
                        + "\n\t\tTotal Hours Spent: " + reportData.getString(2)
                        + "\n\t\tActual Study Time: " + reportData.getString(3)
                );
            }
        }
        /* ArrayAdapter to set items to ListView */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reportListData);
        adapter.notifyDataSetChanged();

        /* Setting the adapter to the ListView */
        listView.setAdapter(adapter);
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
