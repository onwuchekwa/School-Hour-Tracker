package com.android.school_hour_tracker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    DatabaseHelper mDatabaseHelper;
    private Button btnAdd;
    private EditText classId;
    private EditText classText;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Reference to the EditText of the layout main.xml */
        classId = (EditText) findViewById(R.id.txtClassCode);

        /* Reference to the EditText of the layout main.xml */
        classText = (EditText) findViewById(R.id.txtClassName);

        /* Reference to the Button of the layout main.xml */
        btnAdd = (Button) findViewById(R.id.btnAddClass);

        /* Reference to the ListView of the layout main.xml */
        listView = (ListView) findViewById(R.id.lvClassList);

        /* Initialize Database Helper Class */
        mDatabaseHelper = new DatabaseHelper(this);
        populateListView();

        /**
         * Defining a click event listener for the button "Add"
         */
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String classCode = classId.getText().toString();
                String className = classText.getText().toString();
                if((classId.length() != 0) && (classText.length() != 0)) {
                    addNewClass(classCode, className);
                    classId.setText("");
                    classText.setText("");
                    populateListView();
                } else {
                    toastMessage("You must provide course code and course name");
                }
            }
        });

        /**
         * Defining a click event listener for the button "ListView"
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String classInfoData = parent.getItemAtPosition(position).toString();
                String className = classInfoData.substring(classInfoData.indexOf("-") + 1).trim();
                String classCode = classInfoData.substring(0, classInfoData.indexOf("-")).trim();
                Log.d(TAG, "You clicked on " + classCode + " and " + className);
                Cursor classIdData = mDatabaseHelper.getClassId(className);
                int singleClassId = -1;
                while (classIdData.moveToNext()) {
                    singleClassId = classIdData.getInt(0);
                }
                if(singleClassId > -1) {
                    Log.d(TAG, "onItemClick: The Class ID is: " + singleClassId);
                    Intent intent = new Intent(MainActivity.this, ClassNavigationOptions.class);
                    intent.putExtra("classId", singleClassId);
                    intent.putExtra("classCode", classCode);
                    intent.putExtra("className", className);
                    startActivity(intent);
                } else {
                    toastMessage("There is no ID associated with that class name!");
                }
            }
        });
    }

    /**
     *  this populates the ListView fro the database
     */
    public void populateListView() {
        Log.d(TAG, "populateListView: Displaying classes in the ListView");
        //Get data and append to the list
        Cursor classData = mDatabaseHelper.getAllClasses();

        /* Items from database is stored in this ArrayList variable */
        ArrayList<String> classListData = new ArrayList<>();
        /* Loop through the ArrayList and add its values to the Cursor */
        if(classData.getCount() == 0) {
            toastMessage("The database is empty.");
        } else {
            while (classData.moveToNext()) {
                classListData.add(classData.getString(1) + " - " + classData.getString(2));
            }
        }
        /* ArrayAdapter to set items to ListView */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classListData);
        adapter.notifyDataSetChanged();

        /* Setting the adapter to the ListView */
        listView.setAdapter(adapter);
    }

    /**
     * Insert class to the database
     * @param newClassId classCode
     * @param newClassName className
     */
    public void addNewClass(String newClassId, String newClassName) {
        boolean insertData = mDatabaseHelper.addClasses(newClassId, newClassName);
        if(insertData) {
            toastMessage("Class Successfully Inserted");
        } else {
            toastMessage("Something went wrong");
        }
    }

    /**
     * Customizable Toast
     * @param message Toast Message
     */
    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
