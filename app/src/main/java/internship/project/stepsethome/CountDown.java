package internship.project.stepsethome;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import internship.project.stepsethome.Utils.SharedPref;

public class CountDown extends AppCompatActivity {

    private static final int DURATION = 120000;
    private TextView countdownTimerText;
    private CountDownTimer countDownTimer;
    SharedPref sharedPref;
    private long mTimeRemaining;
    private Button wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        sharedPref = SharedPref.getInstance(this);
        countdownTimerText = findViewById(R.id.countdownText);
        wallet = findViewById(R.id.wallet);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Wallet.class);
                startActivity(intent);
            }
        });
    }

    private void startTimer(long millis) {
        Log.e("TIMER","IN start timer");
        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countdownTimerText.setText(hms);
            }

            public void onFinish() {
                updateWallet();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TIMER","starting timer");
        updateWallet();
    }

    private void updateWallet() {
        long cTime = new Date(System.currentTimeMillis()).getTime();
        long pTime = sharedPref.getPrevDiff();
        long lTime = sharedPref.getLastCountDownTime();
        long tTime = pTime + cTime - lTime;
        if (sharedPref.getLocationStatus()) {
            if (tTime >= DURATION) {
                sharedPref.setWallet_balance(sharedPref.getWallet_balance() + ((long) Math.floor(tTime / (DURATION))) * 10);
                sharedPref.setLastCountDownTime(lTime + tTime - (tTime % DURATION + pTime));
                sharedPref.setPrevDiff(tTime % DURATION);
            }
        } else {
            if (tTime >= DURATION) {
                sharedPref.setWallet_balance(sharedPref.getWallet_balance() - ((long) Math.floor(tTime / (DURATION))) * 10);
                sharedPref.setLastCountDownTime(lTime + tTime - (tTime % DURATION + pTime));
                sharedPref.setPrevDiff(tTime % DURATION);
            }
        }
        cTime = new Date(System.currentTimeMillis()).getTime();
        mTimeRemaining = DURATION - (cTime - sharedPref.getLastCountDownTime());
        startTimer(mTimeRemaining);
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        countDownTimer = null;
    }

}
