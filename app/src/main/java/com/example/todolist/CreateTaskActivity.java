package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class CreateTaskActivity extends AppCompatActivity {

    Button btnExitTask,btnAddTask,btnSSDate,btnSEDate;
    EditText etTitle,etDescription;
    DatePickerDialog.OnDateSetListener setListener,setListener2;
    CheckBox checkBoxYerelCihaz;
    // Firebase authenticaiton ve database parametreleri
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    String cardCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnExitTask = findViewById(R.id.btnExitTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnSSDate = findViewById(R.id.btnSSDate);
        btnSEDate = findViewById(R.id.btnSEDate);
        checkBoxYerelCihaz = findViewById(R.id.checkBoxYerelCihaz);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        btnExitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth+"/"+(month+1)+"/"+year;
                btnSSDate.setText(date);
                beginTime.set(year, month, dayOfMonth);
            }
        };
        setListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth+"/"+(month+1)+"/"+year;
                btnSEDate.setText(date);
                endTime.set(year, month, dayOfMonth);
            }
        };
        btnSSDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this,
                        R.style.Theme_AppCompat_DayNight_Dialog,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });
        btnSEDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this,
                        R.style.Theme_AppCompat_DayNight_Dialog,setListener2,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });

        ContentResolver cr = this.getContentResolver();

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid(); //Aktif kullanıcının id'sini alıyoruz.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put(CalendarContract.Events.TITLE,etTitle.getText().toString());
                cv.put(CalendarContract.Events.DESCRIPTION,etDescription.getText().toString());
                cv.put(CalendarContract.Events.DTSTART,beginTime.getTimeInMillis());
                cv.put(CalendarContract.Events.DTEND,endTime.getTimeInMillis());
                cv.put(CalendarContract.Events.CALENDAR_ID,1);
                cv.put(CalendarContract.Events.EVENT_TIMEZONE,Calendar.getInstance().getTimeZone().getID());
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI,cv);

                String valueTitle = etTitle.getText().toString();
                String valueContent = etDescription.getText().toString();
                String valueFirstDate = btnSSDate.getText().toString();
                String valueLastDate = btnSEDate.getText().toString();

                //db içindeki Tasks bölümünün referansı değişkene atıldı.
                DatabaseReference dbRef = mDatabase.child("Tasks").getRef();
                    Toast.makeText(CreateTaskActivity.this, dbRef.toString(), Toast.LENGTH_LONG).show();
                //Referansın işaret ettiği yere Task tipinde veri tutan obje kaydedildi.
                dbRef.push().setValue(new Task(valueTitle,valueContent,valueFirstDate,valueLastDate));

                if(checkBoxYerelCihaz.isChecked()){
                    //Yerel Cihaza Depolama kodları
                    SharedPreferences sharedPreferences = CreateTaskActivity.this.getSharedPreferences("com.example.todolist",MODE_PRIVATE);
                    String p = "+";
                    sharedPreferences.edit().putString("storedTask",
                            valueTitle+p+valueContent+p+valueFirstDate+p+valueLastDate).apply();
                    String mesaj = sharedPreferences.getString("storedTask","Bulunamadı..");
                }
                
                //Bildirim verildi.
                BildirimVer("Task Eklendi!","Task başarıyla takvime eklendi..");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });
    }
    public void BildirimVer(String bildirimBasligi, String bildirimText){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("001", "001", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("description");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(this,"001");
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(bildirimBasligi)
                    .setContentText(bildirimText)
                    .setPriority(Notification.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(001, builder.build());
        }

    }
}