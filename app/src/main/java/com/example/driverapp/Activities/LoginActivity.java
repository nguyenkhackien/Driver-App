package com.example.driverapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.models.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    AppCompatButton btnLogin;
    TextView txtSwitchToSignUp;
    String gEmail, gPassword;

    ProgressDialog progressDialog;
    HashMap<String, String> driverEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        getDriverEmails();
        listener();
    }

    private void listener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    loginUser(gEmail, gPassword);
                    btnLogin.setEnabled(false);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                }
            }
        });

        txtSwitchToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                txtSwitchToSignUp.setEnabled(false);
                finish();
            }
        });
    }

    private void loginUser(String gEmail, String gPassword) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(gEmail, gPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkVehicleRegistration();
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean checkInput() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            Toast.makeText(this, "Your email is invalid", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(driverEmails.containsKey(email))) {
            Toast.makeText(this, "Your email does not exist!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        gEmail = email;
        gPassword = password;
        return true;
    }

    private void init() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        btnLogin = findViewById(R.id.buttonLogin);

        txtSwitchToSignUp = findViewById(R.id.sign_up);

        progressDialog = new ProgressDialog(LoginActivity.this);
        driverEmails = new HashMap<>();
    }

    private void checkVehicleRegistration() {
        FirebaseDatabase.getInstance().getReference().child("Drivers")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Driver driver = snapshot.getValue(Driver.class);
                        assert driver != null;
                        Intent intent;
                        if (driver.getVehicleId() == null) {
                            intent = new Intent(LoginActivity.this, RegisterVehicleActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getDriverEmails() {
        FirebaseDatabase.getInstance().getReference().child("Drivers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        driverEmails = new HashMap<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Driver driver = dataSnapshot.getValue(Driver.class);
                            if (driver != null) {
                                if (!(driver.getEmail().isEmpty())) {
                                    driverEmails.put(driver.getEmail(), "1");
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtSwitchToSignUp.setEnabled(true);
    }
}