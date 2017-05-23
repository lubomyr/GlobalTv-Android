package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.BaseApplication;
import atua.anddev.globaltv.database.greendao.FavoriteDao;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Favorite;

public class FavoriteRepository {
    private static FavoriteDao getDao() {
        return BaseApplication.getDaoSession().getFavoriteDao();
    }

    public static void saveAll(List<Favorite> items) {
        FavoriteDao dao = getDao();
        for (Favorite item : items) {
            dao.insertOrReplace(item);
        }
    }

    public static void insert(Channel channel) {
        Favorite item = new Favorite(channel.getName(), channel.getProvider());
        FavoriteDao dao = getDao();
        dao.insertOrReplace(item);
    }

    public static void delete(Channel channel) {
        FavoriteDao dao = getDao();
        Favorite item = getDao().queryBuilder().where(FavoriteDao.Properties.Name.eq(channel.getName()),
                FavoriteDao.Properties.Plist.eq(channel.getProvider())).unique();

        dao.delete(item);
    }

    public static void deleteAll() {
        FavoriteDao dao = getDao();
        dao.deleteAll();
    }

    public static List<Channel> getAll() {
        List<Channel> list = new ArrayList<>();
        List<Favorite> favoriteList = getDao().loadAll();
        for (Favorite fav : favoriteList) {
            list.add(new Channel(fav.getName(), null, null, null,
                    fav.getPlist()));
        }
        return list;
    }
}
