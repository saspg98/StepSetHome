package internship.project.stepsethome.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import internship.project.stepsethome.Utils.SharedPref;

public class GeofenceTransitionService extends IntentService {

    private static final String TAG = "GeoIntentService";
    private static final long DURATION = 120000;

    private SharedPref sharedPref;

    public GeofenceTransitionService() {
        super(TAG);
    }

    private void updateLocationStatus(boolean status) {
        if(status){
            Log.d("SERVICE", "In Home");
            if(sharedPref.checkNewUser()){
                sharedPref.setWallet_balance(0);
                sharedPref.setNewSession(false);
                sharedPref.setNewUser();
                sharedPref.setLastCountDownTime(new Date(System.currentTimeMillis()).getTime());
                sharedPref.setLocationStatus(true);
                sharedPref.setPrevDiff(0);
                return;
            }
                long cTime = new Date(System.currentTimeMillis()).getTime();
                long pTime = sharedPref.getPrevDiff();
                long lTime = sharedPref.getLastCountDownTime();
                long tTime = pTime + cTime - lTime;
                sharedPref.setLocationStatus(true);
            if (tTime >= DURATION) {
                sharedPref.setLastCountDownTime(lTime + tTime - (tTime % DURATION + pTime));
                sharedPref.setWallet_balance(sharedPref.getWallet_balance() - ((long) Math.floor(tTime / DURATION)) * 10);
                sharedPref.setPrevDiff(tTime % DURATION);
            }
//
        }else{
            Log.d("SERVICE", "Away Home");
            long cTime = new Date(System.currentTimeMillis()).getTime();
            long pTime = sharedPref.getPrevDiff();
            long lTime = sharedPref.getLastCountDownTime();
            long tTime = pTime + cTime - lTime;
            sharedPref.setLocationStatus(false);
            if (tTime >= DURATION) {
                sharedPref.setLastCountDownTime(lTime + tTime - (tTime % DURATION + pTime));
                sharedPref.setWallet_balance(sharedPref.getWallet_balance() + ((long) Math.floor(tTime / DURATION)) * 10);
                sharedPref.setPrevDiff(tTime % DURATION);
            }
        }
    }

    private boolean getGeofenceTransitionDetails(int geoFenceTransition) {
        boolean status = false;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = true;
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = false;
        return status;
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sharedPref = SharedPref.getInstance(this);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d(TAG, "geofencing event started!!");
        if (geofencingEvent == null) {
            Log.d(TAG, "geofencing event is null need to check again!!!");
            return;
        }
        if (geofencingEvent.hasError()) {
            String errorString = getErrorString(geofencingEvent.getErrorCode());
            Log.wtf(TAG, "GeofencingEvent error " + errorString);
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            boolean geofenceTransitionDetails = getGeofenceTransitionDetails(transaction);
            updateLocationStatus(geofenceTransitionDetails);
        }
    }
}
