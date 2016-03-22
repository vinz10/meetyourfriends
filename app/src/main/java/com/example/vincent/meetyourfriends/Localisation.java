package com.example.vincent.meetyourfriends;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Localisation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Show the Up button in the action bar.
        setupActionBar();

    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, CreateEvent.class);

                Localisation.this.startActivity(intent);
                return true;

            case R.id.menu_sethybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.menu_showtraffic:
                mMap.setTrafficEnabled(true);
                return true;

            case R.id.menu_zoomin:
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                return true;

            case R.id.menu_zoomout:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                return true;

            case R.id.menu_gotolocation:
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(-34, 151))
                        .zoom(17)
                        .bearing(90)
                        .tilt(30)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                return true;

            case R.id.menu_addmarker:
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-34, 151))
                        .title("Marker in Sydney")
                        .snippet("Sydney")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));
                return true;

            case R.id.menu_getcurrentlocation:
                //mMap.setMyLocationEnabled(true);
                return true;

            case R.id.menu_showcurrentlocation:
                Location myLocation = mMap.getMyLocation();

                LatLng myLatLng = new LatLng(myLocation.getLatitude(),
                        myLocation.getLongitude());

                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(myLatLng).zoom(17).bearing(90).tilt(30).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        //new MenuInflater(getApplication()).inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
