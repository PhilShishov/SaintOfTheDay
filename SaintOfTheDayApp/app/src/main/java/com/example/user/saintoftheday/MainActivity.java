package com.example.user.saintoftheday;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DatabaseHelper mDatabaseHelper;
    private TextView TodayDetail;
    private Button btnAdd, btnViewData,imageButton,btnToday;
    private EditText editName, editDate,editBio;
    private ImageView TodayImage;
    public static final int RequestPermissionCode = 7;
    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editName = (EditText) findViewById(R.id.editName);
        editDate= (EditText) findViewById(R.id.editDate);
        editBio= (EditText) findViewById(R.id.editBio);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnViewData = (Button) findViewById(R.id.btnViewData);
        imageButton= (Button) findViewById(R.id.imageButton);
        btnToday= (Button) findViewById(R.id.btnToday);
        TodayImage=(ImageView)findViewById(R.id.TodayImage);
        TodayDetail=(TextView)findViewById(R.id.TodayDetail);
        mDatabaseHelper = new DatabaseHelper(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (CheckingPermissionIsEnabledOrNot()) {
                Toast.makeText(MainActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                RequestMultiplePermission();

            }
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = editName.getText().toString();
                String newEntry2 = editDate.getText().toString();
                String newEntry3 = editBio.getText().toString();

                ByteArrayOutputStream output=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,output);
                byte[] img=output.toByteArray();
                if (editName.length() != 0&&editBio.length()!=0&&editDate.length()!=0) {
                    AddData(newEntry,newEntry2,newEntry3,img);
                    editName.setText("");
                    editBio.setText("");
                    editDate.setText("");
                } else {
                    toastMessage("You must put something in the text field!");
                }

            }
        });

        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
            }
        });
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(todayDate);

                Cursor data = mDatabaseHelper.getItemDate(thisDate);
                String itemDate = null;
                while(data.moveToNext()){
                    itemDate = data.getString(0);
                }


                Cursor data2 = mDatabaseHelper.getData();
                String name=null,date=null,bio=null;
                while(data2.moveToNext()){
                    if(itemDate.equals(data2.getString(2))){
                        name=data2.getString(1);
                        bio=data2.getString(3);
                        byte[] ii = data2.getBlob(4);
                        ByteArrayInputStream imageStream = new ByteArrayInputStream(ii);
                        Bitmap the = BitmapFactory.decodeStream(imageStream);
                        TodayImage.setImageBitmap(the);
                    }

                }

                String check="Name :"+name+" Date :"+itemDate+" Bio :"+bio;
                TodayDetail.setText(check);
                Toast.makeText(MainActivity.this, name+itemDate+bio, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if(bitmap!=null){
                    Toast.makeText(this, "Bitmap not null", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void RequestMultiplePermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean Write_Storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean Read_Storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Write_Storage&&Read_Storage) {

                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;
    }


    public void AddData(String newEntry,String newEntry2,String newEntry3,byte[] img) {
        boolean insertData = mDatabaseHelper.addData(newEntry,newEntry2,newEntry3,img);

        if (insertData) {
            toastMessage("Data Successfully Inserted!");
        } else {
            toastMessage("Something went wrong");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}


