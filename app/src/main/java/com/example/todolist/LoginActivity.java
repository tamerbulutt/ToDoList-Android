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

public class LoginActivity extends AppCompatActivity {

    EditText eTxtUserEmail , eTxtUserPassword;
    Button btnUserLogin;
    TextView txtRegister;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eTxtUserEmail = findViewById(R.id.eTxtUserEmail);
        eTxtUserPassword = findViewById(R.id.eTxtUserPassword);
        btnUserLogin = findViewById(R.id.btnUserLogin);
        txtRegister = findViewById(R.id.txtRegister);

        mAuth=FirebaseAuth.getInstance();

        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eTxtUserEmail.getText().toString().trim();
                String password = eTxtUserPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    eTxtUserEmail.setError("Email gerekli!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    eTxtUserPassword.setError("Şifre gerekli!");
                    return;
                }

                //authentice the user

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(LoginActivity.this, "Giriş yapıldı!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Hata!"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });
    }
}