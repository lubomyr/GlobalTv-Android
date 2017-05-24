package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Favorite;
import io.realm.Realm;
import io.realm.RealmResults;

public class FavoriteRepository {

    public static void insert(Channel channel) {
        Realm realm = Realm.getDefaultInstance();
        Favorite item = new Favorite(channel.getName(), channel.getProvider());
        realm.beginTransaction();
        realm.copyToRealm(item);
        realm.commitTransaction();

    }

    public static void delete(Channel channel) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Favorite> result = realm.where(Favorite.class).equalTo("name", channel.getName())
                .equalTo("plist", channel.getProvider()).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Favorite.class);
        realm.commitTransaction();
    }

    public static List<Channel> getAll() {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Favorite> results = realm.where(Favorite.class).findAll();
        for (Favorite fav : results) {
            list.add(new Channel(fav.getName(), null, null, null,
                    fav.getPlist()));
        }
        return list;
    }
}
