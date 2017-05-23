package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.BaseApplication;
import atua.anddev.globaltv.database.greendao.PlaylistDao;
import atua.anddev.globaltv.entity.Playlist;

public class PlaylistRepository {
    private static PlaylistDao getDao() {
        return BaseApplication.getDaoSession().getPlaylistDao();
    }

    public static void saveAll(List<Playlist> items) {
        PlaylistDao dao = getDao();
        for (Playlist item : items) {
            dao.insertOrReplace(item);
        }
    }

    public static void insert(Playlist item) {
        PlaylistDao dao = getDao();
        dao.insertOrReplace(item);
    }

    public static void update(Playlist item) {
        PlaylistDao dao = getDao();
        dao.update(item);
    }

    public static void delete(Playlist item) {
        PlaylistDao dao = getDao();
        try {
            dao.delete(item);
        } catch (Exception ignored) {
        }
    }

    public static void deleteAll() {
        PlaylistDao dao = getDao();
        dao.deleteAll();
    }

    public static List<Playlist> getAll() {
        return getDao().loadAll();
    }

    public static List<Playlist> getSortedByDate() {
        return getDao().queryBuilder().orderDesc(PlaylistDao.Properties.Update).list();
    }

    public static List<String> getNames() {
        List<String> list = new ArrayList<>();
        List<Playlist> plist = getDao().loadAll();
        for (Playlist p : plist)
            list.add(p.getName());
        return list;
    }

}
