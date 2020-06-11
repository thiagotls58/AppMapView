package com.thiagoluz.appmapview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvLatitude, tvLongitude, tvAltitude, tvStatus;
    private MapView mapView;

    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MarkerOptions markerOptions;
    private Bundle mapViewBundle;

    private PermissionsMarshmallow permissionsMashmallow = new PermissionsMarshmallow(this);
    private String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvAltitude = findViewById(R.id.tvAltitude);
        tvStatus = findViewById(R.id.tvStatus);
        mapView = findViewById(R.id.mapView);

        // Google Map
        mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        iniciarMapa();

        // Localização
        CheckPermissionGranted();
    }

    private void iniciarMapa() {
        // Google Map
        markerOptions = new MarkerOptions();
        mapView.setClickable(true);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void CheckPermissionGranted() {
        if (permissionsMashmallow.hasPermissions(PERMISSIONS)) {
            //  permission granted
        } else {
            // request permission
            permissionsMashmallow.requestPermissions(PERMISSIONS, 2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itStart:
                iniciarLocalizacao();
                tvStatus.setText("Monitorando");
                break;
            case R.id.itPause:
                tvStatus.setText("Pausado");
                mapView.onPause();
                break;
            case R.id.itStop:
                tvStatus.setText("Parado");
                mapView.onStop();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        gmap.setIndoorEnabled(true);
        UiSettings ponto = gmap.getUiSettings();
        ponto.setIndoorLevelPickerEnabled(true);
        ponto.setMyLocationButtonEnabled(true);
        ponto.setMapToolbarEnabled(true);
        ponto.setCompassEnabled(true);
        ponto.setZoomControlsEnabled(true);

    }

    private void mostrarApontadorNoMapa(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        markerOptions.position(latLng);
        gmap.addMarker(markerOptions);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void iniciarLocalizacao() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mostrarLocalizacao(location);
                mostrarApontadorNoMapa(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        iniciarMonitoramento();
    }

    private void mostrarLocalizacao(Location location) {
        tvLatitude.setText("" + location.getLatitude());
        tvLongitude.setText("" + location.getLongitude());
        tvAltitude.setText("" + location.getAltitude());
    }

    private void iniciarMonitoramento() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

}