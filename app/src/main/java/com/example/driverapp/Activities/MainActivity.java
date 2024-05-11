package com.example.driverapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverapp.models.Trip;
import com.example.driverapp.tools.Const;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView imgProfile;
    FrameLayout btnWorking, buttonCurrentOrder, buttonHistory, buttonIncome, buttonWallet, buttonSetting;

    public static boolean pickUpActivityIsStart = false;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setStatusBarColor();
        init();
        getDriverInfo();
        listener();

    }
    private void getDriverInfo() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child("drivers")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Driver driver = snapshot.getValue(Driver.class);
                        if (driver != null) {
                            Picasso.get().load(driver.getDriverImageUrl())
                                    .resize(1000, 1000)
                                    .centerCrop().into(imgProfile);

                            checkVehicleRegistration(driver);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void checkVehicleRegistration(Driver driver) {
        if (driver.getVehicleId() == null) {
            Intent intent = new Intent(MainActivity.this, RegisterVehicleActivity.class);
            startActivity(intent);
            finish();
        }
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
        btnWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WorkingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        buttonWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderHistory.class);
                startActivity(intent);
            }
        });
        buttonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DailyIncome.class);
                startActivity(intent);
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        buttonCurrentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference().child("Trips")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Trip trip = dataSnapshot.getValue(Trip.class);
                                    if (trip != null) {
                                        if (trip.getDriverId() != null) {
                                            if (trip.getDriverId().equals(driverId)
                                                    && !trip.getStatus().equals(Const.searching)
                                                    && !trip.getStatus().equals(Const.driverFound)
                                                    && !trip.getStatus().equals(Const.waitingForAccept)
                                                    && !trip.getStatus().equals(Const.canceled)
                                                    && !trip.getStatus().equals(Const.success)
                                                    && !trip.getStatus().equals(Const.cancelByPassenger)
                                                    && !trip.getStatus().equals(Const.cancelByDriver)
                                                    && !PickUpActivity.isRunning) {
                                                Intent intent = new Intent(MainActivity.this, PickUpActivity.class);
                                                intent.putExtra("tripId", trip.getId());
                                                startActivity(intent);
                                                progressDialog.dismiss();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "No current order!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }
    private void setStatusBarColor() {
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue_toolbar));
    }
}