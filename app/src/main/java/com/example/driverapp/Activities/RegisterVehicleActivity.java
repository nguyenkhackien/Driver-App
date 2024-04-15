package com.example.driverapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverapp.R;
import com.example.driverapp.models.Driver;
import com.example.driverapp.models.Vehicle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class RegisterVehicleActivity extends AppCompatActivity {

    ImageView imageVehicle;
    TextView textUploadImage;
    EditText edtName, edtPlateNumber;
    RadioButton radioButtonCar, radioButtonMotorbike;
    RadioGroup radioGroup;
    AppCompatButton buttonRegister;

    Vehicle vehicle;

    Uri vehicleImg;

    String vehicleType;

    Driver driver;
    final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_vehicle);
        init();
        listener();
    }
    private void init(){
        imageVehicle = findViewById(R.id.img_vehicle);
        textUploadImage = findViewById(R.id.text_uploadVehicleImage);
        edtName = findViewById(R.id.edt_name);
        edtPlateNumber = findViewById(R.id.edt_plate);
        radioButtonCar = findViewById(R.id.radio_car);
        radioButtonMotorbike = findViewById(R.id.radio_motorbike);
        buttonRegister = findViewById(R.id.btnRegister);
        radioGroup = findViewById(R.id.group_selectType);
        vehicle = new Vehicle();
    }

    private void listener(){
        textUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,PICK_IMAGE_REQUEST);
        });
        radioButtonCar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    vehicleType = "car";
                }
            }
        });
        radioButtonMotorbike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    vehicleType = "motorbike";
                }
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String plateNumber = edtPlateNumber.getText().toString();

                if (vehicleImg == null) {
                    Toast.makeText(getApplicationContext(), "Please select your vehicle Image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Your vehicle's name can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (plateNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Your vehicle's plate number can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (vehicleType == null) {
                    Toast.makeText(getApplicationContext(), "Please select your vehicle type!", Toast.LENGTH_SHORT).show();
                    return;
                }

                vehicle.setName(name);
                vehicle.setPlateNumber(plateNumber);
                vehicle.setType(vehicleType);
                vehicle.setDriverId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                StorageReference filePath = FirebaseStorage.getInstance().getReference("vehicleImages")
                        .child(System.currentTimeMillis() + ".jpg");
                StorageTask uploadTask = filePath.putFile(vehicleImg);
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

                        vehicle.setVehicleImageUrl(imgUrl);
                        FirebaseDatabase.getInstance().getReference().child("vehicles").child(vehicle.getDriverId()).setValue(vehicle);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        buttonRegister.setEnabled(true);
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        driver = snapshot.getValue(Driver.class);
                        driver.setVehicleId(vehicle.getPlateNumber());
                        FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(driver);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent intent = new Intent(RegisterVehicleActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            vehicleImg =(Uri) data.getData();
            imageVehicle.setImageURI(vehicleImg);
            Toast.makeText(this, vehicleImg.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error, please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}