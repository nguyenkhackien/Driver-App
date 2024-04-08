package com.example.driverapp.tools;

public class Converter {
    public static float fromMPerSToKmPerHour(float speed) {
        float returnSpeed;
        returnSpeed = (float) (speed * 3.6);

        return returnSpeed;
    }
}
