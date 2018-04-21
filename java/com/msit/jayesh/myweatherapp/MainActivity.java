package com.msit.jayesh.myweatherapp;

import android.Manifest;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    EditText editText;

    Typeface weatherFont;
    String la,lo;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        gps = new GPSTracker(MainActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            la=Double.toString(latitude);
            lo=Double.toString(longitude);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        Button btnShowLocation = (Button) findViewById(R.id.button);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

                Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                    public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                        cityField.setText(weather_city);
                        updatedField.setText(weather_updatedOn);
                        detailsField.setText(weather_description);
                        currentTemperatureField.setText(weather_temperature);
                        humidity_field.setText("Humidity: " + weather_humidity);
                        pressure_field.setText("Pressure: " + weather_pressure);
                        weatherIcon.setText(Html.fromHtml(weather_iconText));
                    }
                });

                asyncTask.execute(la,lo);

            }
        });


        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                pressure_field.setText("Pressure: "+weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));
            }
        });

        asyncTask.execute(la,lo);
        //  asyncTask.execute("latitude", "longitude");

    }



    public void setVisibility(View view) {

        editText = (EditText) findViewById(R.id.editText);
        editText.setVisibility(View.VISIBLE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void performSearch() {
        String errorMessage = "";
        if (Geocoder.isPresent()) {
            try {
                String param = editText.getText().toString();
                Geocoder gc = new Geocoder(this);
                List<Address> addresses = gc.getFromLocationName(param, 5);

                if (addresses.size() > 0) {
                    double lat = addresses.get(0).getLatitude();
                    double lon = addresses.get(0).getLongitude();

                    Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
                        public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                            cityField.setText(weather_city);
                            updatedField.setText(weather_updatedOn);
                            detailsField.setText(weather_description);
                            currentTemperatureField.setText(weather_temperature);
                            humidity_field.setText("Humidity: " + weather_humidity);
                            pressure_field.setText("Pressure: " + weather_pressure);
                            weatherIcon.setText(Html.fromHtml(weather_iconText));
                        }
                    });
                    asyncTask.execute(String.valueOf(lat), String.valueOf(lon));

                }


            } catch (IOException e ) {
                e.printStackTrace();
                // Catch network or other I/O problems.
                Log.d("Input", "Service Not Available");
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                Log.e("Invalid", "Invalid_lat_long_used");
            }catch (NullPointerException e) {

                // Handle case where no address was found.
                Log.e("Noaddress", "Please enter a city name!");

            }
        }

    }
}


