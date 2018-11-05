package com.technosysint.pricechecker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.technosysint.pricechecker.DBHelper.PriceCheckerDatabase;
import com.technosysint.pricechecker.DBHelper.User;
import com.technosysint.pricechecker.Helper.BounceInterpolator;
import com.technosysint.pricechecker.Helper.RequestAPI;

public class SplashActivity extends AppCompatActivity {

    private boolean internetConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            ImageView logo = findViewById(R.id.splash_activity_logo);
            Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            BounceInterpolator interpolator = new BounceInterpolator(0.3, 20);
            myAnim.setInterpolator(interpolator);
            logo.startAnimation(myAnim);
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            internetConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (!internetConnected) {
                Toast barcodeToast = Toast.makeText(getApplicationContext(),"Internet not connected", Toast.LENGTH_SHORT);
                barcodeToast.setGravity(Gravity.CENTER,0,0);
                barcodeToast.show();
            }
            final Context context = this;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (internetConnected) {
                        PriceCheckerDatabase db = PriceCheckerDatabase.getINSTANCE(context);
                        User objUser = db.userDao().getTopRecord();
                        if (objUser != null && objUser.getUserId() > 0) {
                            RequestAPI requestAPI = new RequestAPI(context);
                            requestAPI.ValidateUser(objUser, new RequestAPI.AsyncResponse() {
                                @Override
                                public void processFinish(Object output) {
                                    if (output != null) {
                                        callFeedbackActivity();
                                    } else {
                                        callMainActivity();
                                    }
                                }
                            });
                        } else {
                            callMainActivity();
                        }
                    } else {
                        callMainActivity();
                    }
                }
            }, 1500);
        }catch (Exception e) {
            callMainActivity();
            e.printStackTrace();
        }
    }
    private void callMainActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    private void callFeedbackActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
