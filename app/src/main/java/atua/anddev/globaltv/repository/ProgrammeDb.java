package atua.anddev.globaltv.repository;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import atua.anddev.globaltv.entity.Programme;
import atua.anddev.globaltv.utils.ProgressDialogUtils;

@SuppressLint("Recycle")
public class ProgrammeDb extends DBHelper {
    private static final String TABLE_NAME = "PROGRAMME";
    private static final String COLUMN_START = "start";
    private static final String COLUMN_STOP = "stop";
    private static final String COLUMN_CHANNEL = "channel";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESC = "desc";
    private static final String COLUMN_CATEGORY = "category";
    private ProgressDialog progressDialog;

    public ProgrammeDb(Context context) {
        super(context);
    }

    public void insertAll(final Activity activity, final List<Programme> items) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = ProgressDialogUtils.showProgressDialog(activity,
                        "Processing ChannelGuide...", items.size());
            }
        });
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        db.beginTransaction();
        try {
            for (Programme item : items) {
                cv.put(COLUMN_START, item.getStart());
                cv.put(COLUMN_STOP, item.getStop());
                cv.put(COLUMN_CHANNEL, item.getChannel());
                cv.put(COLUMN_TITLE, item.getTitle());
                cv.put(COLUMN_DESC, item.getDesc());
                cv.put(COLUMN_CATEGORY, item.getCategory());
                db.insertOrThrow(TABLE_NAME, null, cv);

                count++;
                ProgressDialogUtils.setProgress(activity, progressDialog, count);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            ProgressDialogUtils.closeProgress(activity, progressDialog);
        }
    }

    public List<Programme> getAll() {
        List<Programme> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String start = res.getString(res.getColumnIndex(COLUMN_START));
            String stop = res.getString(res.getColumnIndex(COLUMN_STOP));
            String channel = res.getString(res.getColumnIndex(COLUMN_CHANNEL));
            String title = res.getString(res.getColumnIndex(COLUMN_TITLE));
            String desc = res.getString(res.getColumnIndex(COLUMN_DESC));
            String category = res.getString(res.getColumnIndex(COLUMN_CATEGORY));
            Programme programme = new Programme(start, stop, channel, title, desc, category);
            result.add(programme);
            res.moveToNext();
        }
        return result;
    }

    public List<Programme> getAllProgramsByChannel(String str) {
        List<Programme> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_CHANNEL +
                "='" + str + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String start = res.getString(res.getColumnIndex(COLUMN_START));
            String stop = res.getString(res.getColumnIndex(COLUMN_STOP));
            String channel = res.getString(res.getColumnIndex(COLUMN_CHANNEL));
            String title = res.getString(res.getColumnIndex(COLUMN_TITLE));
            String desc = res.getString(res.getColumnIndex(COLUMN_DESC));
            String category = res.getString(res.getColumnIndex(COLUMN_CATEGORY));
            Programme programme = new Programme(start, stop, channel, title, desc, category);
            result.add(programme);
            res.moveToNext();
        }
        return result;
    }

    @SuppressLint("SimpleDateFormat")
    public Programme getCurrentProgramByChannel(String str) {
        final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss Z");
        Calendar currentTime = Calendar.getInstance();
        Programme result = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_CHANNEL +
                "='" + str + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            String start = res.getString(res.getColumnIndex(COLUMN_START));
            String stop = res.getString(res.getColumnIndex(COLUMN_STOP));
            String channel = res.getString(res.getColumnIndex(COLUMN_CHANNEL));
            String title = res.getString(res.getColumnIndex(COLUMN_TITLE));
            String desc = res.getString(res.getColumnIndex(COLUMN_DESC));
            String category = res.getString(res.getColumnIndex(COLUMN_CATEGORY));
            Calendar startDate = decodeDateTime(start);
            Calendar stopDate = decodeDateTime(stop);
            if (currentTime.after(startDate) && currentTime.before(stopDate)) {
                result = new Programme(start, stop, channel, title, desc, category);
            }
            res.moveToNext();
        }
        return result;
    }

    public int getCurrentPosByChannel(String str) {
        Calendar currentTime = Calendar.getInstance();
        int result = -1, n = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_CHANNEL +
                "='" + str + "'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String start = res.getString(res.getColumnIndex(COLUMN_START));
            String stop = res.getString(res.getColumnIndex(COLUMN_STOP));
            Calendar startDate = decodeDateTime(start);
            Calendar stopDate = decodeDateTime(stop);
            if (currentTime.after(startDate) && currentTime.before(stopDate)) {
                result = n;
            }
            n++;
            res.moveToNext();
        }
        return result;
    }

    public List<Programme> getProgramsByNameFullPeriod(String search) {
        List<Programme> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_TITLE +
                " like '%" + search + "%'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String start = res.getString(res.getColumnIndex(COLUMN_START));
            String stop = res.getString(res.getColumnIndex(COLUMN_STOP));
            String channel = res.getString(res.getColumnIndex(COLUMN_CHANNEL));
            String title = res.getString(res.getColumnIndex(COLUMN_TITLE));
            String desc = res.getString(res.getColumnIndex(COLUMN_DESC));
            String category = res.getString(res.getColumnIndex(COLUMN_CATEGORY));
            Programme programme = new Programme(start, stop, channel, title, desc, category);
            result.add(programme);
            res.moveToNext();
        }
        return result;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    @SuppressLint("SimpleDateFormat")
    private Calendar decodeDateTime(String str) {
        final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss Z");
        Calendar result = Calendar.getInstance();
        try {
            if (!str.isEmpty())
                result.setTime(sdf.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
