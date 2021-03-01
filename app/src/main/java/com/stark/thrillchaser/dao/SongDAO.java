package com.stark.thrillchaser.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SongDAO extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public SongDAO(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "favorite_song.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table favorite_song ( id integer primary key);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists favorite_song ");
        onCreate(sqLiteDatabase);
    }

    public void setFavorite(int id){
        db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",id);;
        db.insert("favorite_song",null,contentValues);

    }

    public boolean deleteFavorite(int id){
        db=getWritableDatabase();
        db.delete("favorite_song","id=?",new String[]{String.valueOf(id)});

        return true;
    }

    public boolean isFavorite(int id){
        db=getReadableDatabase();

        Cursor cursor=db.rawQuery("select * from favorite_song where id=?",new String[]{String.valueOf(id)},null);

        cursor.moveToFirst();
        if(cursor.getCount() <=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
