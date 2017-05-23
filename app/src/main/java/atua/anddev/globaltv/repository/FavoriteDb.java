package atua.anddev.globaltv.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

@SuppressLint("Recycle")
public class FavoriteDb extends DBHelper {
    private static final String TABLE_NAME = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PLIST = "plist";

    public FavoriteDb(Context context) {
        super(context);
    }

    public boolean insertIntoFavorites(Channel channel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, channel.getName());
        contentValues.put(COLUMN_PLIST, channel.getProvider());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Integer deleteFromFavoritesById(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("favorites",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public List<Channel> getAllFavorites() {
        List<Channel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            list.add(new Channel(res.getString(res.getColumnIndex(COLUMN_NAME)), null, null, null,
                    res.getString(res.getColumnIndex(COLUMN_PLIST))));
            res.moveToNext();
        }
        return list;
    }

    public List<Integer> getAllFavoritesID() {
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            list.add(res.getInt(res.getColumnIndex(COLUMN_ID)));
            res.moveToNext();
        }
        return list;
    }

    public Channel getFavoriteById(int id) {
        Channel favorites;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where id = " + id, null);
        res.moveToFirst();
        favorites = new Channel(res.getString(res.getColumnIndex(COLUMN_NAME)), null, null, null,
                res.getString(res.getColumnIndex(COLUMN_PLIST)));
        return favorites;
    }

    public void deleteAllFavorites() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }


}
