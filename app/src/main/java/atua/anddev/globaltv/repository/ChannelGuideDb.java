package atua.anddev.globaltv.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.ChannelGuide;

@SuppressLint("Recycle")
public class ChannelGuideDb extends DBHelper {
    private static final String TABLE_NAME = "channelguide";
    private static final String COLUMN_CHANNEL_ID = "channel_id";
    private static final String COLUMN_NAME = "dispname";

    public ChannelGuideDb(Context context) {
        super(context);
    }

    public void insertAll(List<ChannelGuide> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        db.beginTransaction();
        try {
            for (ChannelGuide item : items) {
                cv.put(COLUMN_CHANNEL_ID, item.getId());
                cv.put(COLUMN_NAME, item.getDisplayName());

                db.insertOrThrow(TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ChannelGuide> getAll() {
        List<ChannelGuide> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String channelId = res.getString(res.getColumnIndex(COLUMN_CHANNEL_ID));
            String dispName = res.getString(res.getColumnIndex(COLUMN_NAME));
            ChannelGuide channel = new ChannelGuide(channelId, null, dispName);
            list.add(channel);
            res.moveToNext();
        }
        return list;
    }

    public String getIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_NAME + "='" +
                name + "'", null);
        res.moveToFirst();
        return res.getString(res.getColumnIndex(COLUMN_CHANNEL_ID));
    }

    public String getFirstId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        return res.getString(res.getColumnIndex(COLUMN_CHANNEL_ID));
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
}
