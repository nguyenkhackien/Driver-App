package com.example.driverapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.driverapp.R;
import com.example.driverapp.models.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {
    TextView switchToLogin,uploadImg;
    ImageView imgProfile;
    EditText editName,editPhoneNumber,editEmail,editPassword,editPasswordAgain;
    Driver driver;
    AppCompatButton buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        init();
        listener();
    }

    private void listener(){
        switchToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            switchToLogin.setEnabled(false);
            finish();
        });

        buttonSignUp.setOnClickListener(view -> {
            String name = editName.getText().toString();
            String phone = editPhoneNumber.getText().toString();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            String passwordAgain = editPasswordAgain.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "Your name must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Your phone number must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Your email must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                    Toast.makeText(this, "Your email is invalid!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Your password must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passwordAgain.isEmpty()) {
                Toast.makeText(this, "Please retype your password!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!(password.equals(passwordAgain))) {
                Toast.makeText(this, "Your password and your confirm password are not matched!", Toast.LENGTH_SHORT).show();
                return;
            }

            driver = new Driver(null,name,phone,email,null,null);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        driver.setId(mAuth.getCurrentUser().getUid());
                        db.getReference().child("user").child(driver.getId()).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buttonSignUp.setEnabled(true);
                            }
                        });
                    }
                }
            });
        });
    }
    private void init(){
        uploadImg = findViewById(R.id.text_uploadImage);

        imgProfile = findViewById(R.id.img_profile);

        editName = findViewById(R.id.edit_name);
        editPhoneNumber = findViewById(R.id.edit_phoneNumber);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editPasswordAgain = findViewById(R.id.edit_password_again);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        switchToLogin = findViewById(R.id.text_switchToLogin);
    }
}