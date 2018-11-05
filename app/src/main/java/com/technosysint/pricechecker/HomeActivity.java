package com.technosysint.pricechecker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.technosysint.pricechecker.Helper.RequestAPI;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {

    RequestAPI requestAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        requestAPI= new RequestAPI(this);
    }

    //Action bar Menu Code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        MenuItem home = menu.findItem(R.id.custom_actionbar_home);
        home.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.custom_actionbar_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.custom_actionbar_signout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);
            TextView question = mView.findViewById(R.id.custom_dialog_text);
            question.setText("Do you want to signout?");
            Button btnYes = mView.findViewById(R.id.custom_dialog_button_yes);
            btnYes.setText("");
            Button btnNo = mView.findViewById(R.id.custom_dialog_button_no);
            btnNo.setText("");
            builder.setView(mView);
            final AlertDialog dialog = builder.create();
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestAPI.LogoutApp(new RequestAPI.AsyncResponse() {
                        @Override
                        public void processFinish(Object output) {
                            Signout((Boolean) output);
                            dialog.dismiss();
                        }
                    });
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    protected void Signout(boolean val){
        try {
            if(val) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast barcodeToast = Toasty.error(getApplicationContext(),"Please try again",Toast.LENGTH_SHORT,true);
                barcodeToast.setGravity(Gravity.CENTER,0,0);
                barcodeToast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
