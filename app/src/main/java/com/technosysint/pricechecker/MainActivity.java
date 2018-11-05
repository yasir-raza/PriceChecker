package com.technosysint.pricechecker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.GetChars;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.technosysint.pricechecker.DBHelper.PriceCheckerDatabase;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private EditText barcodeEditText;
    private pl.droidsonroids.gif.GifImageView barcodeBtn;
    PriceCheckerDatabase db;
    String barcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = PriceCheckerDatabase.getINSTANCE(this.getApplicationContext());
        barcodeEditText = findViewById(R.id.barcode_text);
//        barcodeEditText.setFocusableInTouchMode(false);
//        barcodeEditText.setFocusable(false);
//        barcodeEditText.requestFocus();
        barcodeBtn = findViewById(R.id.arrowBtn);

        db.PriceCheckerProductsDao().deleteAll();

        barcodeBtn.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    barcodeEditText.setVisibility(View.GONE);
                    super.onLongPress(e);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Toast toast = Toasty.success(getApplicationContext(),"You can enter barcode manually...",Toast.LENGTH_SHORT,true);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    barcodeEditText.setVisibility(View.VISIBLE);
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        barcodeEditText.addTextChangedListener(generalTextWatcher);


    }


    private TextWatcher generalTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(barcodeEditText.getText().length() >= 6) {
                barcode = barcodeEditText.getText().toString();
                Intent i = new Intent(getApplicationContext(), Products.class);
                i.putExtra("Barcode", barcode);
                startActivity(i);
                finish();

            }

        }

    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(e.getAction()==KeyEvent.ACTION_DOWN){
            char pressedKey = (char) e.getUnicodeChar();
            barcode += pressedKey;
        }
        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Intent intent = new Intent(getApplicationContext(),Products.class);
            intent.putExtra("Barcode",barcode);
            startActivity(intent);
            finish();
            barcode="";

        }
        return super.dispatchKeyEvent(e);
    }
}
