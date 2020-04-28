package internship.project.stepsethome;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;


public class GeofencingAPI {

    private static final String GEOFENCE_REQ_ID = "SSH Geofence";
    private static final float GEOFENCE_RADIUS = 100.0f; // in meters
    private static final int DURATION = 120000;

    private Context context;
    private SharedPref sharedPref;
    private GeofencingRequest geofencingRequest;

    public GeofencingAPI(Context context){
        this.context = context;
        sharedPref = SharedPref.getInstance(context);
        double lat = Double.valueOf(sharedPref.getLatitude());
        double lng = Double.valueOf(sharedPref.getLongitude());
        geofencingRequest = createGeofenceRequest(createGeofence(lat,lng));
    }

    // Create a Geofence
    private Geofence createGeofence(double lat, double lng) {
        Log.d("GEOFENCE", "createGeofence");

        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( lat,lng, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(DURATION)
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d("GEOFENCE", "Creating Geofence Request");

        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofence(geofence)
                .build();
    }

    public GeofencingRequest getGeofencingRequest(){
        return geofencingRequest;
    }

}
