package internship.project.stepsethome;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import internship.project.stepsethome.Services.GeofenceTransitionService;
import internship.project.stepsethome.Services.GeofencingAPI;
import internship.project.stepsethome.Services.GoogleMapsUtil;
import internship.project.stepsethome.Utils.AppPermissionChecker;
import internship.project.stepsethome.Utils.SharedPref;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    private Button showCountDown;
    private SharedPref sharedPref;
    private Snackbar snackBar;
    private boolean newUser;
    private GoogleMapsUtil googleMapsUtil;
    private GeofencingAPI geofencingAPI;
    private GeofencingClient geofencingClient;
    private PendingIntent geoFencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showCountDown = this.findViewById(R.id.bgn_cd);
        showCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nxtIntent = new Intent(MapsActivity.this, CountDown.class);
                startActivity(nxtIntent);
            }
        });
        showCountDown.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMapView();
    }

    @Override
    protected void onDestroy() {
        sharedPref.setNewSession(true);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
                handleCallPermissionResult(permissions, grantResults);
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void handleCallPermissionResult(String[] permissions, int[] grantResults) {
        if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addLocationOnMap();
        }else{
           snackBar = Snackbar.make(this.findViewById(R.id.map),R.string.error_permission_enabled,Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction("Action Message", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    snackBar.dismiss();
                }
            });
            snackBar.show();
        }
    }

    private void updateMapView(){

        sharedPref = SharedPref.getInstance(this);
        newUser = sharedPref.checkNewUser();

        if (newUser) {
            if (!AppPermissionChecker.isLocationPermissionGranted(this)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }else{
                addLocationOnMap();
            }
        } else {
            addPreviousLocationOnMap();
        }
    }
    private void addLocationOnMap(){
        googleMapsUtil = new GoogleMapsUtil(this,mMap,findViewById(R.id.map));
    }

    private void addPreviousLocationOnMap(){
        googleMapsUtil = new GoogleMapsUtil(this,mMap);
    }

    public void addGeofence() {
        geofencingAPI = new GeofencingAPI(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofencingClient.addGeofences(geofencingAPI.getGeofencingRequest(),getGeofencePendingIntent())
            .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("GEOFENCE","Added geofence successfully");
                    googleMapsUtil.drawGeofence();
                    showCountDown.setVisibility(View.VISIBLE);
                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   snackBar = Snackbar.make(findViewById(R.id.map),"Unable to create geo fence",Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Action Message", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Call your action method here
                            snackBar.dismiss();
                        }
                    });
                    snackBar.show();
                }
            });
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.d("GEOFENCE", "Creating Geofence PendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;
        Intent intent = new Intent(this, GeofenceTransitionService.class);
        geoFencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geoFencePendingIntent;
    }

    private void removeGeofence(){
        geofencingClient.removeGeofences(geoFencePendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("GEOFENCE","Removed geofence successfully");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("GEOFENCE","Failed to removed geofence successfully");
                    }
                });
    }


}
