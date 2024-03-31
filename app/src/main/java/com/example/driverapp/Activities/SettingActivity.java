package com.example.driverapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import android.os.Bundle;

import android.widget.RelativeLayout;

import android.widget.TextView;


import com.example.driverapp.R;



public class SettingActivity extends AppCompatActivity {

    TextView textName;
    RelativeLayout layoutCurrentOrder, layoutHistory, layoutWallet, layoutBack, layoutIncome;
    AppCompatButton buttonSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
        listener();
    }

    private void listener() {
        //toDo

    }
    private void init() {
        textName = findViewById(R.id.text_userName);
        layoutCurrentOrder = findViewById(R.id.layout_currentOrder);
        layoutHistory = findViewById(R.id.layout_History);
        layoutWallet = findViewById(R.id.layout_lWallet);
        buttonSignOut = findViewById(R.id.button_sign_out);
        layoutBack = findViewById(R.id.relative_back);
        layoutIncome = findViewById(R.id.layout_Income);
    }
}