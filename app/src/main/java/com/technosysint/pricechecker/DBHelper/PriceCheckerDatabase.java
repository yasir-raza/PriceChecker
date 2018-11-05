package com.technosysint.pricechecker.DBHelper;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Yasir.Raza on 10/23/2018.
 */
@Database(entities = {User.class, PriceCheckerProduct.class,}, version = 2)
public abstract class PriceCheckerDatabase extends RoomDatabase {
    private static PriceCheckerDatabase INSTANCE = null;

    public abstract UserDao userDao();

    public abstract PriceCheckerProductDao PriceCheckerProductsDao();

    public static PriceCheckerDatabase getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PriceCheckerDatabase.class, "technosys_feedback")
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public static void DestroyInstance() {
        INSTANCE = null;
    }
}
