package com.android.school_hour_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManageClassActivity extends AppCompatActivity {
    private static final String TAG = "ManageClassActivity";

    Button btnUpdate, btnDelete;
    EditText classId, classText;
    ClassDatabaseHelper mDatabaseHelper;

    private int numClassId;
    String strClassCode, strClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class);

        /* Reference to the Buttons of the layout activity_edit_class.xml */
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDel);

        /* Reference to the EditText of the layout activity_edit_class.xml */
        classId = (EditText) findViewById(R.id.editClassCode);
        classText = (EditText) findViewById(R.id.editClassName);

        /* Initialize Database Helper Class */
        mDatabaseHelper = new ClassDatabaseHelper(this);

        // Get Intent from the MainActivity
        Intent intent = getIntent();

        // Get Class ID passed from an extra
        numClassId = intent.getIntExtra("classId", -1);
        strClassCode = intent.getStringExtra("classCode");
        strClassName = intent.getStringExtra("className");

        /* Pass text to the EditText fields */
        classId.setText(strClassCode);
        classText.setText(strClassName);

        /*
         * Defining a click event listener for the button "Update Class"
         */
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classCode = classId.getText().toString();
                String className = classText.getText().toString();

                if((!classCode.equals("")) && (!className.equals(""))) {
                    mDatabaseHelper.updateClassData(numClassId, classCode, className);
                    toastMessage("Class has been updated with new data");
                    Log.d(TAG, "onBtnUpdate: Class has been updated with new data");
                } else {
                    toastMessage("You must provide course code and course name");
                }
            }
        });

        /*
         * Defining a click event listener for the button "Delete Class"
         */
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteClassData(numClassId);
                classId.setText("");
                classText.setText("");
                Log.d(TAG, "onBtnDelete: Class has been updated with new data");
                toastMessage("Class has been deleted from the database");
            }
        });
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
