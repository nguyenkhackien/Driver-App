package com.example.driverapp.Activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.squareup.picasso.Picasso;

import com.example.driverapp.R;
import com.example.driverapp.models.Driver;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView imgProfile;
    FrameLayout btnWorking, buttonCurrentOrder, buttonHistory, buttonIncome, buttonWallet, buttonSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();
        listener();
    }

    private void init(){
        imgProfile =findViewById(R.id.img_profile);
        FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Driver driver = snapshot.getValue(Driver.class);
                if (driver != null) {
                    Toast.makeText(getApplicationContext(),driver.getEmail(),Toast.LENGTH_SHORT).show();
                    Picasso.get().load(driver.getDriverImageUrl())
                            .resize(1000, 1000)
                            .centerCrop().into(imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnWorking = findViewById(R.id.buttonWorking);
        buttonCurrentOrder = findViewById(R.id.buttonCurrentOrder);
        buttonHistory = findViewById(R.id.buttonHistory);
        buttonIncome = findViewById(R.id.buttonInCome);
        buttonWallet = findViewById(R.id.buttonWallet);
        buttonSetting = findViewById(R.id.buttonSettings);
    }
    private void listener(){

    }
}