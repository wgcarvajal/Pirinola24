package pirinola24.com.pirinola24.basededatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by geovanny on 11/01/16.
 */
public class AdminSQliteOpenHelper extends SQLiteOpenHelper
{
    public AdminSQliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table pedido(prodid text primary key, prodcantidad integer, prodprecio integer, prodnombre text, proddescripcion text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists pedido");
        db.execSQL("create table pedido(prodid text primary key, prodcantidad integer, prodprecio integer, prodnombre text, proddescripcion text )");
    }
}
