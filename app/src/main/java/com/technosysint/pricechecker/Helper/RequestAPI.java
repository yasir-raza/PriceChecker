package com.technosysint.pricechecker.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.technosysint.pricechecker.DBHelper.PriceCheckerDatabase;
import com.technosysint.pricechecker.DBHelper.PriceCheckerProduct;
import com.technosysint.pricechecker.DBHelper.User;
import com.technosysint.pricechecker.Products;
import com.technosysint.pricechecker.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yasir.Raza on 10/23/2018.
 */

public class RequestAPI {
    private Context _context = null;
    private String _apiLink = "";
    private PriceCheckerDatabase db = null;
    public AsyncResponse delegate = null;
    private String _DataBaseKey = "";


    public RequestAPI(Context _context)
    {
        try {
            this._context = _context;
            SharedPreferences sh_pref = PreferenceManager.getDefaultSharedPreferences(_context);
            _apiLink = sh_pref.getString(_context.getString(R.string.pref_technosys_webapi_key), null);
            _DataBaseKey = sh_pref.getString(_context.getString(R.string.pref_technosys_Database_key), null);
            if(!_apiLink.endsWith("/"))
                _apiLink = _apiLink + "/";
            db = PriceCheckerDatabase.getINSTANCE(_context.getApplicationContext());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface AsyncResponse {
        void processFinish(Object output);
    }

    //Validating User on Splash Screen
    public void ValidateUser(User objUser, AsyncResponse delegate){
        this.delegate = delegate;
        taskCheckLogin obj = new taskCheckLogin();
        obj.execute(objUser);
    }

    //Request Login Api
    public void LoginApp(String username, String password, AsyncResponse delegate)
    {
        this.delegate = delegate;
        taskLoginAPP obj = new taskLoginAPP();
        obj.execute(username, password);
    }

    //Logout
    public void LogoutApp(AsyncResponse delegate){
        this.delegate = delegate;
        taskLogoutAPP obj = new taskLogoutAPP();
        obj.execute();
    }

    public void GetProduct(User objUser, String Barcode , AsyncResponse delegate){
        this.delegate = delegate;
        taskGetProduct obj = new taskGetProduct();
        obj.execute(objUser,Barcode);
    }

    public void GetProductImage(User objUser,int ItemId,AsyncResponse delegate){
        this.delegate = delegate;
        taskGetProductImage obj = new taskGetProductImage();
        obj.execute(objUser,ItemId);
    }

    private User LoginApp(String Username, String Password)
    {
        User objUser = null;
        try {
            String b64Login = Base64.encodeToString((Username + ":" + Password).getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);
            HttpClient mHttpClient = new DefaultHttpClient();
            String APILink = _apiLink + "TSBE/User/FSigninMobileApp";
            HttpPost mHttpPost = new HttpPost(APILink);
            mHttpPost.addHeader("Accept", "application/json");
            mHttpPost.addHeader("Content-Type", "application/json");
            mHttpPost.addHeader("ts", _DataBaseKey);
            mHttpPost.addHeader("app", "6289");
            mHttpPost.addHeader("Authorization", "Basic " + b64Login);
            ArrayList<NameValuePair> nvArray = new ArrayList<>();
            nvArray.add(new BasicNameValuePair("Browser", Build.BRAND));
            nvArray.add(new BasicNameValuePair("IsMobile","true"));
            nvArray.add(new BasicNameValuePair("Platform", Build.DEVICE));
            nvArray.add(new BasicNameValuePair("IPAddress",Build.ID));
            nvArray.add(new BasicNameValuePair("SessionId",Build.MODEL));
            //nvArray.add(new BasicNameValuePair("UserAgent",BASE_OS));
            nvArray.add(new BasicNameValuePair("DataEntryStatus","1"));
            mHttpPost.setEntity(new UrlEncodedFormEntity(nvArray));
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
            String result = EntityUtils.toString(mHttpResponse.getEntity());
            objUser = new User();
            objUser.ConvertJsonToUserLogin(result);
            objUser.setPassword(Password);
            try {
                HttpGet mHttpGet = new HttpGet(_apiLink + "TSBE/CompanyBranch/GetBranchInfo/" + objUser.getCompanyBranchId());
                mHttpGet.addHeader("Accept", "application/json");
                mHttpGet.addHeader("Content-Type", "application/json");
                mHttpGet.addHeader("UserId", Integer.toString(objUser.getUserId()));
                mHttpGet.addHeader("Token", objUser.getGUID());
                mHttpResponse = mHttpClient.execute(mHttpGet);
                result = EntityUtils.toString(mHttpResponse.getEntity());
                JSONObject jObj = new JSONObject(result);
                String image = jObj.getString("ImageBlock");
                if (image != null && !image.equals("null") && !image.isEmpty()) {
                    objUser.setBranchLogo(Base64.decode(image, Base64.DEFAULT));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PriceCheckerDatabase db = PriceCheckerDatabase.getINSTANCE(_context.getApplicationContext());
            db.userDao().deleteAll();
            db.userDao().insertUser(objUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objUser;
    }

    //Validatiing User Method
    private class taskCheckLogin extends AsyncTask<Object, Void, User> {
        @Override
        protected User doInBackground(Object... param) {
            User objUser = (User) param[0];
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(_apiLink + "TSBE/User/ValidateUser");
            try {
                mHttpGet.addHeader("Accept", "application/json");
                mHttpGet.addHeader("Content-Type", "application/json");
                mHttpGet.addHeader("UserId", Integer.toString(objUser.getUserId()));
                mHttpGet.addHeader("Token", objUser.getGUID());
                HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
                String result = EntityUtils.toString(mHttpResponse.getEntity());
                boolean isValid = false;
                if (result != null && !result.isEmpty()) {
                    JSONObject jObj = new JSONObject(result);
                    isValid = jObj.getBoolean("IsValid");
                }
                objUser.setIsAlreadyLogin(isValid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return objUser;
        }
        @Override
        protected void onPostExecute(User obj) {
            super.onPostExecute(obj);
            if(obj.getIsAlreadyLogin()) {
                delegate.processFinish(obj);
            }else {
                LoginApp(obj.getLoginId(), obj.getPassword(), delegate);
            }
        }
    }

    //Request Login Api Method
    private class taskLoginAPP extends AsyncTask<String, Void, User> {
        ProgressDialog dialog = new ProgressDialog(_context,R.style.SpinnerTheme);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setIndeterminateDrawable(_context.getResources().getDrawable(R.drawable.custom_progress_dialog));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected User doInBackground(String... param) {
            User objUser = null;
            try {
                String b64Login = Base64.encodeToString((param[0] + ":" + param[1]).getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);
                HttpClient mHttpClient = new DefaultHttpClient();
                String APILink = "";
                APILink = _apiLink + "TSBE/User/FSigninMobileApp";
                HttpPost mHttpPost = new HttpPost(APILink);
                mHttpPost.addHeader("Accept", "application/json");
                mHttpPost.addHeader("Content-Type", "application/json");
                mHttpPost.addHeader("ts", _DataBaseKey);
                mHttpPost.addHeader("app", "6289");
                mHttpPost.addHeader("Authorization", "Basic " + b64Login);
                ArrayList<NameValuePair> nvArray = new ArrayList<>();
                nvArray.add(new BasicNameValuePair("Browser",Build.BRAND));
                nvArray.add(new BasicNameValuePair("IsMobile","true"));
                nvArray.add(new BasicNameValuePair("Platform", Build.DEVICE));
                nvArray.add(new BasicNameValuePair("IPAddress",Build.ID));
                nvArray.add(new BasicNameValuePair("SessionId",Build.MODEL));
                //nvArray.add(new BasicNameValuePair("UserAgent",Build.VERSION.BASE_OS));
                nvArray.add(new BasicNameValuePair("DataEntryStatus","1"));
                mHttpPost.setEntity(new UrlEncodedFormEntity(nvArray));
                HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
                String result = EntityUtils.toString(mHttpResponse.getEntity());
                objUser = new User();
                objUser.ConvertJsonToUserLogin(result);
                objUser.setPassword(param[1]);
                try {
                    HttpGet mHttpGet = new HttpGet(_apiLink + "TSBE/CompanyBranch/GetBranchInfo/" + objUser.getCompanyBranchId());
                    mHttpGet.addHeader("Accept", "application/json");
                    mHttpGet.addHeader("Content-Type", "application/json");
                    mHttpGet.addHeader("UserId", Integer.toString(objUser.getUserId()));
                    mHttpGet.addHeader("Token", objUser.getGUID());
                    mHttpResponse = mHttpClient.execute(mHttpGet);
                    result = EntityUtils.toString(mHttpResponse.getEntity());
                    JSONObject jObj = new JSONObject(result);
                    String image = jObj.getString("ImageBlock");
                    if (image != null && !image.equals("null") && !image.isEmpty()) {
                        objUser.setBranchLogo(Base64.decode(image, Base64.DEFAULT));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PriceCheckerDatabase db = PriceCheckerDatabase.getINSTANCE(_context.getApplicationContext());
                db.userDao().deleteAll();
                db.userDao().insertUser(objUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return objUser;
        }
        @Override
        protected void onPostExecute(User objUser) {
            dialog.dismiss();
            super.onPostExecute(objUser);
            delegate.processFinish(objUser);
        }
    }

    //Request Logout Method
    private class taskLogoutAPP extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(_context,R.style.SpinnerTheme);
        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setIndeterminateDrawable(_context.getResources().getDrawable(R.drawable.custom_progress_dialog));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            boolean res = false;
            try {
                PriceCheckerDatabase db = PriceCheckerDatabase.getINSTANCE(_context.getApplicationContext());
                db.userDao().deleteAll();
                res = true;
//                db.feedbackQuestionDao().deleteAll();
//                db.feedbackOptionDao().deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Boolean obj) {
            dialog.dismiss();
            super.onPostExecute(obj);
            delegate.processFinish(obj);
        }
    }


    private class taskGetBarcode extends  AsyncTask<Object,Void,Void>{

        @Override
        protected Void doInBackground(Object... param) {
            User objUser =(User) param[0];
            HttpClient mHttpClient = new DefaultHttpClient();
            return null;
        }
    }
    private class taskGetProduct extends AsyncTask<Object, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(_context,R.style.SpinnerTheme);

        @Override
        protected void onPreExecute() {
            dialog.setCancelable(false);
            dialog.setIndeterminateDrawable(_context.getResources().getDrawable(R.drawable.custom_progress_dialog));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... param) {
            User objUser =(User) param[0];
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(_apiLink + "TSBE/PriceChecker/GetPriceCheckerProduct/"+param[1]);
            try {
                mHttpGet.addHeader("Accept", "application/json");
                mHttpGet.addHeader("Content-Type", "application/json");
                mHttpGet.addHeader("UserId", Integer.toString(objUser.getUserId()));
                mHttpGet.addHeader("Token", objUser.getGUID());
                HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
                if(mHttpResponse.getStatusLine().getStatusCode() == 200) {
                    String result = EntityUtils.toString(mHttpResponse.getEntity());
                    if (result != null && !result.isEmpty()) {
                        PriceCheckerDatabase db = PriceCheckerDatabase.getINSTANCE(_context.getApplicationContext());
                        db.PriceCheckerProductsDao().deleteAll();
                        JSONArray jsonArray = new JSONArray(result);
                        int length = jsonArray.length();
                        for (int i = 0; i < length ; i++) {
                            PriceCheckerProduct q = new PriceCheckerProduct();
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            q.ConvertJSON(jObj);
                            db.PriceCheckerProductsDao().insertProducts(q);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void obj) {
            dialog.dismiss();
            super.onPostExecute(obj);
            delegate.processFinish(obj);
        }
    }


    private class taskGetProductImage extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... param) {
            User objUser =(User) param[0];
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(_apiLink + "TSBE/PriceChecker/GetPriceCheckerProductImage/"+param[1]);
            try {
                mHttpGet.addHeader("Accept", "application/json");
                mHttpGet.addHeader("Content-Type", "application/json");
                mHttpGet.addHeader("UserId", Integer.toString(objUser.getUserId()));
                mHttpGet.addHeader("Token", objUser.getGUID());
                HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
                if(mHttpResponse.getStatusLine().getStatusCode() == 200) {
                    String result = EntityUtils.toString(mHttpResponse.getEntity());
                    if (result != null && !result.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(result);
                            PriceCheckerProduct q = new PriceCheckerProduct();
                            q.ConvertJSON(jsonObject);
                            db.PriceCheckerProductsDao().updateDisplayImage(q.getDisplayImage(),q.getImageItemId());
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void obj) {
            super.onPostExecute(obj);
            delegate.processFinish(obj);
        }
    }
}
