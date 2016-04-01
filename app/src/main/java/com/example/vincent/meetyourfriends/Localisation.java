package com.example.vincent.meetyourfriends;

// IMPORTATIONS
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Classe pour la localisation
public class Localisation extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    // Déclaration des variables
    private GoogleMap mMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    private String eventNameTemp;
    private String eventName;
    private String eventDescription;
    private String eventLongitude;
    private String eventLatitude;
    private String day;
    private String month;
    private String year;
    private String hour;
    private String minute;
    private String mode;
    private ArrayList<String> listGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisation);

        // Notification pour Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Déclaration et affectation des préférences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Gestion de la barre d'actions
        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "#0A0A2A");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        boolean logo = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOGOAB, true);
        actionBar.setDisplayUseLogoEnabled(logo);
        actionBar.setDisplayShowHomeEnabled(true);

        // Appel de la fonction pour afficher le bouton retour sur la barre d'actions
        setupActionBar();

        // Déclaration et affectation de btn_find
        ImageButton btn_find = (ImageButton) findViewById(R.id.btn_find);

        // Définition du OnClickListener pour le bouton de la recherche
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Déclaration et affectation de etLocation
                EditText etLocation = (EditText) findViewById(R.id.et_location);

                // Récupération de la location dans un string
                String location = etLocation.getText().toString();

                // Check de la localisation
                if(location!=null && !location.equals("")){
                    // Création de la recherche
                    new GeocoderTask().execute(location);
                }
            }
        };

        // Affectation du OnClickListener au bouton
        btn_find.setOnClickListener(findClickListener);
    }

    // Méthode qui permet la recherche d'une adresse sur Google Map
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Déclaration et affectation des variables
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Récupération de 3 adresses qui correspondent à la location
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Retour de l'adresse
            return addresses;
        }

        // Méthode après la recherche
        @Override
        protected void onPostExecute(List<Address> addresses) {

            // En cas d'erreur
            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // On enlève tous les markers de la carte
            mMap.clear();

            // On ajoute le marker pour la bonne adresse
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Création du nouveau point
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());

                // Ajout du marker
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                mMap.addMarker(markerOptions);

                // Déplacement sur le point voulu
                if(i==0) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    // Méthode pour afficher le bouton retour dans la barre d'actions
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Méthode qui s'exécute lorsque l'utilisateur clique longtemps sur un point de la carte
    @Override
    public void onMapLongClick(LatLng point) {

        // Récupération des valeurs dans l'intent
        Intent intent = getIntent();
        eventNameTemp = intent.getStringExtra("eventNameTemp");
        eventName = intent.getStringExtra("eventName");
        eventDescription = intent.getStringExtra("eventDescription");
        day = intent.getStringExtra("day");
        month = intent.getStringExtra("month");
        year = intent.getStringExtra("year");
        hour = intent.getStringExtra("hour");
        minute = intent.getStringExtra("minute");
        listGuest = intent.getStringArrayListExtra("listGuest");
        mode = intent.getStringExtra("mode");

        // Affectation de la longitude et de la latitude
        eventLongitude = Double.toString(point.longitude);
        eventLatitude = Double.toString(point.latitude);

        // Insertion des valeurs dans le nouvel intent
        Intent intent1 = new Intent(this, CreateEvent.class);
        intent1.putExtra("mode", mode);
        intent1.putExtra("eventNameTemp", eventNameTemp);
        intent1.putExtra("eventName", eventName);
        intent1.putExtra("eventDescription", eventDescription);
        intent1.putExtra("eventLongitude", eventLongitude);
        intent1.putExtra("eventLatitude", eventLatitude);
        intent1.putExtra("day", day);
        intent1.putExtra("month", month);
        intent1.putExtra("year", year);
        intent1.putExtra("hour", hour);
        intent1.putExtra("minute", minute);
        intent1.putExtra("listGuest", listGuest);

        // Retour sur la création de l'événement
        startActivity(intent1);
    }

    // Méthode qui gère la navigation de la barre d'actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Action Retour
                Intent intent = new Intent(this, CreateEvent.class);
                Localisation.this.startActivity(intent);
                return true;
            case R.id.menu_sethybrid:
                // Action Type hybride
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.menu_setnormal:
                // Action Type normal
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_setsatellite:
                // Action Type satellite
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.menu_setterrain:
                // Action Type terrain
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.menu_showtraffic:
                // Action Montrer circulation
                mMap.setTrafficEnabled(true);
                return true;
            case R.id.menu_trafficoff:
                // Action Masquer circulation
                mMap.setTrafficEnabled(false);
                return true;
            case R.id.menu_zoomin:
                // Action Zoom avant
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                return true;
            case R.id.menu_zoomout:
                // Action Zoom arrière
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode qui gère la navigation de la barre d'actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Cette méthode affecte le menu map à la barre d'actions
        getMenuInflater().inflate(R.menu.map, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Méthode à l'initialisation de la Google Map
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapLongClickListener(this);

        // Ajout d'un marker sur la HES-SO Sierre
        LatLng hesSierre = new LatLng(46.2930614, 7.536943899999983);

        // Déplacement sur ce marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(hesSierre).zoom(4).build();
        mMap.addMarker(new MarkerOptions().position(hesSierre).title("HES-SO Sierre"));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}