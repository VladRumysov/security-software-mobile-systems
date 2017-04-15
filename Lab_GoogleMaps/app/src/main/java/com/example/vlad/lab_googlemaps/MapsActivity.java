package com.example.vlad.lab_googlemaps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.*;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvGPSLatLng;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvNetLatLng;

    LocationManager locationManager;
    GoogleMap gMap;
    SendingData sendingData;

    String device_id;
    Boolean is_hash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvGPSLatLng = (TextView) findViewById(R.id.tvGPSLatLng);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvNetLatLng = (TextView) findViewById(R.id.tvNetLatLng);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(this);


        device_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        is_hash = false;
    }


//==========================================================================================

    protected void startThread() throws InterruptedException {
        HttpAsyncTask task = new HttpAsyncTask();
        task.execute();
    }

    class HttpAsyncTask extends AsyncTask<String, Void, String> {

        SendingData sendingData;

        @Override
        protected String doInBackground(String... urls) {
            try {
                while(true) {
                    Thread.sleep(10000);
                    sendingData = new SendingData();
                    sendingData.setDevice_id(device_id.toString());
                    sendingData.setCoords(tvNetLatLng.getText().toString());
                    sendingData.setIs_hash(is_hash);
                    ConnectAndRequestToServer con = new ConnectAndRequestToServer();
                    con.POST("http://openlab.hopto.org:3000/coords", sendingData);

                }
            }
            catch (Exception e){
                e.getMessage();
            }
            return "";
        }
    }


//==========================================================================================

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        // Новые данные о местоположении, объект Location.
        // Вызываем свой метод showLocation, который отобразит данные о местоположении.
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);

            //----- проверка...
            try {
                sendingData = new SendingData();
                sendingData.device_id = device_id;
                sendingData.coords = tvNetLatLng.getText().toString();
                sendingData.is_hash = is_hash;
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("device_id", sendingData.getDevice_id());
                jsonObject.accumulate("coords", sendingData.getCoords());
                jsonObject.accumulate("is_hash", sendingData.getIs_hash());
                Log.d(">>>>>>>", jsonObject.toString());

            //-----------------

                startThread();

            }
            catch(Exception e){
                e.getMessage();
            }
        }

        // Указанный провайдер был отключен юзером.
        // В этом методе вызываем свой метод checkEnabled, который обновит текущие статусы провайдеров.
        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        // Указанный провайдер был включен юзером. Тут также вызываем checkEnabled.
        // Далее методом getLastKnownLocation запрашиваем последнее доступное местоположение от включенного провайдера и отображаем его.
        // Оно может быть вполне актуальным, если до этого использовали какое-либо приложение с определением местоположения.
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        // Изменился статус указанного провайдера.
        // В поле status могут быть значения:
        // OUT_OF_SERVICE (данные будут недоступны долгое время),
        // (1) TEMPORARILY_UNAVAILABLE (данные временно недоступны),
        // (2) AVAILABLE (все ок, данные доступны).
        // В этом методе выводим новый статус.
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }
    };

    // На вход с помощью объекта Location определяется провайдера методом getProvider и отображаются координаты.
    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvGPSLatLng.setText(formatLocation(location));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvNetLatLng.setText(formatLocation(location));
        }
    }

    // На вход берет Location, читает из него данные и форматирует из них строку.
    // Данные: Latitude, Longitude, Time.
    private String formatLocation(Location location) {
        if (location == null)
            return "";

        return String.format(Locale.ENGLISH,
                "%(1.7f" + "," + "%(2.7f",
                location.getLatitude(), location.getLongitude()
        );
    }

    // Определяем включены или выключены провайдеры методом isProviderEnabled и отображаем эту инфу.
    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: " +
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: " +
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
    }

}


