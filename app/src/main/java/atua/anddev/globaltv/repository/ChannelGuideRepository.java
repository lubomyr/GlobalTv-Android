package atua.anddev.globaltv.repository;

import java.util.List;

import atua.anddev.globaltv.BaseApplication;
import atua.anddev.globaltv.database.greendao.ChannelGuideDao;
import atua.anddev.globaltv.entity.ChannelGuide;

public class ChannelGuideRepository {
    private static ChannelGuideDao getDao() {
        return BaseApplication.getDaoSession().getChannelGuideDao();
    }

    public static void saveAll(List<ChannelGuide> items) {
        ChannelGuideDao dao = getDao();
        for (ChannelGuide item : items) {
            dao.insertOrReplace(item);
        }
    }

    public static void insert(ChannelGuide item) {
        ChannelGuideDao dao = getDao();
        dao.insertOrReplace(item);
    }

    public static void update(ChannelGuide item) {
        ChannelGuideDao dao = getDao();
        dao.update(item);
    }

    public static void delete(ChannelGuide item) {
        ChannelGuideDao dao = getDao();
        try {
            dao.delete(item);
        } catch (Exception ignored) {
        }
    }

    public static void deleteAll() {
        ChannelGuideDao dao = getDao();
        dao.deleteAll();
    }

    public static List<ChannelGuide> getAll() {
        return getDao().loadAll();
    }

    public static String getFirstId() {
        return getDao().queryBuilder().limit(1).unique().getId();
    }

    public static String getIdByName(String name) {
        return getDao().queryBuilder().where(ChannelGuideDao.Properties.DisplayName.eq(name)).limit(1).unique().getId();
    }

}
