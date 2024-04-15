package com.example.driverapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.driverapp.R;
import com.example.driverapp.models.Driver;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {
    TextView switchToLogin,uploadImg;
    ImageView imgProfile;
    EditText editName,editPhoneNumber,editEmail,editPassword,editPasswordAgain;
    Driver driver;
    AppCompatButton buttonSignUp;

    Uri imageUri;

    HashMap<String, Boolean> driverEmails;
    final int PICK_IMAGE_REQUEST = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        init();
        listener();
    }

    private void listener(){
        uploadImg.setOnClickListener(View-> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
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

            if (imageUri == null) {
                Toast.makeText(this, "Please upload your image!", Toast.LENGTH_SHORT).show();
                return ;
            }
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
            } else  {
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
                Toast.makeText(this, "Your password are not matched!", Toast.LENGTH_SHORT).show();
                return;
            }

            driver = new Driver(null,name,phone,email,null,null);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        driver.setId(mAuth.getCurrentUser().getUid());

                        StorageReference filePath = FirebaseStorage.getInstance().getReference("DriverProfileImages")
                                .child(System.currentTimeMillis() + ".jpg");

                        //get image url
                        StorageTask uploadTask = filePath.putFile(imageUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                //upload post
                                Uri downloadUri = (Uri) task.getResult();
                                String imgUrl = downloadUri.toString();

                                driver.setDriverImageUrl(imgUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                buttonSignUp.setEnabled(true);
                            }
                        });

                        FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(driver.getId()).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
            Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Error, please try again!", Toast.LENGTH_SHORT).show();
        }
    }


}