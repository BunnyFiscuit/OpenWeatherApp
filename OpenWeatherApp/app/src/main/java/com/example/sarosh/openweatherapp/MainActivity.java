package com.example.sarosh.openweatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.aksingh.owmjapis.api.APIException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();

    private ArrayList<String> weatherData = new ArrayList<>();

    private TextView cityName, cityTemp, cityHumidity, windSpeed;
    private EditText weatherLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.locationText);
        cityTemp = findViewById(R.id.currentTemp);
        cityHumidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.windSpeed);
        weatherLocation = findViewById(R.id.inputLocation);
        Button searchCity = findViewById(R.id.locationButton);

        searchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    WeatherModel.getInstance().setCity(weatherLocation.getText().toString());
                    updateInfo();
                } catch (APIException e){
                    showToast("Couldn't update city");
                    e.printStackTrace();
                }
            }
        });

        Button change = findViewById(R.id.tempSwitch);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    WeatherModel.getInstance().changeUnit();
                    updateInfo();
                } catch (APIException e) {

                }
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Connection().execute();

    }

    protected void updateInfo(){
        try {
            weatherData = WeatherModel.getInstance().getWeatherInfo();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cityName.setText(weatherData.get(0));
                    weatherLocation.setText(cityName.getText());
                    cityTemp.setText(weatherData.get(1));
                    cityHumidity.setText(weatherData.get(2));

                    windSpeed.setText(weatherData.get(3));


                    showToast("Weather data updated!");
                }
            });

        } catch (APIException e){
            System.err.println("Could not get information");
            e.printStackTrace();
        }
    }

    private void showToast(String message){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

    }

    private class Connection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            updateInfo();
            return null;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.v(TAG, "onStart()");
        updateInfo();
    }




}
