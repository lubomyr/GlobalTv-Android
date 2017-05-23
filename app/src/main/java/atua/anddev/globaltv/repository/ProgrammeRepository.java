package atua.anddev.globaltv.repository;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import atua.anddev.globaltv.BaseApplication;
import atua.anddev.globaltv.database.greendao.ProgrammeDao;
import atua.anddev.globaltv.entity.Programme;

public class ProgrammeRepository {
    private static ProgrammeDao getDao() {
        return BaseApplication.getDaoSession().getProgrammeDao();
    }

    public static void saveAll(List<Programme> items) {
        ProgrammeDao dao = getDao();
        for (Programme item : items) {
            dao.insertOrReplace(item);
        }
    }

    public static void insert(Programme item) {
        ProgrammeDao dao = getDao();
        dao.insertOrReplace(item);
    }

    public static void update(Programme item) {
        ProgrammeDao dao = getDao();
        dao.update(item);
    }

    public static void delete(Programme item) {
        ProgrammeDao dao = getDao();
        dao.delete(item);
    }

    public static void deleteAll() {
        ProgrammeDao dao = getDao();
        dao.deleteAll();
    }

    public static List<Programme> getAll() {
        return getDao().loadAll();
    }

    public static List<Programme> getAllProgramsByChannel(String chId) {
        return getDao().queryBuilder().where(ProgrammeDao.Properties.Channel.eq(chId)).list();
    }

    public static Programme getCurrentProgramByChannel(String chId) {
        Calendar currentTime = Calendar.getInstance();
        List<Programme> list = getDao().queryBuilder().where(ProgrammeDao.Properties.Channel.eq(chId)).list();
        for (Programme p : list) {
            Calendar startDate = decodeDateTime(p.getStart());
            Calendar stopDate = decodeDateTime(p.getStop());
            if (currentTime.after(startDate) && currentTime.before(stopDate)) {
                return p;
            }
        }
        return null;
    }

    public static int getCurrentPosByChannel(String chId) {
        Calendar currentTime = Calendar.getInstance();
        int result = -1, n = 0;
        List<Programme> list = getDao().queryBuilder().where(ProgrammeDao.Properties.Channel.eq(chId)).list();
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
        return getDao().queryBuilder().where(ProgrammeDao.Properties.Title.like("%" + search + "%")).list();
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
