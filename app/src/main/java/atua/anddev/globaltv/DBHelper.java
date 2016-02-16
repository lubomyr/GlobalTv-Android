package atua.anddev.globaltv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lubomyr on 15.02.16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GlobalTV.db";
    public static final String CHANNELS_TABLE_NAME = "channels";
    public static final String CHANNELS_COLUMN_ID = "id";
    public static final String CHANNELS_COLUMN_NAME = "name";
    public static final String CHANNELS_COLUMN_URL = "url";
    public static final String CHANNELS_COLUMN_GROUP = "category";
    public static final String CHANNELS_COLUMN_PLIST = "plist";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table channels " +
                        "(id integer primary key, name text,url text,category text, plist text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS channels");
        onCreate(db);
    }

    public boolean insertChannel(String name, String url, String category, String plist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", url);
        contentValues.put("category", category);
        contentValues.put("plist", plist);
        db.insert("channels", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from channels where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CHANNELS_TABLE_NAME);
        return numRows;
    }

    public boolean updateChannel(Integer id, String name, String url, String category, String plist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("url", url);
        contentValues.put("category", category);
        contentValues.put("plist", plist);
        db.update("channels", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteChannel(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("channels",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteChannelbyPlist(String plist) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("channels",
                "plist = ? ",
                new String[]{plist});
    }

    public ArrayList<String> getAllChannels() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from channels", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(CHANNELS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
