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
public class ChannelDb extends DBHelper {
    private static final String TABLE_NAME = "channels";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_GROUP = "category";
    private static final String COLUMN_ICON = "icon";
    private static final String COLUMN_PLIST = "plist";

    public ChannelDb(Context context) {
        super(context);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Integer deleteChannelbyPlist(String plist) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "plist = ? ",
                new String[]{plist});
    }

    public Channel getChannelById(int id) {
        Channel channel;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where id = " + id, null);
        res.moveToFirst();
        channel = new Channel(res.getString(res.getColumnIndex(COLUMN_NAME)),
                res.getString(res.getColumnIndex(COLUMN_URL)),
                res.getString(res.getColumnIndex(COLUMN_GROUP)),
                res.getString(res.getColumnIndex(COLUMN_ICON)),
                res.getString(res.getColumnIndex(COLUMN_PLIST)));
        return channel;
    }

    public List<Channel> getChannelsByPlist(String plist) {
        List<Channel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where plist = '" + plist + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            String name = res.getString(res.getColumnIndex(COLUMN_NAME));
            String url = res.getString(res.getColumnIndex(COLUMN_URL));
            String group = res.getString(res.getColumnIndex(COLUMN_GROUP));
            String icon = res.getString(res.getColumnIndex(COLUMN_ICON));
            Channel channel = new Channel(name, url, group, icon, plist);
            list.add(channel);
            res.moveToNext();
        }
        return list;
    }

    public List<Channel> getChannelsByCategory(String plist, String category) {
        List<Channel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where plist = '" + plist +
                "' and category = '" + category + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            String name = res.getString(res.getColumnIndex(COLUMN_NAME));
            String url = res.getString(res.getColumnIndex(COLUMN_URL));
            String group = res.getString(res.getColumnIndex(COLUMN_GROUP));
            String icon = res.getString(res.getColumnIndex(COLUMN_ICON));
            Channel channel = new Channel(name, url, group, icon, plist);
            list.add(channel);
            res.moveToNext();
        }
        return list;
    }

    public String getChannelsUrlByPlistAndName(String plist, String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where plist = '" + plist +
                "' and name ='" + name + "'", null);
        res.moveToFirst();

        return res.getString(res.getColumnIndex(COLUMN_URL));
    }

    public List<Channel> searchChannelsByName(String search) {
        List<Channel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where name like '%" + search + "%'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            String name = res.getString(res.getColumnIndex(COLUMN_NAME));
            String url = res.getString(res.getColumnIndex(COLUMN_URL));
            String group = res.getString(res.getColumnIndex(COLUMN_GROUP));
            String icon = res.getString(res.getColumnIndex(COLUMN_ICON));
            String plist = res.getString(res.getColumnIndex(COLUMN_PLIST));
            Channel channel = new Channel(name, url, group, icon, plist);
            list.add(channel);
            res.moveToNext();
        }
        return list;
    }

    public void deleteAllChannels() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public List<String> getCategoriesList(String plist) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select distinct category from " + TABLE_NAME +
                " where plist = '" + plist + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            String group = res.getString(res.getColumnIndex(COLUMN_GROUP));
            if (!group.isEmpty())
                list.add(group);
            res.moveToNext();
        }
        return list;
    }

    public List<Integer> getAllChannelId() {
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

    public void insertAllChannels(List<Channel> channels) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        db.beginTransaction();

        try {
            for (Channel s : channels) {
                cv.put(COLUMN_NAME, s.getName());
                cv.put(COLUMN_URL, s.getUrl());
                cv.put(COLUMN_GROUP, s.getCategory());
                cv.put(COLUMN_ICON, s.getIcon());
                cv.put(COLUMN_PLIST, s.getProvider());

                db.insertOrThrow(TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
