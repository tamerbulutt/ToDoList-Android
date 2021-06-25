package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText eTxtEmail,eTxtName,eTxtPassword;
    Button btnRegister;
    TextView txtZatenUyeyim;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseDatabase rootNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Widget tanımlamaları
        eTxtEmail = findViewById(R.id.eTxtEmail);
        eTxtName = findViewById(R.id.eTxtName);
        eTxtPassword = findViewById(R.id.eTxtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtZatenUyeyim = findViewById(R.id.txtZatenUyeyim);
        //Widget tanımlamaları bitiş

        mAuth=FirebaseAuth.getInstance();

        /*if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }*/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Textboxlardan name, email ve şifreyi alıyoruz.
                String name = eTxtName.getText().toString();
                String email = eTxtEmail.getText().toString().trim();
                String password = eTxtPassword.getText().toString().trim();

                //Email password doluluk kontrolü
                if(TextUtils.isEmpty(email)){
                    eTxtEmail.setError("Email gerekli!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    eTxtPassword.setError("Şifre gerekli!");
                    return;
                }

                //Email ve şifre ile yeni user oluşturuyoruz.
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = mAuth.getCurrentUser().getUid();

                            Integer cardCount = 1;

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String,String> userMap = new HashMap<>();

                            userMap.put("NAME",name);
                            userMap.put("cardCount",cardCount.toString());


                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Üyelik tamamlandı!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(RegisterActivity.this, "Hata!"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        txtZatenUyeyim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });;

    }
}