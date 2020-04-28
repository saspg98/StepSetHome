package internship.project.stepsethome.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPref {

    private SharedPreferences pref;
    private static SharedPref instance  = null;
    private SharedPreferences.Editor editor;

    private static final String WALLET_BALANCE = "WALLET_BALANCE";
    private static final String NEW_SESSION = "NEW_SESSION";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String LOCATION_STATUS = "LOCATION_STATUS";
    private static final String LOCATION_PROVIDER = "LOCATION_PROVIDER";
    private static final String LAST_COUNTDOWN_TIME = "LAST_COUNTDOWN_TIME";
    private static final String PREV_DIFF = "PREV_DIFF";
    private static final String NEW_USER = "NEW_USER";

    public SharedPref(Context context) {
        pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SharedPref getInstance(Context context){
        if (null == instance) instance = new SharedPref(context);
        return instance;
    }

    public void setPrevDiff(long diff){
        editor.putLong(PREV_DIFF,diff);
        editor.commit();
    }
    public long getPrevDiff(){
        return pref.getLong(PREV_DIFF,0);
    }

    public boolean checkNewUser(){
        return pref.getBoolean(NEW_USER,true);
    }
    public void setNewUser(){
        editor.putBoolean(NEW_USER,false);
        editor.commit();
    }

    public boolean checkNewSession(){
        return pref.getBoolean(NEW_SESSION,true);
    }
    public void setNewSession(boolean bool){
        editor.putBoolean(NEW_SESSION,bool);
        editor.commit();
    }
    public float getWallet_balance(){
        float wallet = pref.getFloat(WALLET_BALANCE,0);
        Log.d("WALLET","Amount in wallet" + wallet);
        return wallet;
    }
    public void setWallet_balance(float balance){
        Log.d("WALLET","Amount added successfully");
        editor.putFloat(WALLET_BALANCE,balance);
        editor.commit();
    }

    public void setLatitude(double latitude){
        Log.d("PREFS","Latitude added");
        editor.putString(LATITUDE, String.valueOf(latitude));
        editor.commit();
    }
    public String getLatitude() {
        String str = pref.getString(LATITUDE, null);
        Log.d("PREFS","Latitude = "+str);
        return str;
    }

    public void setLongitude(double longitude){
        Log.d("PREFS","Longitude added");
        editor.putString(LONGITUDE, String.valueOf(longitude));
        editor.commit();
    }
    public String getLongitude() {
        String str = pref.getString(LONGITUDE, null);
        Log.d("PREFS","Longitude = "+str);
        return str;
    }

    public void setLocationProvider(String locationProvider){
        editor.putString(LOCATION_PROVIDER,locationProvider);
        editor.commit();
    }
    public String getLocationProvider(){ return pref.getString(LOCATION_PROVIDER,null);}

    public void setLastCountDownTime(long endTime){
        Log.d("PREFS","CountDown Time Last = "+ endTime);
        editor.putLong(LAST_COUNTDOWN_TIME, endTime);
        editor.commit();
    }
    public long getLastCountDownTime() {
        return pref.getLong(LAST_COUNTDOWN_TIME,0);
    }

    public void setLocationStatus(boolean status){
        Log.d("PREFS","In home = "+ status);
        editor.putBoolean(LOCATION_STATUS,status);
        editor.commit();
    }
    public boolean getLocationStatus(){
        boolean status = pref.getBoolean(LOCATION_STATUS,false);
        Log.d("PREFS","In home = "+ status);
        return status;
    }
    /**
     * Method call when user log-out of application
     */
    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}