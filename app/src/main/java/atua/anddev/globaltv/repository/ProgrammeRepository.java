package atua.anddev.globaltv.repository;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import atua.anddev.globaltv.entity.Programme;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class ProgrammeRepository {

    public static void saveAll(List<Programme> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
        realm.close();
    }

    public static void insert(Programme item) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(item);
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Programme.class);
        realm.commitTransaction();
        realm.close();
    }

    public static List<Programme> getAll() {
        Realm realm = Realm.getDefaultInstance();
        List<Programme> list = new ArrayList<>();
        RealmResults<Programme> results = realm.where(Programme.class).findAll();
        list.addAll(results);
        return list;
    }

    public static List<Programme> getAllProgramsByChannel(String chId) {
        Realm realm = Realm.getDefaultInstance();
        List<Programme> list = new ArrayList<>();
        RealmResults<Programme> results = realm.where(Programme.class).equalTo("channel", chId).findAll();
        list.addAll(results);
        return list;
    }

    public static Programme getCurrentProgramByChannel(String chId) {
        Realm realm = Realm.getDefaultInstance();
        Calendar currentTime = Calendar.getInstance();
        List<Programme> list = new ArrayList<>();
        RealmResults<Programme> results = realm.where(Programme.class).equalTo("channel", chId).findAll();
        for (Programme p : results) {
            Calendar startDate = decodeDateTime(p.getStart());
            Calendar stopDate = decodeDateTime(p.getStop());
            if (currentTime.after(startDate) && currentTime.before(stopDate)) {
                return p;
            }
        }
        return null;
    }

    public static int getCurrentPosByChannel(String chId) {
        Realm realm = Realm.getDefaultInstance();
        Calendar currentTime = Calendar.getInstance();
        int result = -1, n = 0;
        List<Programme> list = new ArrayList<>();
        RealmResults<Programme> results = realm.where(Programme.class).equalTo("channel", chId).findAll();
        list.addAll(results);
        for (Programme p : list) {
            Calendar startDate = decodeDateTime(p.getStart());
            Calendar stopDate = decodeDateTime(p.getStop());
            if (currentTime.after(startDate) && currentTime.before(stopDate)) {
                result = n;
            }
            n++;
        }
        return result;
    }

    public static List<Programme> getProgramsByNameFullPeriod(String search) {
        Realm realm = Realm.getDefaultInstance();
        List<Programme> list = new ArrayList<>();
        RealmResults<Programme> results = realm.where(Programme.class).like("title", "*" + search + "*",
                Case.INSENSITIVE).findAll();
        list.addAll(results);
        return list;
    }

    @SuppressLint("SimpleDateFormat")
    private static Calendar decodeDateTime(String str) {
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
