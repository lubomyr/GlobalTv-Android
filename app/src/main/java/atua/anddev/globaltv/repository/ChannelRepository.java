package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.BaseApplication;
import atua.anddev.globaltv.database.greendao.ChannelDao;
import atua.anddev.globaltv.entity.Channel;

public class ChannelRepository {
    private static ChannelDao getDao() {
        return BaseApplication.getDaoSession().getChannelDao();
    }

    public static void saveAll(List<Channel> items) {
        ChannelDao dao = getDao();
        dao.insertOrReplaceInTx(items);
    }

    public static void insert(Channel item) {
        ChannelDao dao = getDao();
        dao.insertOrReplace(item);
    }

    public static void update(Channel item) {
        ChannelDao dao = getDao();
        dao.update(item);
    }

    public static void delete(Channel item) {
        ChannelDao dao = getDao();
        try {
            dao.delete(item);
        } catch (Exception ignored) {
        }
    }

    public static void deleteAll() {
        ChannelDao dao = getDao();
        dao.deleteAll();
    }

    public static List<Channel> getAll() {
        return getDao().loadAll();
    }

    public static String getUrlByPlistAndName(String plist, String name) {
        ChannelDao dao = getDao();
        return dao.queryBuilder().where(ChannelDao.Properties.Provider.eq(plist),
                ChannelDao.Properties.Name.eq(name)).unique().getUrl();
    }

    public static List<Channel> getChannelsByPlaylist(String plist) {
        ChannelDao dao = getDao();
        return dao.queryBuilder().where(ChannelDao.Properties.Provider.eq(plist)).list();
    }

    public static void deleteChannelsByPlaylist(String plist) {
        ChannelDao dao = getDao();
        dao.queryBuilder().where(ChannelDao.Properties.Provider.eq(plist)).buildDelete();
    }

    public static List<Channel> getChannelsByCategory(String plist, String catname) {
        ChannelDao dao = getDao();
        return dao.queryBuilder().where(ChannelDao.Properties.Provider.eq(plist),
                ChannelDao.Properties.Category.eq(catname)).list();
    }

    public static List<String> getCategoriesList(String plist) {
        ChannelDao dao = getDao();
        List<String> result = new ArrayList<>();
        List<Channel> chList = dao.queryBuilder().where(ChannelDao.Properties.Provider.eq(plist)).list();
        for (Channel c : chList)
            if (!result.contains(c.getCategory()) && !c.getCategory().isEmpty())
                result.add(c.getCategory());
        return result;
    }

    public static List<Channel> searchChannelsByName(String search) {
        ChannelDao dao = getDao();
        return dao.queryBuilder().where(ChannelDao.Properties.Name.like("%" + search + "%")).list();
    }

}
