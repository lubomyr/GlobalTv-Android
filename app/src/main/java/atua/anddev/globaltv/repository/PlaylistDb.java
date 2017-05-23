package atua.anddev.globaltv.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Playlist;

@SuppressLint("Recycle")
public class PlaylistDb extends DBHelper {
    private static final String TABLE_NAME = "playlists";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_FILE = "file";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_MD5 = "md5";
    private static final String COLUMN_UPDATED = "updated";

    public PlaylistDb(Context context) {
        super(context);
    }

    public boolean insertPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, playlist.getName());
        contentValues.put(COLUMN_URL, playlist.getUrl());
        contentValues.put(COLUMN_FILE, playlist.getFile());
        contentValues.put(COLUMN_TYPE, playlist.getType());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public boolean updatePlaylist(Integer id, Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", playlist.getName());
        contentValues.put("url", playlist.getUrl());
        contentValues.put("file", playlist.getFile());
        contentValues.put("type", playlist.getType());
        db.update("playlists", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deletePlaylist(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("playlists",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public List<Playlist> getPlaylists() {
        List<Playlist> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            list.add(new Playlist(res.getString(res.getColumnIndex(COLUMN_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_URL)),
                    res.getString(res.getColumnIndex(COLUMN_FILE)),
                    res.getInt(res.getColumnIndex(COLUMN_TYPE)),
                    res.getString(res.getColumnIndex(COLUMN_MD5)),
                    res.getString(res.getColumnIndex(COLUMN_UPDATED))));
            res.moveToNext();
        }
        return list;
    }

    public Playlist getPlaylistById(Integer id) {
        Playlist playlist;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where id = " + id, null);
        res.moveToFirst();
        playlist = new Playlist(res.getString(res.getColumnIndex(COLUMN_NAME)),
                res.getString(res.getColumnIndex(COLUMN_URL)),
                res.getString(res.getColumnIndex(COLUMN_FILE)),
                res.getInt(res.getColumnIndex(COLUMN_TYPE)),
                res.getString(res.getColumnIndex(COLUMN_MD5)),
                res.getString(res.getColumnIndex(COLUMN_UPDATED)));
        return playlist;
    }

    public List<Integer> getPlaylistId() {
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

    public List<String> getPlaylistNames() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            list.add(res.getString(res.getColumnIndex(COLUMN_NAME)));
            res.moveToNext();
        }
        return list;
    }

    public void deleteAllPlaylists() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public boolean setPlaylistMd5ById(Integer id, String md5) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("md5", md5);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean setPlaylistUpdatedById(Integer id, long updated) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("updated", updated);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public List<Playlist> getSortedByDatePlaylists() {
        List<Playlist> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " order by updated desc", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            list.add(new Playlist(res.getString(res.getColumnIndex(COLUMN_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_URL)),
                    res.getString(res.getColumnIndex(COLUMN_FILE)),
                    res.getInt(res.getColumnIndex(COLUMN_TYPE)),
                    res.getString(res.getColumnIndex(COLUMN_MD5)),
                    res.getString(res.getColumnIndex(COLUMN_UPDATED))));
            res.moveToNext();
        }
        return list;
    }
}
