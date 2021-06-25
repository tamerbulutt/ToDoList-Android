package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView taskList;
    TextView txtYapılacaklar;
    Button btnExit, btnAccount;
    FloatingActionButton btnAddTask;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mDetails;
    ArrayList<Task> taskListObject;
    ArrayList<String> keyList;
    TasksToListView tasksToListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskList = findViewById(R.id.taskList); //Taskların gösterileceği listview tanımlaması
        taskListObject = new ArrayList<>();     //Taskların uzak sunucudan çekileceği liste tanımlaması
        keyList = new ArrayList<>();            //Task keylerinin saklandığı listenin tanımlaması
        btnExit = findViewById(R.id.btnExit); //Çıkış butonu tanımlaması
        btnAddTask = findViewById(R.id.btnAddTask); //Task ekleme butonu
        btnAccount = findViewById(R.id.btnAccount);
        txtYapılacaklar = findViewById(R.id.txtYapılacaklar);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid(); //Aktif kullanıcının id'sini alıyoruz.
        //Aktif kullanıcının tüm tasklerini tutan veritabanı bölümünün referansını değişkene aktarıyoruz.

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Tasks");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskListObject.clear();
                keyList.clear();
                //Eğer task var ise...
                if (snapshot.hasChildren()) {
                    for (DataSnapshot tasksSnapshot : snapshot.getChildren()) {
                        //...tüm taskler Task nesnesine map edilir ve listeye eklenir.
                        taskListObject.add(tasksSnapshot.getValue(Task.class));
                        keyList.add(tasksSnapshot.getKey());
                    }
                }
                //Taskları listview'a aktaracak sınıfın nesne tanımlaması
                tasksToListView = new TasksToListView(ReturnList(taskListObject,0),
                            ReturnList(taskListObject,1),MainActivity.this);
                taskList.setAdapter(tasksToListView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),TaskDetailsActivity.class);
                intent.putExtra("taskKey",keyList.get(position));
                //Toast.makeText(MainActivity.this, keyList.get(position), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CreateTaskActivity.class));
                finish();
            }
        });

        //Çıkış butonuna tıklandığında çıkış yapılıp login ekranına dönülmesini sağlıyor.
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
            }
        });

    }

    //Task nesnelerinin içindeki değişkenleri listelere ayıran metot BAŞLANGIÇ
    public ArrayList<String> ReturnList(ArrayList<Task> list,int preference)
    {
        ArrayList<String> tmpList = new ArrayList<>();
        for (Task task:list) {
            if (preference == 0)
                tmpList.add(task.title);
            else if (preference == 1) {
                if (task.content.length() > 40) {
                    task.content = task.content.substring(0,40);
                    task.content += "...";
                }
                tmpList.add(task.content);
            }
        }
        return tmpList;
    }
    //Task nesnelerinin içindeki değişkenleri listelere ayıran metot BİTİŞ

}