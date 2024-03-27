package com.example.driverapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.driverapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    TextView signUp;
    AppCompatButton login;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        init();
        listener();
    }

    private void listener(){
        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            signUp.setEnabled(false);
            finish();
        });

        login.setOnClickListener(view -> {
            String Email = email.getText().toString();
            String Password = password.getText().toString();
            if(Email.isEmpty()){
                Toast.makeText(this,"please enter your email",Toast.LENGTH_SHORT).show();
                return;
            } else if (!(Patterns.EMAIL_ADDRESS.matcher(Email).matches())) {
                Toast.makeText(this,"your email is invalid",Toast.LENGTH_SHORT).show();
                return;
            }
            if(Password.isEmpty()) {
                Toast.makeText(this, "please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        login.setEnabled(false);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"unsuccessful",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
    private void init(){
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);
        login = findViewById(R.id.buttonLogin);
        signUp = findViewById(R.id.sign_up);
        progressDialog = new ProgressDialog(LoginActivity.this);
    }
}