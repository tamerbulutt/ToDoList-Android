package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetailsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView txtTitle,txtContent,txtStartDate,txtEndDate;
    Button btnTaskCompleted;
    DatabaseReference taskReference;
    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        txtTitle = findViewById(R.id.txtTitleDt);
        txtContent = findViewById(R.id.txtContentDt);
        txtStartDate = findViewById(R.id.txtSDD);
        txtEndDate = findViewById(R.id.txtEDDt);
        btnTaskCompleted = findViewById(R.id.btnTaskCompleted);

        Intent intent = getIntent();
        //Seçilen task'in keyi intent ile alındı.
        String taskKey = intent.getStringExtra("taskKey");

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        //Key yardımı ile task'in db üzerindeki referansı bir değişkene aktarıldı.
        taskReference = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(userId)
                                        .child("Tasks").child(taskKey);
        taskReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //task firebase üzerinden çekilerek bir Task nesnesine aktarıldı.
                task = snapshot.getValue(Task.class);
                //Task bilgileri gerekli yerlere yazdırıldı.
                txtTitle.setText(task.title);
                txtContent.setText(task.content);
                txtStartDate.setText(task.startDate);
                txtEndDate.setText(task.endDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskDetailsActivity.this,
                        "Bilgiler getirilirken bir hata oluştu :(", Toast.LENGTH_LONG).show();
            }
        });

        btnTaskCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sil butonuna basınca task db'den silindi ve task listeleme ekranına yönlendirildi.
                taskReference.removeValue();
                Toast.makeText(TaskDetailsActivity.this,
                        "Task başarıyla silindi!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }
}