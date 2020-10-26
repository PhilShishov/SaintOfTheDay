package com.example.user.saintoftheday;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete, btnViewDet;
    private EditText editable_item1,editable_item2,editable_item3;

    DatabaseHelper mDatabaseHelper;

    private String selectedName;
    private int selectedID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnViewDet = (Button) findViewById(R.id.btnViewDet);
        editable_item1 = (EditText) findViewById(R.id.editable_item1);
        editable_item2 = (EditText) findViewById(R.id.editable_item2);
        editable_item3 = (EditText) findViewById(R.id.editable_item3);
        mDatabaseHelper = new DatabaseHelper(this);

        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");

        btnViewDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor data = mDatabaseHelper.getData();
                while(data.moveToNext()){
                    if(selectedID==data.getInt(0)){
                        String n,d,b;
                        n=data.getString(1);
                        d=data.getString(2);
                        b=data.getString(3);
                        Toast.makeText(EditDataActivity.this, "Name : "+n+" Date : "+d+" Bio : "+b, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        editable_item1.setText(selectedName);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editable_item1.getText().toString();
                String date = editable_item2.getText().toString();
                String bio = editable_item3.getText().toString();

                if(!name.equals("")&&!date.equals("")&&!bio.equals("")){
                    mDatabaseHelper.updateData(selectedID,name,date,bio);
                }else{
                    toastMessage("You must Fill ALL ");
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteData(selectedID,selectedName);
                editable_item1.setText("");
                editable_item2.setText("");
                editable_item3.setText("");

                toastMessage("removed from database");
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
