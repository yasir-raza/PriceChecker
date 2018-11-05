package com.technosysint.pricechecker.DBHelper;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Yasir.Raza on 10/26/2018.
 */
@Dao
public interface PriceCheckerProductDao {
    @Query("SELECT * FROM PriceCheckerProduct ORDER BY _ProductItemId")
    List<PriceCheckerProduct> getAll();

    @Query("SELECT * FROM PriceCheckerProduct WHERE _ProductItemId = :fbQId")
    List<PriceCheckerProduct> findById(int fbQId);

    @Query("SELECT _ProductItemId FROM PriceCheckerProduct ORDER BY _ProductItemId LIMIT 1")
    int checkRecordExist();

    @Query("Update PriceCheckerProduct set _DisplayImage = :DisplayImage where _ImageItemId = :ImageItemId")
    void updateDisplayImage(byte[] DisplayImage, int ImageItemId);

    @Insert()
    void insertProducts(PriceCheckerProduct priceCheckerProduct);

    @Update
    void updateProducts(PriceCheckerProduct priceCheckerProduct);

    @Delete
    void deleteProducts(PriceCheckerProduct priceCheckerProduct);

    @Query("DELETE FROM PriceCheckerProduct")
    void deleteAll();
}
