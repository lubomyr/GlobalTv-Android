package atua.anddev.globaltv.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GlobalTV.db";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table channels " +
                        "(id integer primary key, name text, url text, category text, icon, text, plist text)"
        );

        db.execSQL(
                "create table favorites " +
                        "(id integer primary key, name text, plist text)"
        );

        db.execSQL(
                "create table playlists " +
                        "(id integer primary key, name text, url text, file text, type integer, md5 text, updated date)"
        );

        db.execSQL(
                "create table channelguide " +
                        "(id integer primary key, channel_id text, dispname text)"
        );

        db.execSQL(
                "create table programme " +
                        "(id integer primary key, start text, stop text, channel text, title text, desc text, category text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS channels");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS playlists");
        db.execSQL("DROP TABLE IF EXISTS channelguide");
        db.execSQL("DROP TABLE IF EXISTS programme");
        onCreate(db);
    }

}
