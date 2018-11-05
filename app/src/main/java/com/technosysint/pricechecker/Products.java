package com.technosysint.pricechecker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technosysint.pricechecker.DBHelper.PriceCheckerDatabase;
import com.technosysint.pricechecker.DBHelper.PriceCheckerProduct;
import com.technosysint.pricechecker.DBHelper.User;
import com.technosysint.pricechecker.Helper.RequestAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class Products extends AppCompatActivity {
    private TextView BarcodeTv;
    RequestAPI requestAPI;
    User objUser;
    PriceCheckerDatabase db;
    private List<PriceCheckerProduct> lFQ;
    private List<PriceCheckerProduct> pcp;
    TextView tvProductName,tvProductWeight,tvMarketPrice,tvSavePrice,tvOurPrice,tvDiscountOffer1,tvOffer1MarketPrice,tvOffer1OurPrice,tvDiscountOffer2,tvOffer2MarketPrice,tvOffer2OurPrice,tvBarcode;
    private TextView tvHorizontalLine;
    ImageView ivProductImage;
    int imageId;
    String prodcutBarcode = "";
    String oldbarcode = "";
    LinearLayout priceBoxLayout,discountBoxLayout,marketPriceLayout,ourPriceLayout;
    RelativeLayout imageBoxLayout;
    boolean isRunningTimer = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        db = PriceCheckerDatabase.getINSTANCE(this.getApplicationContext());
        objUser = db.userDao().getTopRecord();
        requestAPI= new RequestAPI(this);

        //TextBox Initialize
        tvProductName = findViewById(R.id.product_name);
        tvBarcode = findViewById(R.id.barcode_tv);
        tvProductWeight = findViewById(R.id.product_weight);
        tvMarketPrice = findViewById(R.id.market_price);
        tvSavePrice = findViewById(R.id.save_price);
        tvOurPrice = findViewById(R.id.tv_our_price);
        tvDiscountOffer1 = findViewById(R.id.tv_discount_offer1);
        tvOffer1MarketPrice = findViewById(R.id.iv_offer1_market_price);
        tvOffer1OurPrice = findViewById(R.id.iv_offer1_our_price);
        tvDiscountOffer2 = findViewById(R.id.tv_discount_offer2);
        tvOffer2MarketPrice = findViewById(R.id.iv_offer2_market_price);
        tvOffer2OurPrice = findViewById(R.id.iv_offer2_our_price);

        tvHorizontalLine = findViewById(R.id.horizontal_line);

        //ImageView
        ivProductImage = findViewById(R.id.product_image);

        //Layouts
        discountBoxLayout = findViewById(R.id.discount_box_layout);
        priceBoxLayout = findViewById(R.id.price_box_layout);
        marketPriceLayout = findViewById(R.id.market_price_layout);
        ourPriceLayout = findViewById(R.id.our_price_layout);





        if(oldbarcode == "") {
            Bundle b = getIntent().getExtras();
            prodcutBarcode = b.getString("Barcode");
            prodcutBarcode = prodcutBarcode.replaceAll("[^A-Za-z0-9]", "");
        }
        else
        {
            prodcutBarcode = oldbarcode;
        }
        if(!isRunningTimer == false)
            countDownTimer.start();
        try { setProduct(prodcutBarcode);}
        catch (Exception e) { e.printStackTrace(); }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toasty.normal(getApplicationContext(),"Back Pressed",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    CountDownTimer countDownTimer = new CountDownTimer(15000, 1000) {
        public void onTick(long millisUntilFinished) {
            isRunningTimer = true;
            //TODO: Do something every second
            Log.d("seconds remaining ", "seconds remaining: " + millisUntilFinished / 1000);
        }
        public void onFinish() {
            isRunningTimer = false;
            countDownTimer.cancel();
            Intent intent = new Intent(Products.this,  MainActivity.class);
            startActivity(intent);
            finish();

        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isRunningTimer == true) {
                countDownTimer.cancel();
                countDownTimer.start();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(isRunningTimer == true) {
                countDownTimer.cancel();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if(isRunningTimer == true) {
                countDownTimer.cancel();
            }
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(e.getAction()==KeyEvent.ACTION_DOWN){
            char pressedKey = (char) e.getUnicodeChar();
            prodcutBarcode += pressedKey;
        }
        if (e.getAction()==KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            prodcutBarcode = prodcutBarcode.replaceAll("[^A-Za-z0-9]","");
            try { setProduct(prodcutBarcode);}
            catch (Exception er) { er.printStackTrace(); }
            oldbarcode = prodcutBarcode;
            prodcutBarcode="";
            if (isRunningTimer == true) {
                countDownTimer.cancel();
                countDownTimer.start();
            }
        }
        return super.dispatchKeyEvent(e);
    }


    private void setProduct(String Barcode){

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ACCESSIBILITY, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        ivProductImage.setImageBitmap(null);
        ivProductImage.setBackground(getDrawable(R.drawable.basket_icon));
        db.PriceCheckerProductsDao().deleteAll();
        requestAPI.GetProduct(objUser, Barcode, new RequestAPI.AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                priceBoxLayout.setVisibility(View.VISIBLE);
                ivProductImage.setVisibility(View.VISIBLE);
                tvProductWeight.setVisibility(View.VISIBLE);
                priceBoxLayout.setVisibility(View.VISIBLE);
                tvBarcode.setVisibility(View.VISIBLE);
                marketPriceLayout.setVisibility(View.VISIBLE);
                discountBoxLayout.setVisibility(View.VISIBLE);
                tvDiscountOffer2.setVisibility(View.VISIBLE);
                tvOffer2MarketPrice.setVisibility(View.VISIBLE);
                tvOffer2OurPrice.setVisibility(View.VISIBLE);
                tvHorizontalLine.setVisibility(View.VISIBLE);
                tvDiscountOffer1.setVisibility(View.VISIBLE);
                tvOffer1MarketPrice.setVisibility(View.VISIBLE);
                tvOffer1OurPrice.setVisibility(View.VISIBLE);

                lFQ = db.PriceCheckerProductsDao().getAll();

                if(lFQ != null && lFQ.size() > 0){
                    if (lFQ != null && lFQ.size() == 1){
                        forOneProduct();
                    }
                    else if(lFQ != null && lFQ.size() >= 1)
                    {
                        final PriceCheckerProduct item = lFQ.get(0);
                        imageId = item.getImageItemId();
                        tvProductName.setText(item.getProductName());
                        tvProductWeight.setText("Weight : "+item.getQuantity());
                        tvMarketPrice.setText("Rs."+item.getSaleRate());
//                        tvMarketPrice.setPaintFlags(tvOffer1MarketPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvSavePrice.setText("Save Rs."+item.getDiscountAmount()+"/-");
                        tvOurPrice.setText("Rs."+item.getDiscountedPrice()+"/-");
                        tvBarcode.setText(item.getBarcode());

                        forSecondProduct();

                        if(lFQ != null && lFQ.size() > 2){
                            forThirdProduct();
                        }
                    }


                    RequestAPI obj = new RequestAPI(getApplicationContext());
                    obj.GetProductImage(objUser, imageId, new RequestAPI.AsyncResponse() {
                        @Override
                        public void processFinish(Object output) {
                            lFQ = db.PriceCheckerProductsDao().getAll();
                            if(lFQ.get(0).getDisplayImage() != null)
                            {
                            final PriceCheckerProduct item = lFQ.get(0);
                                Bitmap img = BitmapFactory.decodeByteArray(item.getDisplayImage(), 0, item.getDisplayImage().length);
                                img.setHasAlpha(true);
                                ivProductImage.setImageBitmap(img);
                                ivProductImage.setBackground(null);
                            }
                            else {
                                ivProductImage.setImageBitmap(null);
                                ivProductImage.setBackground(getDrawable(R.drawable.basket_icon));
                            }
                        }
                    });
                }
                else {
//                    Toast toast = Toasty.error(getApplicationContext(),"Invalid Barcode.. Please contact to the administrator.",Toast.LENGTH_SHORT,true);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();tvBarcode.setVisibility(View.GONE);
                    priceBoxLayout.setVisibility(View.GONE);
                    ivProductImage.setVisibility(View.GONE);
                    tvProductName.setText("Invalid Barcode.. Please contact to the administrator.");
                    tvProductName.setGravity(Gravity.CENTER);
                    tvProductWeight.setVisibility(View.GONE);

                }
            }
        });

    }

    private void forOneProduct(){
        discountBoxLayout.setVisibility(View.GONE);
        final PriceCheckerProduct item = lFQ.get(0);
        tvBarcode.setText(item.getBarcode());
        tvProductName.setText(item.getProductName());
        tvProductWeight.setText("Weight : "+item.getQuantity());
        marketPriceLayout.setVisibility(View.GONE);
        tvSavePrice.setText("");
        tvSavePrice.setVisibility(View.GONE);
        tvOurPrice.setText("Rs."+item.getSaleRate()+"/-");


    }

    private void forSecondProduct(){
        if(lFQ != null && lFQ.size() > 2){
            final PriceCheckerProduct item1 = lFQ.get(1);
            tvDiscountOffer1.setText("Buy "+item1.getSaleQuantity()+" - Get "+item1.getSaleDiscount().substring(0,item1.getSaleDiscount().indexOf("%"))+"% Discount");
            double discountTotalAmount = item1.getSaleRate() *  Double.parseDouble(item1.getSaleQuantity().substring(0,item1.getSaleQuantity().indexOf(" ")));
            tvOffer1MarketPrice.setText("Market Price: Rs."+discountTotalAmount+"/- | ");
            tvOffer1OurPrice.setText("Our Price: Rs."+item1.getDiscountedAmount());
//            tvOffer1Discount.setText("Discount "+item1.getSaleDiscount().substring(0,item1.getSaleDiscount().indexOf("%"))+"% ("+item1.getDiscountAmount()+")");
        }
        else{
            tvDiscountOffer1.setVisibility(View.GONE);
            tvOffer1MarketPrice.setVisibility(View.GONE);
            tvOffer1OurPrice.setVisibility(View.GONE);
            tvHorizontalLine.setVisibility(View.GONE);
        }
    }

    private void forThirdProduct(){
        if(lFQ != null && lFQ.size() >= 2){
            final PriceCheckerProduct item2 = lFQ.get(2);
            tvDiscountOffer2.setText("Buy "+item2.getSaleQuantity()+" - Get "+item2.getSaleDiscount().substring(0,item2.getSaleDiscount().indexOf("%"))+"% Discount");
            double discountTotalAmount = item2.getSaleRate() *  Double.parseDouble(item2.getSaleQuantity().substring(0,item2.getSaleQuantity().indexOf(" ")));
            tvOffer2MarketPrice.setText("Market Price: Rs."+discountTotalAmount+"/- | ");
            tvOffer2OurPrice.setText("Our Price: Rs."+item2.getDiscountedAmount());

        }
        else{
            tvDiscountOffer2.setVisibility(View.GONE);
            tvOffer2MarketPrice.setVisibility(View.GONE);
            tvOffer2OurPrice.setVisibility(View.GONE);
            tvHorizontalLine.setVisibility(View.GONE);
        }
    }
}
