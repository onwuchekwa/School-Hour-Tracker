package com.android.school_hour_tracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManageClassActivity extends AppCompatActivity {
    private static final String TAG = "ManageClassActivity";

    Button btnUpdate, btnDelete;
    EditText classId, classText;
    DatabaseHelper mDatabaseHelper;

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
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        // Get Intent from the MainActivity
        Bundle intent = getIntent().getExtras();

        // Get Class ID passed from an extra
        if(intent != null) {
            numClassId = intent.getInt("classId", -1);
            strClassCode = intent.getString("classCode");
            strClassName = intent.getString("className");
        }

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
                    Intent intent = new Intent( ManageClassActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageClassActivity.this);
                builder.setTitle("Confirm Delete Class");
                builder.setMessage("Deleting this class will also delete all study records for this class. Are you sure you want to delete?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.deleteClassData(numClassId);
                        Log.d(TAG, "onBtnDelete: Class has been and study records has been deleted");
                        toastMessage("Class has been deleted from the database");
                        dialog.dismiss();
                        Intent intent = new Intent( ManageClassActivity.this, MainActivity.class);
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
        Intent intent = new Intent( ManageClassActivity.this, ClassNavigationOptions.class);
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
