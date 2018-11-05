package com.technosysint.pricechecker;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technosysint.pricechecker.DBHelper.User;
import com.technosysint.pricechecker.Helper.RequestAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private EditText tvLoginId;
    private EditText tvPassword;
    Button btnLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvLoginId = findViewById(R.id.signin_txt_LoginId);
        tvPassword = findViewById(R.id.signin_txt_Password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    //Action bar Menu Code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        MenuItem sign_out = menu.findItem(R.id.custom_actionbar_signout);
        sign_out.setVisible(false);
        MenuItem home = menu.findItem(R.id.custom_actionbar_home);
        home.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.custom_actionbar_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void Login(){

        try{
            String loginId = tvLoginId.getText().toString();
            String password = tvPassword.getText().toString();

            if(loginId.equals(null) || loginId.equals("")) {
                tvLoginId.setError("Username Required");
                return;
            }
            if(password.equals(null) || password.equals("")) {
                tvPassword.setError("Password Required");
                return;
            }
                RequestAPI obj = new RequestAPI(this);
                obj.LoginApp(loginId, password, new RequestAPI.AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        if(output != null) {
                            User obj = (User)output;
                            if(obj.getUserId() > 0) {
                                Toast toast = Toasty.success(getApplicationContext(),"Login successful...",Toast.LENGTH_SHORT,true);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                finish();
                            }
                            else {
                                Toast toast = Toasty.error(getApplicationContext(),"Username/Password invalid",Toast.LENGTH_SHORT,true);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                }
                        }
                        else {
                            Toast toast = Toasty.info(getApplicationContext(),"Please check your configuration and internet setting...",Toast.LENGTH_SHORT,true);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
