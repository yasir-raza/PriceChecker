package com.technosysint.pricechecker.DBHelper;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Base64;

import org.json.JSONObject;

/**
 * Created by Yasir.Raza on 10/26/2018.
 */
@Entity
public class PriceCheckerProduct {
    @PrimaryKey(autoGenerate = true)
    private int _ProductItemId;
    private String _Barcode;
    private String _ProductName;
    private double _SaleRate;
    private String _OtherItemName;
    private int _ImageItemId;
    private byte[] _DisplayImage;
    private double _Quantity;
    private String _SaleQuantity;
    private String _SaleDiscount;
    private double _DiscountedPrice;
    private double _DiscountedAmount;
    private double _DiscountAmount;


    public int getProductItemId() {return _ProductItemId;}

    public void setProductItemId(int _ProductItemId) {this._ProductItemId = _ProductItemId; }

    public String getBarcode() {return _Barcode; }

    public void setBarcode(String _Barcode) {this._Barcode = _Barcode; }

    public String getProductName() {return _ProductName; }

    public void setProductName(String _ProductName) {this._ProductName = _ProductName; }

    public Double getSaleRate() { return _SaleRate; }

    public void setSaleRate(Double _SaleRate) { this._SaleRate = _SaleRate; }

    public String getOtherItemName() { return _OtherItemName;}

    public void setOtherItemName(String _OtherItemName) { this._OtherItemName = _OtherItemName; }

    public int getImageItemId() {  return _ImageItemId;}

    public void setImageItemId(int _ImageItemId) { this._ImageItemId = _ImageItemId; }

    public byte[] getDisplayImage() {
        return _DisplayImage;
    }

    public void setDisplayImage(byte[] displayImage) {
        _DisplayImage = displayImage;
    }

    public double getQuantity() {return _Quantity; }

    public void setQuantity(double _Quantity) {this._Quantity = _Quantity; }

    public String getSaleQuantity() {return _SaleQuantity;    }

    public void setSaleQuantity(String _SaleQuantity) { this._SaleQuantity = _SaleQuantity; }

    public String getSaleDiscount() {return _SaleDiscount; }

    public void setSaleDiscount(String _SaleDiscount) { this._SaleDiscount = _SaleDiscount; }

    public double getDiscountedPrice() { return _DiscountedPrice; }

    public void setDiscountedPrice(double _DiscountedPrice) { this._DiscountedPrice = _DiscountedPrice;  }

    public double getDiscountedAmount() {return _DiscountedAmount; }

    public void setDiscountedAmount(double _DiscountedAmount) {this._DiscountedAmount = _DiscountedAmount; }

    public double getDiscountAmount() {return _DiscountAmount; }

    public void setDiscountAmount(double _DiscountAmount) {this._DiscountAmount = _DiscountAmount; }

    public void ConvertJSON(JSONObject obj){
        try{
            this.setProductItemId(obj.getInt("ProductItemId"));
            this.setBarcode(obj.getString("Barcode"));
            this.setProductName(obj.getString("ProductName"));
            this.setSaleRate(obj.getDouble("SaleRate"));
            this.setImageItemId(obj.getInt("ImageItemId"));
            this.setOtherItemName(obj.getString("OtherItemName"));
            String image = obj.getString("DisplayImage");
            if(image != null && !image.equals("null") && !image.isEmpty()) {
                this.setDisplayImage(Base64.decode(image, Base64.DEFAULT));
            }
            this.setQuantity(obj.getDouble("Quantity"));
            this.setSaleQuantity(obj.getString("SaleQuantity"));
            this.setSaleDiscount(obj.getString("SaleDiscount"));
            this.setDiscountedPrice(obj.getDouble("DiscountedPrice"));
            this.setDiscountedAmount(obj.getDouble("DiscountedAmount"));
            this.setDiscountAmount(obj.getDouble("DiscountAmount"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
