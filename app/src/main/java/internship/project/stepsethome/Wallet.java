package internship.project.stepsethome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Wallet extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    TextView walletAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        walletAmt = findViewById(R.id.wallet_amount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = this.getSharedPreferences(this.getPackageName(),MODE_PRIVATE);
        walletAmt.setText(String.valueOf(sharedPref.getFloat("WALLET_BALANCE",0)));
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("WALLET_BALANCE")){
                    walletAmt.setText(String.valueOf(sharedPref.getLong("WALLET_BALANCE",0)));
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}
