package com.example.driverapp.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.driverapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkingActivity extends AppCompatActivity implements OnMapReadyCallback {
    AppCompatButton buttonEnableConnection;
    TextView textAvailableStatus;
    CircleImageView imgProfile, imgVehicle;
    TextView textVehicleName, textPlateNumber;
    AppCompatButton buttonIncome;

    public static GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_working);
        init();
        listener();
    }
    private void init() {
        buttonEnableConnection = findViewById(R.id.buttonEnable);
        buttonIncome = findViewById(R.id.btnInCome);
        textAvailableStatus = findViewById(R.id.text_status);
        textPlateNumber = findViewById(R.id.text_plateNumber);
        textVehicleName = findViewById(R.id.text_vehicleName);
        imgProfile = findViewById(R.id.img_profile);
        imgVehicle = findViewById(R.id.imgVehicle);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(this);
    }

    private void listener(){

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setRotateGesturesEnabled(false);
    }
}