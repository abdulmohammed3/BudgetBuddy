package com.example.budgetingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username,password;
    private Button regBtn;
    private TextView regQn;

    private FirebaseAuth mAuth;
    private ProgressDialog progDialouge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        regBtn = findViewById(R.id.regBtn);
        regQn = findViewById(R.id.regQn);

        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        ProgressDialog progDialouge=new ProgressDialog(this);

        regQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = username.getText().toString();
                String passwordString = password.getText().toString();


                if (TextUtils.isEmpty(emailString)){
                    username.setError("Username is required");
                }
                if (TextUtils.isEmpty(passwordString)){
                    password.setError("Password is required");
                }
                else{
                    progDialouge.setMessage("Registration in progress");
                    progDialouge.setCanceledOnTouchOutside(false);
                    progDialouge.show();

                    Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                progDialouge.dismiss();
                            }else{
                                Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progDialouge.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}