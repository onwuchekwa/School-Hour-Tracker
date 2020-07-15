package com.android.school_hour_tracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ClassNavigationOptions extends AppCompatActivity {
    private static final String TAG = "ClassNavigationOptions";
    TextView lblClassCode, lblClassName;
    Button btnManageClass, btnRecordHours;
    String strClassCode, strClassName;
    int numClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_navigation_options);

        /* Reference to the TextView of the layout activity_class_navigation_options.xml */
        lblClassCode = (TextView) findViewById(R.id.tvLblClassCode);
        lblClassName = (TextView) findViewById(R.id.tvClassCode);

        /* Reference to the Button of the layout activity_class_navigation_options.xml */
        btnManageClass = (Button) findViewById(R.id.btnManage);
        btnRecordHours = (Button) findViewById(R.id.btnRecord);

        // Get Intent from the MainActivity
        Bundle intent = getIntent().getExtras();

        // Get Class ID passed from an extra
        if(intent != null) {
            numClassId = intent.getInt("classId", -1);
            strClassCode = intent.getString("classCode");
            strClassName = intent.getString("className");
        }

        /* Pass text to the EditText fields */
        if((!strClassCode.equals("")) && (!strClassName.equals(""))) {
            lblClassCode.setText(strClassCode);
            lblClassName.setText(strClassName);
        } else {
            toastMessage("There is no data returned from previous action.");
        }

        /**
         * Defining a click event listener for the button "btnRecordHours"
         */
        btnRecordHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numClassId > -1) {
                    Log.d(TAG, "onBtnRecordHours: The Class ID is: " + numClassId);
                    createIntent(HourLog.class);
                } else {
                    toastMessage("There is no ID associated with that class name!");
                }
            }
        });

        /**
         * Defining a click event listener for the button "btnManageClass"
         */
        btnManageClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numClassId > -1) {
                    Log.d(TAG, "onBtnManageClass: The Class ID is: " + numClassId);
                    createIntent(ManageClassActivity.class);
                } else {
                    toastMessage("There is no ID associated with that class name!");
                }
            }
        });
    }

    /**
     * Pass values across different Activities
     * @param nameOfClass class to receive intent
     */
    public void createIntent(Class nameOfClass) {
        Intent intent = new Intent(ClassNavigationOptions.this, nameOfClass);
        intent.putExtras(getIntent());
        intent.putExtra("classId", numClassId);
        intent.putExtra("classCode", strClassCode);
        intent.putExtra("className", strClassName);
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
