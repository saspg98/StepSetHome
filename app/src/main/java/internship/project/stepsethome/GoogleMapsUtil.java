package internship.project.stepsethome;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

public class GoogleMapsUtil {

    private static final double GEOFENCE_RADIUS = 100.0f;

    private GoogleMap mMap;
    private Context context;
    private View mView;
    private Snackbar snackBar;
    private SharedPref sharedPref;
    private Location location;
    private Marker marker;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION", String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                drawMarker(location);
                LocationManagerUtil.removeLocationListener(context,mLocationListener);
            } else {
                Log.d("LOCATION", "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public GoogleMapsUtil(Context context, GoogleMap mMap){
        this.location = null;
        this.mMap = mMap;
        this.context = context;
        sharedPref = SharedPref.getInstance(context);
        initMap();
        getPreviousLocation();
    }

    public GoogleMapsUtil(Context context, GoogleMap mMap,View mView){
        this.mMap = mMap;
        this.context = context;
        this.mView = mView;
        this.location = null;
        sharedPref = SharedPref.getInstance(context);
        initMap();
        getCurrentLocation();
    }

    private void initMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }

    private void getPreviousLocation() {
            String lat = sharedPref.getLatitude();
            String lng = sharedPref.getLongitude();
            if (lat != null && lng != null) {
                String provider = sharedPref.getLocationProvider();
                location = new Location(provider);
                location.setLatitude(Double.parseDouble(lat));
                location.setLongitude(Double.parseDouble(lng));
                Log.d("LOCATION",String.format("getPreviousLocation(%f, %f)", location.getLatitude(), location.getLongitude()));
                drawMarker(location);
            }else{
                Log.e("LOCATION","No location present in shared pref");
            }
    }

    private void getCurrentLocation() {

        boolean isGPSEnabled = LocationManagerUtil.isGpsEnabled(context);
        boolean isNetworkEnabled = LocationManagerUtil.isNetworkEnabled(context);

        if (!(isGPSEnabled || isNetworkEnabled)) {
            snackBar = Snackbar.make(mView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction("Action Message", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    snackBar.dismiss();
                }
            });
            snackBar.show();
        }
        else {
            if (isNetworkEnabled) {
                location = LocationManagerUtil.getLocation(context,mLocationListener,LocationManager.NETWORK_PROVIDER);
                if(location == null){
                    snackBar = Snackbar.make(mView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE);
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
            if (isGPSEnabled) {
                location = LocationManagerUtil.getLocation(context,mLocationListener,LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            Log.d("LOCATION",String.format("getCurrentLocation(%f, %f)", location.getLatitude(), location.getLongitude()));
            drawMarker(location);
        }
    }

    private void drawMarker(Location location) {

        sharedPref.setLatitude(location.getLatitude());
        sharedPref.setLongitude(location.getLongitude());
        sharedPref.setLocationProvider(location.getProvider());

        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 18));
            MapsActivity mapsActivity = (MapsActivity) context;
            mapsActivity.addGeofence();
        }
    }

    private Circle geoFenceLimits;
    public void drawGeofence() {
        Log.d("MAP", "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( marker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( GEOFENCE_RADIUS );
        geoFenceLimits = mMap.addCircle( circleOptions );
    }

}
